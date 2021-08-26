package com.holdbetter.stonks.utility;

import android.util.Log;

import com.holdbetter.stonks.model.IndiceBaseInfoProvider;
import com.holdbetter.stonks.model.http.IndiceHttp;
import com.holdbetter.stonks.model.room.Indice;
import com.holdbetter.stonks.model.room.IndiceWithSymbols;
import com.holdbetter.stonks.model.room.Symbol;
import com.holdbetter.stonks.viewmodel.StockViewModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.rxjava3.core.Observable;

public class IndiceCache {
    private static IndiceCache instance = null;

    public static IndiceCache getInstance() {
        if (instance == null) {
            instance = new IndiceCache();
        }
        return instance;
    }

    public void cache(IndiceHttp freshIndiceData, StockViewModel stocksViewModel) {
        Log.d("INDICE_CACHING", Thread.currentThread().getName());

        // exchange walmart (logo not accesible) for yandex
        for (int i = 0; i < freshIndiceData.getConstituents().length; i++) {
            if (freshIndiceData.getConstituents()[i].equals("WMT")) {
                freshIndiceData.getConstituents()[i] = "YNDX";
                break;
            }
        }

        List<Symbol> symbols = Arrays.stream(freshIndiceData.getConstituents()).flatMap((Function<String, Stream<Symbol>>) s -> Stream.of(new Symbol(s))).collect(Collectors.toList());

        IndiceWithSymbols indiceWithSymbols = new IndiceWithSymbols();
        indiceWithSymbols.indice = new Indice(freshIndiceData.getName());
        indiceWithSymbols.symbols = symbols;

        stocksViewModel.getDatabase()
                .getIndiceDao()
                .insertIndiceWithSymbols(indiceWithSymbols);
    }

    public Observable<? extends IndiceBaseInfoProvider> getCache(StockViewModel stocksViewModel, String indiceName) {
        return Observable.create(emitter -> {
            Log.d("INDICE_CACHE_READ", Thread.currentThread().getName());
            IndiceWithSymbols indiceWithSymbols = stocksViewModel.getDatabase().getIndiceDao().getIndiceWithSymbols(indiceName);
            if (indiceWithSymbols != null && indiceWithSymbols.symbols.size() != 0) {
                IndiceHttp indiceHttp = new IndiceHttp();
                String[] symbols = indiceWithSymbols.symbols.stream().flatMap((Function<Symbol, Stream<String>>) symbol -> Stream.of(symbol.indiceName)).toArray(String[]::new);
                indiceHttp.setConstituents(symbols);
                indiceHttp.setName(indiceWithSymbols.indice.name);
                emitter.onNext(indiceHttp);
            }
            emitter.onComplete();
        });
    }
}
