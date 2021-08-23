package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.holdbetter.stonks.Credentials;
import com.holdbetter.stonks.model.SymbolBaseInfoProvider;
import com.holdbetter.stonks.model.http.StockPriceBySocket;
import com.holdbetter.stonks.model.http.WebSocketMessageToSubscribe;
import com.holdbetter.stonks.services.SocketMessageDeserializer;
import com.holdbetter.stonks.utility.PriceCache;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SocketRepository extends Repository {
    private static SocketRepository instance;

    private WebSocket socket;
    private WebSocketAdapter listener;
    private final WebSocketFactory factory = new WebSocketFactory();

    private SocketRepository() {

    }

    public static SocketRepository getInstance() {
        if (instance == null) {
            instance = new SocketRepository();
        }

        return instance;
    }

    public static void printSocketMessage(TreeSet<StockPriceBySocket> stocks) {
        for (StockPriceBySocket s : stocks) {
            Log.d("SocketMessage", String.format("Symbol: %s  %.2f %d    / thread: %s%n", s.getSymbol(), s.getPrice(), s.getTime(), Thread.currentThread().getName()));
        }
    }

    public Single<WebSocket> subscribeToSymbols(PublishSubject<String> subject,
                                               List<? extends SymbolBaseInfoProvider> symbolNameList) {
        return Single.create((SingleOnSubscribe<WebSocket>) emitter -> {
                WebSocket socket = newSocketInstance();
                socket.connect();
                socket.addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String text) {
                        subject.onNext(text);
                    }
                });
                emitter.onSuccess(socket);
        }).subscribeOn(Schedulers.io()).doOnSuccess(s -> {
            for (SymbolBaseInfoProvider baseSymbolInfo : symbolNameList) {
                sendMessageToSubscribe(baseSymbolInfo.getName());
            }
        });
    }

    public Disposable startHandleSocketMessage(PublishSubject<String> subject, PriceCache cacheRepository, StockViewModel viewModel) {
        return subject.flatMap(t -> Observable.just(new GsonBuilder()
                .registerTypeAdapter(TreeSet.class, new SocketMessageDeserializer())
                .create()
                .fromJson(t, TreeSet.class)))
                .filter(treeSet -> !treeSet.isEmpty())
                .doOnNext(treePrice -> cacheRepository.cache(treePrice, viewModel))
                .doOnNext(SocketRepository::printSocketMessage)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    void sendMessageToSubscribe(String symbolName) {
        socket.sendText(new WebSocketMessageToSubscribe(symbolName).toJson());
    }

    public WebSocketAdapter createListener(PublishSubject<String> subject) {
        return new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String text) {
                subject.onNext(text);
            }
        };
    }

    void dispose() {
        this.getSocket().clearListeners();
        final WebSocket socketToDisconnect = this.getSocket();
        this.socket = null;
        socketToDisconnect.disconnect();
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

    public boolean isSocketAvailable() {
        return socket != null;
    }

    public WebSocket getSocket() {
        return socket;
    }
}
