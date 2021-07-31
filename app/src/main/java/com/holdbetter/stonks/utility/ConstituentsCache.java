package com.holdbetter.stonks.utility;

import android.util.Log;

import com.google.gson.Gson;
import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.viewmodel.StocksViewModel;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.BufferOverflowException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class ConstituentsCache extends BaseCaching<Indice> {
    private static final String cacheFileName = "constituents.txt";
    private static ConstituentsCache instance = null;

    private ConstituentsCache() {
    }

    public static ConstituentsCache getInstance() {
        if (instance == null) {
            instance = new ConstituentsCache();
        }
        return instance;
    }

    @Override
    public void cache(Indice freshIndiceData, StocksViewModel viewModel, File diskCacheDir) {
        Log.d("INDICE_CACHING", Thread.currentThread().getName());

        // exchange walmart (logo not accesible) for yandex
        for (int i = 0; i < freshIndiceData.getConstituents().length; i++) {
            if (freshIndiceData.getConstituents()[i].equals("WMT")) {
                freshIndiceData.getConstituents()[i] = "YNDX";
                break;
            }
        }

        // cache in ViewModel
        viewModel.setDowJonesIndice(freshIndiceData);

        // cache on disk
        File cache = new File(diskCacheDir, getCacheFileName());
        if (!cache.exists()) {
            writeDataInCacheFile(cache, freshIndiceData);
        } else {
            Indice cachedIndice = null;
            try {
                cachedIndice = new Gson().fromJson(new FileReader(cache), Indice.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Not sure about this condition
            if (cachedIndice != null && cachedIndice.getExpiresTime() <= freshIndiceData.getLastUpdateTime()) {
                if (cache.delete()) {
                    writeDataInCacheFile(cache, freshIndiceData);
                } else {
                    Log.d("CACHE_STATE", "Cache (Indice) wasn't deleted and written");
                }
            }
        }

        Log.d("INDICE_CACHING", "COMPLETE");
    }

    @Override
    void writeDataInCacheFile(File cache, Indice indice) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(cache, false))) {
            writer.write(new Gson().toJson(indice, Indice.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Nullable
    Indice readDataInCacheFile(File cache) {
        Indice indice = null;
        if (cache.exists()) {
            try (FileReader reader = new FileReader(cache)) {
                indice = new Gson().fromJson(reader, Indice.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return indice;
    }

    @Override
    public Observable<Indice> getMemoryCache(StocksViewModel stocksViewModel) {
        return Observable.create(emitter -> {
            Log.d("INDICE_MEMORY_READING", Thread.currentThread().getName());
            if (stocksViewModel.getDowJones() != null) {
                emitter.onNext(stocksViewModel.getDowJones());
            }
            emitter.onComplete();
        });
    }

    @Override
    public Observable<Indice> getDiskCache(File cacheFolder) {
        return Observable.create(emitter -> {
            Log.d("INDICE_DISK_READING", Thread.currentThread().getName());
            File fileCache = new File(cacheFolder, getCacheFileName());
            Indice indice = null;
            if (fileCache.exists() && (indice = readDataInCacheFile(fileCache)) != null) {
                emitter.onNext(indice);
            }
            emitter.onComplete();
        });
    }

    @Override
    public String getCacheFileName() {
        return cacheFileName;
    }
}
