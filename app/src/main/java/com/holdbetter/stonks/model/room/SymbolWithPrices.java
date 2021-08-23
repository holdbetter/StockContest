package com.holdbetter.stonks.model.room;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class SymbolWithPrices {
    @Embedded
    public Symbol symbol;
    @Relation(parentColumn = "name", entityColumn = "symbolName")
    public List<Price> priceList;
}
