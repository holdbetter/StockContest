package com.holdbetter.stonks.model;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

public class StockHttpData
{
    private String stockName;
    private Price price;

    public StockHttpData(String stockName)
    {
        this.stockName = stockName;
    }

    public String getStockName()
    {
        return stockName;
    }

    public String getCurrentPrice() {
        return new DecimalFormat("##.#").format(price.current);
    }

    public void setCurrentPrice(double c) {
        this.price.current = c;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public static class Price {
        @SerializedName(value = "c")
        private double current;
        @SerializedName(value = "h")
        private double high;
        @SerializedName(value = "l")
        private double low;
        @SerializedName(value = "o")
        private double open;
        @SerializedName(value = "pc")
        private double previousClose;
        @SerializedName(value = "t")
        private double time;
        @SerializedName(value = "d")
        private double d;
        @SerializedName(value = "dp")
        private double dp;
    }
}
