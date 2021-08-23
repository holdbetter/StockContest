package com.holdbetter.stonks;

import java.net.MalformedURLException;
import java.net.URL;

public class Credentials {

    public static final String GET_SOCKET_URL = String.format("wss://ws.finnhub.io?token=%s", CredentialsStorage.API_KEY_FINNHUB);

    public static URL getConstituentsURL(String indiceName) throws MalformedURLException {
        return new URL(String.format("https://finnhub.io/api/v1/index/constituents?symbol=^%s&token=%s", indiceName, CredentialsStorage.API_KEY_FINNHUB));
    }

    public static URL getSymbolLogoURL(String symbol) throws MalformedURLException {
        return new URL(String.format("https://cloud.iexapis.com/stable/stock/%s/logo?token=%s", symbol, CredentialsStorage.API_KEY_IEX));
    }

    public static URL getSymbolDataURL(String symbol) throws MalformedURLException {
        return new URL(String.format("https://cloud.iexapis.com/stable/stock/%s/quote?token=%s", symbol, CredentialsStorage.API_KEY_IEX));
    }
}
