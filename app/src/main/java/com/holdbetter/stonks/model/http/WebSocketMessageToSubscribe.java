package com.holdbetter.stonks.model.http;

import com.google.gson.Gson;

public class WebSocketMessageToSubscribe {
    private final String type = "subscribe";
    private final String symbol;

    public WebSocketMessageToSubscribe(String symbol) {
        this.symbol = symbol;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
