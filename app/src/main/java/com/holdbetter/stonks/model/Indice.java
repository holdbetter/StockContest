package com.holdbetter.stonks.model;

public class Indice {
    private String symbol;
    private String[] constituents;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String[] getConstituents() {
        return constituents;
    }

    public void setConstituents(String[] constituents) {
        this.constituents = constituents;
    }
}
