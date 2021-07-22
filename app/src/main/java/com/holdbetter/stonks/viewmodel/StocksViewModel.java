package com.holdbetter.stonks.viewmodel;

import androidx.lifecycle.ViewModel;

import com.holdbetter.stonks.model.StockHttpData;

import java.util.List;

public class StocksViewModel extends ViewModel {
    private List<StockHttpData> httpData;
    private List<String> dowJonesCostituents;

    public List<StockHttpData> getHttpStocksData() {
        return httpData;
    }

    public List<String> getDowJonesCostituents() {
        return dowJonesCostituents;
    }

    public void setHttpData(List<StockHttpData> httpData) {
        this.httpData = httpData;
    }

    public void setDowJonesSymbols(List<String> symbols) {
        dowJonesCostituents = symbols;
    }
}
