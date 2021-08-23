package com.holdbetter.stonks.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.holdbetter.stonks.model.http.StockPriceBySocket;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class SocketMessageDeserializer implements JsonDeserializer<SortedSet<StockPriceBySocket>> {
    @Nullable
    @Override
    public SortedSet<StockPriceBySocket> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject root = jsonElement.getAsJsonObject();
        String responseType = root.get("type").getAsString();
        switch (responseType) {
            case "error":
            case "ping":
                return new TreeSet<>();
            case "trade":
                Gson gson = new Gson();
                JsonArray tradesArray = root.getAsJsonArray("data");
                TreeSet<StockPriceBySocket> stockSocketSet = new TreeSet<>(Comparator.comparing(StockPriceBySocket::getSymbol));
                for (int i = tradesArray.size() - 1; i >= 0; i--) {
                    stockSocketSet.add(gson.fromJson(tradesArray.get(i), StockPriceBySocket.class));
                }
                return stockSocketSet;
            default:
                return new TreeSet<>();
        }
    }
}
