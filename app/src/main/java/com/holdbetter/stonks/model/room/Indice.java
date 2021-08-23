package com.holdbetter.stonks.model.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "indices")
public class Indice {
    @NonNull
    @PrimaryKey
    public String name;

    public Indice(@NonNull String name) {
        this.name = name;
    }
}
