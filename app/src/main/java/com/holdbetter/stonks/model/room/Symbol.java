package com.holdbetter.stonks.model.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.holdbetter.stonks.model.SymbolBaseInfoProvider;

@Entity(tableName = "symbols")
public class Symbol implements SymbolBaseInfoProvider {
    @PrimaryKey
    @NonNull
    public String name;
    public String companyName;
    @ColumnInfo(defaultValue = "0")
    public boolean isFavourite;
    public String logoUrl;
    public String indiceName;

    public Symbol(@NonNull String name) {
        this.name = name;
    }

    @Ignore
    public Symbol(@NonNull String name, String companyName, boolean isFavourite, String logoUrl, String indiceName) {
        this.name = name;
        this.companyName = companyName;
        this.isFavourite = isFavourite;
        this.logoUrl = logoUrl;
        this.indiceName = indiceName;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isCachedData() {
        return true;
    }

    @Override
    public String getIndiceName() {
        return indiceName;
    }
}
