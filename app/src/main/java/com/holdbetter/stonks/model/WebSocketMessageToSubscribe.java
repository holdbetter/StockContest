package com.holdbetter.stonks.model;

public class WebSocketMessageToSubscribe {
    private String type = "subscribe";
    private String symbol;

    public WebSocketMessageToSubscribe(String symbol) {
        this.symbol = symbol;
    }
}
