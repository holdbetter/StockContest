package com.holdbetter.stonks.viewmodel;

import androidx.lifecycle.ViewModel;

import com.holdbetter.stonks.model.StockData;

import java.util.List;

public class StocksViewModel extends ViewModel {
    private List<StockData> httpData;
    private List<String> dowJonesCostituents;

    public List<StockData> getHttpStocksData() {
        return httpData;
    }

    public List<String> getDowJonesCostituents() {
        return dowJonesCostituents;
    }

    public void setHttpData(List<StockData> httpData) {
        this.httpData = httpData;
    }

    public void setDowJonesSymbols(List<String> symbols) {
        dowJonesCostituents = symbols;
    }
}
