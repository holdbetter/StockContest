package com.holdbetter.stonks.model.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "symbols")
public class SymbolEntity {
    @PrimaryKey
    @NonNull
    public String name;
    @ColumnInfo(defaultValue = "0")
    public boolean isFavourite;

    public SymbolEntity(String name) {
        this.name = name;
    }
}
