package com.holdbetter.stonks.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.holdbetter.stonks.model.http.IndiceHttp;
import com.holdbetter.stonks.model.http.SymbolHttp;
import com.holdbetter.stonks.model.room.StockDatabase;
import com.holdbetter.stonks.model.room.SymbolWithPrices;
import com.holdbetter.stonks.utility.IndiceCache;
import com.holdbetter.stonks.utility.PriceCache;
import com.holdbetter.stonks.utility.SymbolCache;

import java.io.File;
import java.util.List;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class StockViewModel extends AndroidViewModel {
    private List<SymbolHttp> stockData;
    private IndiceHttp dowJones;
    private final PublishSubject<String> subject = PublishSubject.create();
    private final MainRepository repository;
    private final IndiceCache constituentsCache;
    private final SymbolCache stockCache;
    private final PriceCache priceCache;
    private final StockDatabase database;

    public StockViewModel(Application application) {
        super(application);

        repository = MainRepository.getInstance();
        constituentsCache = IndiceCache.getInstance();
        stockCache = SymbolCache.getInstance();
        priceCache = PriceCache.getInstance();

        database = StockDatabase.getDatabase(application);
    }

    public List<SymbolHttp> getStockData() {
        return stockData;
    }

    public StockDatabase getDatabase() {
        return database;
    }

    public IndiceHttp getDowJones() {
        return dowJones;
    }

    public PublishSubject<String> getSubject() {
        return subject;
    }

    public PriceCache getPriceCache() {
        return priceCache;
    }

    public MainRepository getRepository() {
        return repository;
    }

    public LiveData<List<SymbolWithPrices>> getFavouriteList() {
        return database.getFavouriteDao().getFavouriteList();
    }

    public IndiceCache getConstituentsCache() {
        return constituentsCache;
    }

    public SymbolCache getSymbolCache() {
        return stockCache;
    }

    public void setStockData(List<SymbolHttp> stockData) {
        this.stockData = stockData;
    }

    public void setDowJonesIndice(IndiceHttp indice) {
        dowJones = indice;
    }
}
