package com.holdbetter.stonks.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public abstract class FavouriteDao {
    @Query("SELECT * FROM symbols WHERE isFavourite = 1 ORDER BY name")
    public abstract Flowable<List<SymbolWithPrices>> getFavouriteList();

    @Query("SELECT isFavourite FROM symbols WHERE name = :name")
    public abstract LiveData<Boolean> isFavouriteSymbol(String name);
}
