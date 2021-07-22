package com.holdbetter.stonks;

import java.net.MalformedURLException;
import java.net.URL;

public class Credentials
{
    public static final String API_KEY = "c3pg96iad3ifkq8gs3sg";
    public static final String GET_INDICES = "";
    public static final String GET_STOCK_PRICE = "https://finnhub.io/api/v1/quote?symbol=";
    public static final String GET_CONSTITUENTS = String.format("https://finnhub.io/api/v1/index/constituents?symbol=%s&token=%s", "^DJI", API_KEY);
    public static URL GET_CONSTITUENTS_URL;

    static {
        try {
            GET_CONSTITUENTS_URL = new URL(GET_CONSTITUENTS);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
