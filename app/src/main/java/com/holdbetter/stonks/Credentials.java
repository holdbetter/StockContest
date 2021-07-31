package com.holdbetter.stonks;

import java.net.MalformedURLException;
import java.net.URL;

public class Credentials {
    public static final String GET_CONSTITUENTS = String.format("https://finnhub.io/api/v1/index/constituents?symbol=%s&token=%s", "^DJI", CredentialsStorage.API_KEY_FINNHUB);
    public static final String GET_SOCKET_URL = String.format("wss://ws.finnhub.io?token=%s", CredentialsStorage.API_KEY_FINNHUB);
    public static URL GET_CONSTITUENTS_URL;

    static {
        try {
            GET_CONSTITUENTS_URL = new URL(GET_CONSTITUENTS);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static URL getSymbolLogoURL(String symbol) throws MalformedURLException {
        return new URL(String.format("https://cloud.iexapis.com/stable/stock/%s/logo?token=%s", symbol, CredentialsStorage.API_KEY_IEX));
    }

    public static URL getSymbolPriceURL(String symbol) throws MalformedURLException {
        return new URL(String.format("https://cloud.iexapis.com/stable/stock/%s/quote?token=%s", symbol, CredentialsStorage.API_KEY_IEX));
    }

    public static URL isUSMarketOpenURL() throws MalformedURLException {
        return new URL(String.format("https://cloud.iexapis.com/stable/stock/aapl/quote/isUSMarketOpen?token=%s", CredentialsStorage.API_KEY_IEX));
    }
}
