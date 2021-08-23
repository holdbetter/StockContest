package com.holdbetter.stonks.utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.holdbetter.stonks.model.http.SymbolHttp;
import com.holdbetter.stonks.model.room.Price;
import com.holdbetter.stonks.model.room.Symbol;
import com.holdbetter.stonks.viewmodel.StockViewModel;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Observable;

public class SymbolCache {
    private static SymbolCache instance;

    private SymbolCache() {
    }

    public static SymbolCache getInstance() {
        if (instance == null) {
            instance = new SymbolCache();
        }
        return instance;
    }

    public Observable<List<Symbol>> getCache(StockViewModel stocksViewModel, @NonNull String indiceName) {
        return Observable.create(emitter -> {
            Log.d("SYMBOL_NAME_READING", Thread.currentThread().getName());
            List<Symbol> symbols = stocksViewModel.getDatabase()
                    .getSymbolDao()
                    .getSymbolsByIndiceName(indiceName);

            if (symbols.size() != 0) {
                emitter.onNext(symbols);
            }
            emitter.onComplete();
        });
    }

    public void cache(List<SymbolHttp> stockData, StockViewModel viewModel, String indiceName) {
        Log.d("SYMBOL_CACHING", Thread.currentThread().getName());
        List<Symbol> symbols = stockData.stream().map(symbolHttp -> new Symbol(symbolHttp.getName(), symbolHttp.getCompanyName(), symbolHttp.isFavourite(), symbolHttp.getLogoUrl(), indiceName)).collect(Collectors.toList());

        List<Price> prices = stockData.stream().map(symbolHttp -> new Price(symbolHttp.getLatestUpdateTimeInMillis(), symbolHttp.getLatestPrice(), symbolHttp.isUSMarketOpen(), symbolHttp.getPriceChange(), symbolHttp.getPriceChangePercent(), symbolHttp.getPreviousClose(), symbolHttp.getName())).collect(Collectors.toList());

        viewModel.getDatabase()
                .getSymbolDao()
                .updateSymbolsAddPrices(symbols, prices)
                .subscribe();
    }
}
