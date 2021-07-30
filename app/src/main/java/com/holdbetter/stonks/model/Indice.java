package com.holdbetter.stonks.model;

import com.google.gson.annotations.Expose;

import java.util.Calendar;

public class Indice {
    private String symbol;
    private String[] constituents;
    @Expose
    private long lastUpdateTime;
    @Expose
    private long expiresTime;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public long getExpiresTime() {
        return expiresTime;
    }

    public String[] getConstituents() {
        return constituents;
    }

    public void setConstituents(String[] constituents) {
        this.constituents = constituents;
    }

    public void setLastUpdateTime(long responseDateTime) {
        lastUpdateTime = responseDateTime;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(lastUpdateTime);
//        c.roll(Calendar.DAY_OF_YEAR, 1);
        c.roll(Calendar.SECOND, 10);
        expiresTime = c.getTimeInMillis();
    }
}
