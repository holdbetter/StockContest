package com.holdbetter.stonks.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.model.room.StockDatabase;
import com.holdbetter.stonks.model.room.SymbolEntity;
import com.holdbetter.stonks.utility.ConstituentsCache;
import com.holdbetter.stonks.utility.StockCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StocksViewModel extends AndroidViewModel {
    private List<StockData> stockData;
    private Indice dowJones;
    private File cacheDirectory;
    private StocksRepository repository;
    private ConstituentsCache constituentsCache;
    private StockCache stockCache;
    private StockDatabase database;

    public StocksViewModel(Application application) {
        super(application);

        cacheDirectory = application.getFilesDir();
        repository = StocksRepository.getInstance();
        constituentsCache = ConstituentsCache.getInstance(cacheDirectory);
        stockCache = StockCache.getInstance(cacheDirectory);

        database = StockDatabase.getDatabase(application);
    }

    public List<StockData> getStockData() {
        return stockData;
    }

    public StockDatabase getDatabase() {
        return database;
    }

    public Indice getDowJones() {
        return dowJones;
    }

    public File getCacheDirectory() {
        return cacheDirectory;
    }

    public StocksRepository getRepository() {
        return repository;
    }

    public LiveData<List<SymbolEntity>> getFavouriteList() {
        return database.getFavouriteDao().getFavouriteList();
    }

    public ConstituentsCache getConstituentsCache() {
        return constituentsCache;
    }

    public StockCache getStockCache() {
        return stockCache;
    }

    public void setStockData(List<StockData> stockData) {
        this.stockData = stockData;
    }

    public void setDowJonesIndice(Indice indice) {
        dowJones = indice;
    }


}
