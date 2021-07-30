package com.holdbetter.stonks.viewmodel;

import androidx.lifecycle.ViewModel;

import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.model.StockData;

import java.util.List;

public class StocksViewModel extends ViewModel {
    private List<StockData> httpData;
    private Indice dowJones;

    public List<StockData> getHttpStocksData() {
        return httpData;
    }

    public Indice getDowJones() {
        return dowJones;
    }

    public void setHttpData(List<StockData> httpData) {
        this.httpData = httpData;
    }

    public void setDowJonesIndice(Indice indice) {
        dowJones = indice;
    }
}
