package com.holdbetter.stonks.model.room;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "prices", primaryKeys = { "symbolName", "latestUpdateInMillis" })
public class Price {
    public long latestUpdateInMillis;
    public double latestPrice;
    public boolean isUSMarketOpen = true;
    public Double change;
    public Double changePercent;
    public Double previousClose;
    @NotNull
    public String symbolName;

    public Price(@NotNull String symbolName) {
        this.symbolName = symbolName;
    }

    @Ignore
    public Price(long latestUpdateInMillis, double latestPrice, boolean isUSMarketOpen, double change, double changePercent, double previousClose, String symbolName) {
        this.latestUpdateInMillis = latestUpdateInMillis;
        this.latestPrice = latestPrice;
        this.isUSMarketOpen = isUSMarketOpen;
        this.change = change;
        this.changePercent = changePercent;
        this.previousClose = previousClose;
        this.symbolName = symbolName;
    }

    public String getFormattedLatestPrice() {
        return String.format("$%.2f", latestPrice);
    }

    public String getFormattedPriceChange(double lastPreviousClose) {
        if (getPriceChange(lastPreviousClose) > 0) {
            return String.format("+%.2f (%.2f%%)", getPriceChange(lastPreviousClose), transformToPercentValue(getPriceChangePercent(lastPreviousClose)));
        } else {
            return String.format("%.2f (%.2f%%)", getPriceChange(lastPreviousClose), transformToPercentValue(getPriceChangePercent(lastPreviousClose)));
        }
    }

    private double transformToPercentValue(double value) {
        return Math.abs(value) * 100;
    }

    private double getPriceChange(double lastPreviousClose) {
        if (change == null) {
            return latestPrice - lastPreviousClose;
        }
        return change;
    }

    private double getPriceChangePercent(double lastPreviousPrice) {
        if (changePercent == null) {
            return (latestPrice - lastPreviousPrice) / lastPreviousPrice;
        }
        return changePercent;
    }
}
