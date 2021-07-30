package com.holdbetter.stonks.viewmodel;

import androidx.lifecycle.ViewModel;

import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.model.StockData;

import java.util.List;

public class StocksViewModel extends ViewModel {
    private List<StockData> stockData;
    private Indice dowJones;

    public List<StockData> getStockData() {
        return stockData;
    }

    public Indice getDowJones() {
        return dowJones;
    }

    public void setStockData(List<StockData> stockData) {
        this.stockData = stockData;
    }

    public void setDowJonesIndice(Indice indice) {
        dowJones = indice;
    }
}
