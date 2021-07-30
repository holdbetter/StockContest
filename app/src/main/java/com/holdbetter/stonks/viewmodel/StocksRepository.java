package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.holdbetter.stonks.Credentials;
import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.model.StockSocketData;
import com.holdbetter.stonks.model.WebSocketMessageToSubscribe;
import com.holdbetter.stonks.utility.ConstituentsCache;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class StocksRepository {

    private static StocksRepository instance;

    private StocksRepository() {}

    public static StocksRepository getInstance() {
        if (instance == null) {
            instance = new StocksRepository();
        }

        return instance;
    }

    public Single<WebSocket> subscribeToSymbols(WebSocket socket,
                                                PublishSubject<String> subject,
                                                List<StockData> symbols) {

        return Single.fromCallable(socket::connect)
                .subscribeOn(Schedulers.io())
                .map(s -> s.addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String text) {
                        subject.onNext(text);
                    }
                }))
                .doAfterSuccess(s -> {
                    for (StockData httpData : symbols)
                        sendMessageToSubscribe(s, httpData);
                });
    }

    private void sendMessageToSubscribe(WebSocket s, StockData httpData) {
        s.sendText(new WebSocketMessageToSubscribe(httpData.getSymbol()).toJson());
    }

    public void printSocketMessage(TreeSet<StockSocketData> stocks) {
        for (StockSocketData s : stocks) {
            Log.d("SocketMessage", String.format("Symbol: %s  %.2f %d    / thread: %s%n", s.getSymbol(), s.getPrice(), s.getTime(), Thread.currentThread().getName()));
        }
    }

    public Single<List<StockData>> getStocksData(Indice indice) {
        return Observable.fromIterable(Arrays.asList(indice.getConstituents()))
                .subscribeOn(Schedulers.io())
                .flatMap(symbol -> Observable.just(getStockInfo(symbol)))
                .observeOn(Schedulers.computation())
                .toSortedList((s1, s2) -> s1.getSymbol().compareTo(s2.getSymbol()));
    }

    public Single<Indice> getDowJonesIndice(StocksViewModel stocksViewModel, File diskCache) {
        ConstituentsCache cache = ConstituentsCache.getInstance();
        return Observable.concat(cache.getMemoryCache(stocksViewModel),
                cache.getDiskCache(diskCache),
                queryIndiceNames(stocksViewModel, diskCache))
                .subscribeOn(Schedulers.io())
                .firstElement()
                .toSingle();
    }

    private Observable<Indice> queryIndiceNames(StocksViewModel stocksViewModel, File diskCache) {
        return Observable.fromCallable(this::getIndiceNames)
                .doOnNext(indice -> ConstituentsCache.getInstance().cache(indice, stocksViewModel, diskCache));
    }

    private Indice getIndiceNames() {
        Log.d("DATA_LOADING", Thread.currentThread().getName());
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.GET_CONSTITUENTS_URL, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Indice indice = new Gson().fromJson(answerJson, Indice.class);
        indice.setLastUpdateTime(Calendar.getInstance().getTimeInMillis());

        return indice;
    }

    private StockData getStockInfo(String symbol) {
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.getSymbolPriceURL(symbol), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return appendLogoInfo(new Gson().fromJson(answerJson, StockData.class));
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
