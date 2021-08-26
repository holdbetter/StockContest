package com.holdbetter.stonks.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public abstract class IndiceDao {
    @Transaction
    @Query("SELECT * FROM indices WHERE name = :indiceName LIMIT 1")
    public abstract IndiceWithSymbols getIndiceWithSymbols(String indiceName);
    @Query("SELECT * FROM indices WHERE name = :indiceName LIMIT 1")
    public abstract Single<Indice> getIndice(String indiceName);
    @Insert
    public abstract Completable insertIndice(Indice indice);
    @Insert()
    public abstract Single<long[]> insertSymbols(List<Symbol> symbols);
    @Transaction
    public void insertIndiceWithSymbols(IndiceWithSymbols indiceWithSymbols) {
        insertIndice(indiceWithSymbols.indice).subscribe();
        insertSymbols(indiceWithSymbols.symbols).subscribe();
    }
    @Delete
    public abstract Single<Integer> deleteIndice(Indice indice);
}
