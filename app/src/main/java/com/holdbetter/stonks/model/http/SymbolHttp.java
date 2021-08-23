package com.holdbetter.stonks.model.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.holdbetter.stonks.model.SymbolBaseInfoProvider;

public class SymbolHttp implements SymbolBaseInfoProvider
{
    @SerializedName(value = "symbol")
    private String name;
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
    private long latestUpdateTimeInMillis;
    @SerializedName(value = "previousClose")
    private double previousClose;
    @Expose
    private String logoUrl;
    @Expose
    private boolean isFavourite;
    @Expose
    private String indiceName;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isCachedData() {
        return false;
    }

    @Override
    public String getIndiceName() {
        return indiceName;
    }

    public double getLatestPrice() {
        return price;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public long getLatestUpdateTimeInMillis() {
        return latestUpdateTimeInMillis;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public double getPriceChangePercent() {
        return priceChangePercent;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public boolean isUSMarketOpen() {
        return isMarketOpen;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setIndiceName(String indiceName) {
        this.indiceName = indiceName;
    }

    private double transformToPercentValue(double value) {
        return Math.abs(priceChangePercent) * 100;
    }
}
