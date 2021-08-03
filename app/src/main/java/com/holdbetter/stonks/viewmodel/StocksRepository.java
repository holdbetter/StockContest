package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.utility.ConstituentsCache;
import com.holdbetter.stonks.utility.StockCache;
import com.neovisionaries.ws.client.WebSocket;

import java.io.File;
import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class StocksRepository extends Repository {
    private static StocksRepository instance;

    public final IndiceRepository indiceRepository = IndiceRepository.getInstance();
    public final SymbolRepository symbolRepository = SymbolRepository.getInstance();
    public final SocketRepository socketRepository = SocketRepository.getInstance();

    private StocksRepository() {
    }

    public static StocksRepository getInstance() {
        if (instance == null) {
            instance = new StocksRepository();
        }
        return instance;
    }

    public Single<Indice> getDowJonesIndice(StocksViewModel stocksViewModel, ConstituentsCache cacheRepository) {
        return indiceRepository.getDowJonesIndice(stocksViewModel, cacheRepository);
    }

    public Single<List<StockData>> getStocksData(StocksViewModel stocksViewModel, Indice indice, StockCache cacheRepository) {
        return symbolRepository.getStocksData(stocksViewModel, indice, cacheRepository);
    }

    public Maybe<WebSocket> subscribeToSymbols(PublishSubject<String> subject,
                                               List<StockData> symbols) {
        return socketRepository.subscribeToSymbols(subject, symbols);
    }

    public void socketDisconnect() {
        if (socketRepository.getSocket() != null) {
            Log.d("SOCKET", "DISPOSE");
            socketRepository.dispose();
        }
    }

    public Disposable socketReconnect(File cacheDirectory, PublishSubject<String> subject, StocksViewModel stocksViewModel) {
        return Single.just(socketRepository.isSocketAvailable())
                .filter(available -> !available)
                .flatMapSingle(empty -> Single.just(socketRepository.newSocketInstance()))
                .subscribeOn(Schedulers.io())
                .filter(webSocket -> isUSMarketCurrentlyOpen())
                .map(socket -> socket.connect())
                .doOnSuccess(socket -> Log.d("RECONNECT", socket.getState().toString()))
                .map(socket -> socket.addListener(socketRepository.createListener(subject)))
                .flatMapObservable(socket -> StockCache.getInstance(cacheDirectory).getMemoryCache(stocksViewModel))
                .flatMapIterable(stockDataList -> stockDataList)
                .doOnNext(socketRepository::sendMessageToSubscribe)
                .subscribe();
    }
}
