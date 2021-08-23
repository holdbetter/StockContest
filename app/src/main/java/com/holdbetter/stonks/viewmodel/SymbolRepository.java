package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.holdbetter.stonks.Credentials;
import com.holdbetter.stonks.model.IndiceBaseInfoProvider;
import com.holdbetter.stonks.model.SymbolBaseInfoProvider;
import com.holdbetter.stonks.model.http.SymbolHttp;
import com.holdbetter.stonks.model.room.Price;
import com.holdbetter.stonks.model.room.Symbol;
import com.holdbetter.stonks.utility.SymbolCache;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Maybe;
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

    public Maybe<List<? extends SymbolBaseInfoProvider>> checkForDataUpdate(List<? extends SymbolBaseInfoProvider> symbolBaseList, StockViewModel viewModel) {
        return Maybe.create(emitter -> {
            String indiceName = symbolBaseList.get(0).getIndiceName();
            // query any symbol for market state
            SymbolHttp symbol = getStockInfo(symbolBaseList.get(0).getName());
            if (!symbol.isUSMarketOpen()) {
                // are prices on closed market already exist?
                int symbolWithSamePriceDateCount = viewModel.getDatabase()
                        .getSymbolDao()
                        .hasThisPriceInfo(symbol.getName(), symbol.getLatestUpdateTimeInMillis());

                // no
                if (symbolWithSamePriceDateCount == 0) {
                    // update market closed info
                    List<SymbolHttp> symbolHttpList = new ArrayList<>();
                    symbolHttpList.add(symbol);

                    Observable.fromIterable(symbolBaseList.subList(1, symbolBaseList.size()))
                            .flatMap(baseSymbol -> Observable.just(getStockInfo(baseSymbol.getName())))
                            .map(s -> {
                                s.setIndiceName(indiceName);
                                return s;
                            })
                            .doOnNext(symbolHttpList::add)
                            .subscribe();

                    // delete unrelevant data
                    for (SymbolHttp s : symbolHttpList) {
                        viewModel.getDatabase()
                                .getPriceDao()
                                .deletePricesAfterDateForSymbol(symbol.getName(), s.getLatestUpdateTimeInMillis());
                    }

                    // update data from request (symbolHttpList)
                    SymbolCache.getInstance().cache(symbolHttpList, viewModel, indiceName);
                }
                emitter.onComplete();
            } else {
                emitter.onSuccess(symbolBaseList);
            }
        });
    }

    public Single<List<? extends SymbolBaseInfoProvider>> getSymbolData(StockViewModel stocksViewModel, IndiceBaseInfoProvider indiceBaseInfoProvider, SymbolCache cacheRepository) {
        return Observable.concat(cacheRepository.getCache(stocksViewModel, indiceBaseInfoProvider.getName()),
                queryStockData(stocksViewModel, indiceBaseInfoProvider, cacheRepository))
                .firstElement()
                .toSingle();
    }

    private Observable<? extends List<? extends SymbolBaseInfoProvider>> queryStockData(StockViewModel stocksViewModel, IndiceBaseInfoProvider indiceBaseInfoProvider, SymbolCache cacheRepository) {
        return Observable.fromIterable(Arrays.asList(indiceBaseInfoProvider.getConstituents()))
                .subscribeOn(Schedulers.io())
                .flatMap(symbol -> Observable.just(getStockInfo(symbol)))
                .map(symbol -> {
                    symbol.setIndiceName(indiceBaseInfoProvider.getName());
                    return symbol;
                })
                .map(this::appendLogoInfo)
                .toList()
                .doOnSuccess(symbolHttpList -> cacheRepository.cache(symbolHttpList, stocksViewModel, indiceBaseInfoProvider.getName()))
                .doAfterSuccess(s -> Log.d("SYMBOLS_LOADED", Thread.currentThread().getName()))
                .toObservable();
    }

    private SymbolHttp getStockInfo(String symbol) {
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.getSymbolDataURL(symbol), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Gson().fromJson(answerJson, SymbolHttp.class);
    }

    private SymbolHttp appendLogoInfo(SymbolHttp stockData) {
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.getSymbolLogoURL(stockData.getName()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stockData.setLogoUrl(new Gson().fromJson(answerJson, LogoUrl.class).url);
        return stockData;
    }

    private static class LogoUrl {
        String url;
    }
}
