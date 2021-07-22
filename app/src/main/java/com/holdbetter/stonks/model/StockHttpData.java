package com.holdbetter.stonks.model;

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
        return new DecimalFormat("##.#").format(price.c);
    }

    public void setCurrentPrice(double c) {
        this.price.c = c;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public static class Price {
        private double c;
        private double h;
        private double l;
        private double o;
        private double pc;
        private double t;
    }
}
