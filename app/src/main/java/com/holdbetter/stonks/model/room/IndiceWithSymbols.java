package com.holdbetter.stonks.model.room;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class IndiceWithSymbols {
    @Embedded
    public Indice indice;
    @Relation(parentColumn = "name", entityColumn = "indiceName")
    public List<Symbol> symbols;
}
