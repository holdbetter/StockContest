package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import com.holdbetter.stonks.model.IndiceBaseInfoProvider;
import com.holdbetter.stonks.model.SymbolBaseInfoProvider;
import com.holdbetter.stonks.utility.IndiceCache;
import com.holdbetter.stonks.utility.SymbolCache;
import com.neovisionaries.ws.client.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class MainRepository extends Repository {
    public static final int DELAY = 8;
    private static MainRepository instance;

    public final IndiceRepository indiceRepository = IndiceRepository.getInstance();
    public final SymbolRepository symbolRepository = SymbolRepository.getInstance();
    public final SocketRepository socketRepository = SocketRepository.getInstance();

    private MainRepository() {
    }

    public static MainRepository getInstance() {
        if (instance == null) {
            instance = new MainRepository();
        }
        return instance;
    }

    public Single<? extends IndiceBaseInfoProvider> getIndice(StockViewModel stocksViewModel, IndiceCache cacheRepository, String indiceName) {
        return indiceRepository.getIndice(stocksViewModel, cacheRepository, indiceName);
    }

    public Single<List<? extends SymbolBaseInfoProvider>> getSymbolData(StockViewModel stocksViewModel, IndiceBaseInfoProvider indiceBaseInfoProvider, SymbolCache cacheRepository) {
        return symbolRepository.getSymbolData(stocksViewModel, indiceBaseInfoProvider, cacheRepository);
    }

    public Single<WebSocket> subscribeToSymbols(PublishSubject<String> subject,
                                                List<? extends SymbolBaseInfoProvider> symbolNameList) {
        return socketRepository.subscribeToSymbols(subject, symbolNameList);
    }

    public void socketDisconnect() {
        if (socketRepository.getSocket() != null) {
            Log.d("SOCKET", "DISPOSE");
            socketRepository.dispose();
        }
    }

    public Disposable socketReconnect(PublishSubject<String> subject, StockViewModel stocksViewModel, String indiceName) {
        return Single.timer(DELAY, TimeUnit.SECONDS)
                .flatMapMaybe(timer -> symbolRepository.checkForDataUpdate(
                        SymbolCache.getInstance()
                                .getCache(stocksViewModel, indiceName)
                                .blockingSingle(),
                        stocksViewModel))
                .doOnSuccess(marketOpen -> Single.just(socketRepository.isSocketAvailable())
                        .filter(available -> !available)
                        .flatMapSingle(empty -> Single.just(socketRepository.newSocketInstance()))
                        .map(WebSocket::connect)
                        .doOnSuccess(socket -> Log.d("RECONNECT", socket.getState().toString()))
                        .map(socket -> socket.addListener(socketRepository.createListener(subject)))
                        .subscribe()
                )
                .toObservable()
                .flatMapIterable(symbolBaseInfoList -> symbolBaseInfoList)
                .doOnNext(symbol -> socketRepository.sendMessageToSubscribe(symbol.getName()))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
