package com.holdbetter.stonks.model.http;

import androidx.annotation.NonNull;

import com.holdbetter.stonks.model.IndiceBaseInfoProvider;

public class IndiceHttp implements IndiceBaseInfoProvider {
    private String name;
    private String[] constituents;

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public String[] getConstituents() {
        return constituents;
    }

    public void setConstituents(String[] constituents) {
        this.constituents = constituents;
    }
}
