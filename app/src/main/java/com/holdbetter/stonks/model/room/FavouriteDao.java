package com.holdbetter.stonks.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long[] insertSymbols(SymbolEntity... symbol);

    @Update
    public abstract int updateSymbol(SymbolEntity symbol);

    @Query("SELECT * FROM symbols WHERE isFavourite = 1")
    public abstract LiveData<List<SymbolEntity>> getFavouriteList();
}
