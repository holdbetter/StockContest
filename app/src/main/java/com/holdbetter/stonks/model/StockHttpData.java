package com.holdbetter.stonks.model;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

public class StockHttpData
{
    @SerializedName(value = "symbol")
    private String symbol;
    @SerializedName(value = "companyName")
    private String companyName;
    @SerializedName(value = "latestPrice")
    private double price;
    @SerializedName(value = "change")
    private double priceChange;
    @SerializedName(value = "changePercent")
    private double priceChangePercent;
    @SerializedName(value = "isUSMarketOpen")
    private boolean isMarketOpen;

    public StockHttpData() {
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCurrentPrice() {
        return new DecimalFormat("##.#").format(price);
    }

    public String getFormattedPriceChange() {
        if (priceChange > 0) {
            return String.format("+%.2f (%.2f%%)", priceChange, Math.abs(priceChangePercent) * 100);
        } else {
            return String.format("%.2f (%.2f%%)", priceChange, Math.abs(priceChangePercent) * 100);
        }
    }

    public String getPriceChangePercent() {
        return priceChangePercent * 100 + "";
    }

    public boolean isMarketOpen() {
        return isMarketOpen;
    }

    public void setCurrentPrice(double price) {
        this.price = price;
    }
}
