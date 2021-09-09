package com.holdbetter.stonks.model.room;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public abstract class SymbolDao {
    @Transaction
    @Query("SELECT * FROM symbols ORDER BY name")
    public abstract LiveData<List<SymbolWithPrices>> getPrices();

    public Single<long[]> updateSymbolsAddPrices(List<Symbol> symbols, List<Price> prices) {
        return updateSymbols(symbols)
                .doOnSuccess(updatedCount -> Log.d("SYMBOLS_UPDATE", "COUNT: " + updatedCount))
                .flatMap(updatedCount -> insertPrices(prices))
                .doOnSuccess(insertedRows -> Log.d("PRICES_INSERT", "SIZE: " + insertedRows.length));
    }

    @Query("SELECT COUNT(*) FROM prices WHERE symbolName = :symbolName AND isUSMarketOpen = 0 AND latestUpdateInMillis = :latestUpdateTime")
    public abstract Integer hasThisPriceInfo(String symbolName, long latestUpdateTime);

    @Query("SELECT * FROM symbols WHERE indiceName = :indiceName ORDER BY name")
    public abstract List<Symbol> getSymbolsByIndiceName(String indiceName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Single<long[]> insertSymbols(List<Symbol> symbols);

    @Update
    public abstract Single<Integer> updateSymbols(List<Symbol> symbols);

    @Update
    public abstract Single<Integer> updateSymbol(Symbol symbol);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract Single<long[]> insertPrices(List<Price> prices);
}
