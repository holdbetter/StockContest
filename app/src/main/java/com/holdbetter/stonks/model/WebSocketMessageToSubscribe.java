package com.holdbetter.stonks.model;

import com.google.gson.Gson;

public class WebSocketMessageToSubscribe {
    private String type = "subscribe";
    private String symbol;

    public WebSocketMessageToSubscribe(String symbol) {
        this.symbol = symbol;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
