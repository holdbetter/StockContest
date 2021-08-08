package com.holdbetter.stonks.utility;

import android.util.Log;

import com.google.gson.Gson;
import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.model.room.SymbolEntity;
import com.holdbetter.stonks.viewmodel.StocksViewModel;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import io.reactivex.rxjava3.core.Observable;

public class StockCache extends BaseCaching<List<StockData>> {
    private static final String cacheFileName = "stocks.txt";
    private static StockCache instance;
    private final File cache;

    private StockCache(File cache) {
        this.cache = cache;
    }

    public static StockCache getInstance(File cacheDirectory) {
        if (instance == null) {
            instance = new StockCache(new File(cacheDirectory, cacheFileName));
        }
        return instance;
    }

    @Override
    void writeDataInCacheFile(List<StockData> stockData) {
        if (cache.exists()) {
            cache.delete();
        }

        try (PrintWriter writer = new PrintWriter(cache)) {
            writer.write(new Gson().toJson(stockData));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Nullable
    List<StockData> readDataInCacheFile() {
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
    public Observable<List<StockData>> getDiskCache() {
        return Observable.create(emitter -> {
            Log.d("STOCKS_DISK_READING", Thread.currentThread().getName());
            List<StockData> stockData;
            if (cache.exists() && (stockData = readDataInCacheFile()) != null) {
                emitter.onNext(stockData);
            }
            emitter.onComplete();
        });
    }

    @Override
    public void cacheAll(List<StockData> stockData, StocksViewModel viewModel) {
        Log.d("STOCKS_CACHING", Thread.currentThread().getName());
        cacheOnMemory(stockData, viewModel);
        cacheOnDisk(stockData);
        Log.d("STOCKS_CACHING", "COMPLETE");
    }

    @Override
    public void cacheOnMemory(List<StockData> stockData, StocksViewModel viewModel) {
        // cache in ViewModel
        viewModel.setStockData(stockData);

        SymbolEntity[] symbols = stockData.stream().flatMap((Function<StockData, Stream<SymbolEntity>>) stockData1 -> Stream.of(new SymbolEntity(stockData1.getSymbol()))).toArray(SymbolEntity[]::new);
        viewModel.getDatabase().getFavouriteDao().insertSymbols(symbols);
    }

    @Override
    public void cacheOnDisk(List<StockData> stockData) {
        // cache on disk
        writeDataInCacheFile(stockData);
    }
}
