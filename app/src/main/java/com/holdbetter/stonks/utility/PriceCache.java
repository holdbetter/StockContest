package com.holdbetter.stonks.utility;

import com.holdbetter.stonks.model.http.StockPriceBySocket;
import com.holdbetter.stonks.model.room.Price;
import com.holdbetter.stonks.viewmodel.StockViewModel;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PriceCache {
    private static PriceCache instance = null;

    public static PriceCache getInstance() {
        if (instance == null) {
            instance = new PriceCache();
        }
        return instance;
    }

    public void cache(TreeSet<?> treePrice, StockViewModel viewModel) {
        List<Price> freshPrices = treePrice.stream().flatMap(o -> Stream.of((StockPriceBySocket) o))
                .flatMap(p -> {
                    Price price = new Price();
                    price.latestPrice = p.getPrice();
                    price.latestUpdateInMillis = p.getTime();
                    price.symbolName = p.getSymbol();

                    return Stream.of(price);
                }).collect(Collectors.toList());

        viewModel.getDatabase()
                .getSymbolDao()
                .insertPrices(freshPrices)
                .subscribe();
    }
}
