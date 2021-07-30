package com.holdbetter.stonks.utility;

import android.util.Log;

import com.google.gson.Gson;
import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.viewmodel.StocksViewModel;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class StockCache extends BaseCaching<List<StockData>> {
    private static final String cacheFileName = "stocks.txt";
    private static StockCache instance;

    private StockCache() {
    }

    public static StockCache getInstance() {
        if (instance == null) {
            instance = new StockCache();
        }
        return instance;
    }

    @Override
    void writeDataInCacheFile(File cache, List<StockData> stockData) {
        try (PrintWriter writer = new PrintWriter(cache)) {
            writer.write(new Gson().toJson(stockData));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Nullable
    List<StockData> readDataInCacheFile(File cache) {
        StockData[] data = null;
        try (FileReader reader = new FileReader(cache)) {
            data = new Gson().fromJson(reader, StockData[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data != null ? new ArrayList<>(Arrays.asList(data)) : null;
    }

    @Override
    public Observable<List<StockData>> getMemoryCache(StocksViewModel stocksViewModel) {
        return Observable.create(emitter -> {
            Log.d("STOCKS_MEMORY_READING", Thread.currentThread().getName());
            if (stocksViewModel.getStockData() != null) {
                emitter.onNext(stocksViewModel.getStockData());
            }
            emitter.onComplete();
        });
    }

    @Override
    public Observable<List<StockData>> getDiskCache(File cacheFolder) {
        return Observable.create(emitter -> {
            Log.d("STOCKS_DISK_READING", Thread.currentThread().getName());
            File cache = new File(cacheFolder, getCacheFileName());
            List<StockData> stockData = null;
            if (cache.exists() && (stockData = readDataInCacheFile(cache)) != null) {
                emitter.onNext(stockData);
            }
            emitter.onComplete();
        });
    }

    @Override
    public void cache(List<StockData> stockData, StocksViewModel viewModel, File diskCacheDir) {
        Log.d("STOCKS_CACHING", Thread.currentThread().getName());

        // cache in ViewModel
        viewModel.setStockData(stockData);

        // cache on disk
        File cache = new File(diskCacheDir, getCacheFileName());
        if (!cache.exists()) {
            writeDataInCacheFile(cache, stockData);
        } else {
            if (cache.delete()) {
                writeDataInCacheFile(cache, stockData);
            } else {
                Log.d("CACHE_STATE", "Cache (Stocks) wasn't deleted and written");
            }
        }

        Log.d("STOCKS_CACHING", "COMPLETE");
    }

    @Override
    public String getCacheFileName() {
        return cacheFileName;
    }
}
