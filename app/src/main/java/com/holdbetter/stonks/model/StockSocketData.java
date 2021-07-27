package com.holdbetter.stonks.model;

import com.google.gson.annotations.SerializedName;

public class StockSocketData {
    @SerializedName(value = "s")
    private String symbol;
    @SerializedName(value = "p")
    private double price;
    @SerializedName(value = "t")
    private long time;

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

