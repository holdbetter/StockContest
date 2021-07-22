package com.holdbetter.stonks.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.holdbetter.stonks.model.StockSocketData;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class SocketMessageDeserializer implements JsonDeserializer<SortedSet<StockSocketData>> {
    @Nullable
    @Override
    public SortedSet<StockSocketData> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
//         writing logs
//        BufferedWriter writer = null;
//        try {
//            writer = new BufferedWriter(new FileWriter("logs.txt", true));
//            writer.write(String.format("%s%n%n%n",jsonElement.toString()));
//        } catch (IOException fileNotFoundException) {
//            fileNotFoundException.printStackTrace();
//        } finally {
//            if (writer != null) {
//                try {
//                    writer.close();
//                } catch (IOException ioException) {
//                    ioException.printStackTrace();
//                }
//            }
//        }

        JsonObject root = jsonElement.getAsJsonObject();
        String responseType = root.get("type").getAsString();
        switch (responseType) {
            case "error":
            case "ping":
                return null;
            case "trade":
                Gson gson = new Gson();
                JsonArray tradesArray = root.getAsJsonArray("data");
                TreeSet<StockSocketData> stockSocketSet = new TreeSet<>((s1, s2) -> s1.getS().compareTo(s2.getS()));
                for (int i = tradesArray.size() - 1; i >= 0; i--) {
                    stockSocketSet.add(gson.fromJson(tradesArray.get(i), StockSocketData.class));
                }
                return stockSocketSet;
            default:
                return null;
        }
    }
}
