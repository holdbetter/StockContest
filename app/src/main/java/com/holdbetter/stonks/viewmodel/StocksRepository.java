package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModel;

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
import java.net.Socket;
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

public class StocksRepository extends Repository implements LifecycleObserver {
    private final IndiceRepository indiceRepository = IndiceRepository.getInstance();
    private final SymbolRepository symbolRepository = SymbolRepository.getInstance();
    private final SocketRepository socketRepository = SocketRepository.getInstance();

    private static StocksRepository instance;

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

    public Maybe<WebSocket> subscribeToSymbols(WebSocket socket,
                                               PublishSubject<String> subject,
                                               List<StockData> symbols) {
        return socketRepository.subscribeToSymbols(socket, subject, symbols);
    }
}
