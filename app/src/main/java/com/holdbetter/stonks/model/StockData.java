package com.holdbetter.stonks.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

public class StockData
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
    @SerializedName(value = "latestUpdate")
    private long latestUpdateTime;
    @SerializedName(value = "previousClose")
    private double previousClose;
    @Expose
    private String url;

    public StockData() {
    }

    public long getLatestUpdateTime() {
        return latestUpdateTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCurrentPrice() {
        return String.format("%.2f", price);
    }

    public String getFormattedPriceChange() {
        if (priceChange > 0) {
            return String.format("+%.2f (%.2f%%)", priceChange, getPriceChangePercent());
        } else {
            return String.format("%.2f (%.2f%%)", priceChange, getPriceChangePercent());
        }
    }

    public String getUrl() {
        return url;
    }

    public double getPriceChangePercent() {
        return Math.abs(priceChangePercent) * 100;
    }

    public boolean isUSMarketOpen() {
        return isMarketOpen;
    }

    public void updatePrices(double price) {
        this.price = price;
        this.priceChange = price - previousClose;
        this.priceChangePercent = priceChange / previousClose;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
