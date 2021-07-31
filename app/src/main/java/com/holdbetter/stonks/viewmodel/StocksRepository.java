package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.holdbetter.stonks.Credentials;
import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.model.StockSocketData;
import com.holdbetter.stonks.model.WebSocketMessageToSubscribe;
import com.holdbetter.stonks.utility.ConstituentsCache;
import com.holdbetter.stonks.utility.StockCache;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class StocksRepository {

    private static StocksRepository instance;

    private StocksRepository() {
    }

    public static StocksRepository getInstance() {
        if (instance == null) {
            instance = new StocksRepository();
        }

        return instance;
    }

    public Maybe<WebSocket> subscribeToSymbols(WebSocket socket,
                                                PublishSubject<String> subject,
                                                List<StockData> symbols) {
        return Single.just(isUSMarketCurrentlyOpen())
                .filter(isOpen -> isOpen)
                .flatMap(marketOpen -> Maybe.just(socket.connect()))
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

    public Single<Indice> getDowJonesIndice(StocksViewModel stocksViewModel, ConstituentsCache cacheRepository) {
        return Observable.concat(cacheRepository.getMemoryCache(stocksViewModel),
                cacheRepository.getDiskCache(),
                queryIndiceNames(stocksViewModel, cacheRepository))
                .subscribeOn(Schedulers.io())
                .firstElement()
                .toSingle();
    }

    private Observable<Indice> queryIndiceNames(StocksViewModel stocksViewModel, ConstituentsCache cacheRepository) {
        return Observable.fromCallable(this::getIndiceNames)
                .doOnNext(indice -> cacheRepository.cache(indice, stocksViewModel));
    }

    private Boolean isUSMarketCurrentlyOpen() {
        Log.d("CHECKING_FOR_USMARKET", Thread.currentThread().getName());
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.isUSMarketOpenURL(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Boolean.getBoolean(answerJson);
    }

    private Indice getIndiceNames() {
        Log.d("INDICE_LOADING", Thread.currentThread().getName());
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.GET_CONSTITUENTS_URL, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Indice indice = new Gson().fromJson(answerJson, Indice.class);
        indice.setLastUpdateTime(Calendar.getInstance().getTimeInMillis());
        Arrays.sort(indice.getConstituents());

        return indice;
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
