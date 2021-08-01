package com.holdbetter.stonks.viewmodel;

import android.telecom.InCallService;
import android.util.Log;

import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.model.StockSocketData;
import com.holdbetter.stonks.model.WebSocketMessageToSubscribe;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

import java.net.Socket;
import java.util.List;
import java.util.TreeSet;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SocketRepository extends Repository {
    private static SocketRepository instance;

    private SocketRepository() {

    }

    public static SocketRepository getInstance() {
        if (instance == null) {
            instance = new SocketRepository();
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

    public static void printSocketMessage(TreeSet<StockSocketData> stocks) {
        for (StockSocketData s : stocks) {
            Log.d("SocketMessage", String.format("Symbol: %s  %.2f %d    / thread: %s%n", s.getSymbol(), s.getPrice(), s.getTime(), Thread.currentThread().getName()));
        }
    }
}
