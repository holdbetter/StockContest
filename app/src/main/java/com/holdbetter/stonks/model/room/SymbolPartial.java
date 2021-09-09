package com.holdbetter.stonks.model.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.holdbetter.stonks.model.SymbolBaseInfoProvider;

public class SymbolPartial {
    public String name;
    public String companyName;
    public String indiceName;

    public SymbolPartial(@NonNull String name, String companyName, String indiceName) {
        this.name = name;
        this.companyName = companyName;
        this.indiceName = indiceName;
    }
}
