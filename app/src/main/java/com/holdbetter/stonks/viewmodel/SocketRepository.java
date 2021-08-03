package com.holdbetter.stonks.viewmodel;

import android.telecom.InCallService;
import android.util.Log;

import com.holdbetter.stonks.Credentials;
import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.model.StockSocketData;
import com.holdbetter.stonks.model.WebSocketMessageToSubscribe;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketListener;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.TreeSet;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeEmitter;
import io.reactivex.rxjava3.core.MaybeOnSubscribe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SocketRepository extends Repository {
    private static SocketRepository instance;

    private WebSocket socket;
    private WebSocketAdapter listener;
    private WebSocketFactory factory = new WebSocketFactory();

    private SocketRepository() {

    }

    public boolean isSocketAvailable() {
        return socket != null;
    }

    public WebSocket getSocket() {
        return socket;
    }

    public static SocketRepository getInstance() {
        if (instance == null) {
            instance = new SocketRepository();
        }

        return instance;
    }

    public Maybe<WebSocket> subscribeToSymbols(PublishSubject<String> subject,
                                               List<StockData> symbols) {
        return Single.fromCallable(this::isUSMarketCurrentlyOpen)
                .subscribeOn(Schedulers.io())
                .filter(isOpen -> isOpen)
                .flatMapSingle(marketOpen -> Single.just(newSocketInstance())
                .map(s -> s.connect()))
                .map(s -> {
                    s.addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) {
                            subject.onNext(text);
                        }
                    });
                    return s;
                })
                .doOnSuccess(so -> {
                    for (StockData httpData : symbols)
                        sendMessageToSubscribe(httpData);
                });
    }

    void sendMessageToSubscribe(StockData httpData) {
        socket.sendText(new WebSocketMessageToSubscribe(httpData.getSymbol()).toJson());
    }

    public static void printSocketMessage(TreeSet<StockSocketData> stocks) {
        for (StockSocketData s : stocks) {
            Log.d("SocketMessage", String.format("Symbol: %s  %.2f %d    / thread: %s%n", s.getSymbol(), s.getPrice(), s.getTime(), Thread.currentThread().getName()));
        }
    }


    public WebSocketAdapter createListener(PublishSubject<String> subject) {
        return new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String text) {
                subject.onNext(text);
            }
        };
    }

    public WebSocket newSocketInstance() {
        if (this.getSocket() != null) {
            dispose();
        }

        try {
            socket = factory.createSocket(Credentials.GET_SOCKET_URL, 3000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return socket;
    }

    void dispose() {
        this.getSocket().clearListeners();
        final WebSocket socketToDisconnect = this.getSocket();
        this.socket = null;
        socketToDisconnect.disconnect();
    }
}
