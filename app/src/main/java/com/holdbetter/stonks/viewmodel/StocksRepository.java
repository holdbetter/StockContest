package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.GsonBuildConfig;
import com.holdbetter.stonks.Credentials;
import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.model.StockSocketData;
import com.holdbetter.stonks.model.WebSocketMessageToSubscribe;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class StocksRepository {

    private static StocksRepository instance;

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

    public Single<List<StockData>> getStocksData(List<String> symbols) {
        return Observable.fromIterable(symbols)
                .subscribeOn(Schedulers.io())
                .flatMap(symbol -> Observable.just(getStockInfo(symbol)))
                .observeOn(Schedulers.computation())
                .toSortedList((s1, s2) -> s1.getSymbol().compareTo(s2.getSymbol()));
    }

    public Single<List<String>> getDowJonesConstituents() {
        return Observable.fromCallable(this::getIndiceNames)
                .flatMapIterable(indice -> new ArrayList<>(Arrays.asList(indice.getConstituents())))
                .toList()
                .subscribeOn(Schedulers.io());
    }

    private Indice getIndiceNames() {
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.GET_CONSTITUENTS_URL, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Gson().fromJson(answerJson, Indice.class);
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
