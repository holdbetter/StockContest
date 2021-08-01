package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.holdbetter.stonks.Credentials;
import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.utility.StockCache;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SymbolRepository extends Repository {
    private static SymbolRepository instance;

    private SymbolRepository() {

    }

    public static SymbolRepository getInstance() {
        if (instance == null) {
            instance = new SymbolRepository();
        }
        return instance;
    }

    public Single<List<StockData>> getStocksData(StocksViewModel stocksViewModel, Indice indice, StockCache cacheRepository) {
        return Observable.concat(cacheRepository.getMemoryCache(stocksViewModel),
                cacheRepository.getDiskCache(),
                queryStockData(stocksViewModel, indice, cacheRepository))
                .firstElement()
                .toSingle();
    }

    private Observable<List<StockData>> queryStockData(StocksViewModel stocksViewModel, Indice indice, StockCache cacheRepository) {
        return Observable.fromIterable(Arrays.asList(indice.getConstituents()))
                .subscribeOn(Schedulers.io())
                .flatMap(symbol -> Observable.just(getStockInfo(symbol)))
                .map(stockData -> appendLogoInfo(stockData))
                .observeOn(Schedulers.computation())
                .toSortedList((s1, s2) -> s1.getSymbol().compareTo(s2.getSymbol()))
                .doOnSuccess(s -> Log.d("STOCKS_LOADED", Thread.currentThread().getName()))
                .observeOn(Schedulers.io())
                .doAfterSuccess(stockData -> cacheRepository.cache(stockData, stocksViewModel))
                .toObservable();
    }

    private StockData getStockInfo(String symbol) {
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.getSymbolPriceURL(symbol), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Gson().fromJson(answerJson, StockData.class);
    }

    private StockData appendLogoInfo(StockData stockData) {
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.getSymbolLogoURL(stockData.getSymbol()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stockData.setUrl(new Gson().fromJson(answerJson, LogoUrl.class).url);
        return stockData;
    }

    private static class LogoUrl {
        String url;
    }
}
