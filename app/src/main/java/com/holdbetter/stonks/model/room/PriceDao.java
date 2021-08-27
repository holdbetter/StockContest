package com.holdbetter.stonks.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public abstract class PriceDao {
    @Transaction
    @Query("SELECT * FROM symbols WHERE indiceName = :indiceName ORDER BY name")
    public abstract Flowable<List<SymbolWithPrices>> getSymbolWithPrices(String indiceName);

    @Query("DELETE FROM prices WHERE symbolName = :symbolName AND latestUpdateInMillis >= :latestUpdateInMillis AND isUSMarketOpen = 1")
    public abstract Single<Integer> deletePricesAfterDateForSymbol(String symbolName, long latestUpdateInMillis);
}
