package com.holdbetter.stonks.model.http;

import com.google.gson.annotations.SerializedName;

public class StockPriceBySocket {
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

