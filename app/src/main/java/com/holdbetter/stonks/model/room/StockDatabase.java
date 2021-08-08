package com.holdbetter.stonks.model.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SymbolEntity.class}, version = 1)
public abstract class StockDatabase extends RoomDatabase {
    public abstract FavouriteDao getFavouriteDao();

    private static volatile StockDatabase database;

    public static StockDatabase getDatabase(@NonNull Context context) {
        if (database == null) {
            synchronized (StockDatabase.class) {
                if (database == null) {
                    database = Room.databaseBuilder(context.getApplicationContext(), StockDatabase.class, "favourite")
                            .build();
                }
            }
        }
        return database;
    }
}
