package com.holdbetter.stonks.utility;

import android.util.Log;

import com.google.gson.Gson;
import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.viewmodel.StocksViewModel;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import io.reactivex.rxjava3.core.Observable;

public class ConstituentsCache extends BaseCaching<Indice> {
    private static final String cacheFileName = "constituents.txt";
    private static ConstituentsCache instance = null;
    private final File cache;

    private ConstituentsCache(File cache) {
        this.cache = cache;
    }

    public static ConstituentsCache getInstance(File cacheDirectory) {
        if (instance == null) {
            instance = new ConstituentsCache(new File(cacheDirectory, cacheFileName));
        }
        return instance;
    }

    @Override
    public void cache(Indice freshIndiceData, StocksViewModel viewModel) {
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
        if (!cache.exists()) {
            writeDataInCacheFile(freshIndiceData);
        } else {
            Indice cachedIndice = null;
            try {
                cachedIndice = new Gson().fromJson(new FileReader(cache), Indice.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Not sure about this condition
            if (cachedIndice != null && cachedIndice.getExpiresTime() <= freshIndiceData.getLastUpdateTime()) {
                writeDataInCacheFile(freshIndiceData);
            }
        }

        Log.d("INDICE_CACHING", "COMPLETE");
    }

    @Override
    void writeDataInCacheFile(Indice indice) {
        if (cache.exists()) {
            cache.delete();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(cache, false))) {
            writer.write(new Gson().toJson(indice, Indice.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Nullable
    Indice readDataInCacheFile() {
        Indice indice = null;
        try (FileReader reader = new FileReader(cache)) {
            indice = new Gson().fromJson(reader, Indice.class);
        } catch (IOException e) {
            e.printStackTrace();
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
    public Observable<Indice> getDiskCache() {
        return Observable.create(emitter -> {
            Log.d("INDICE_DISK_READING", Thread.currentThread().getName());
            Indice indice = null;
            if (cache.exists() && (indice = readDataInCacheFile()) != null) {
                emitter.onNext(indice);
            }
            emitter.onComplete();
        });
    }
}
