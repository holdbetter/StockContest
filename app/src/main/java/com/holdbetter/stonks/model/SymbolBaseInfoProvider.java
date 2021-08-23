package com.holdbetter.stonks.model;

public interface SymbolBaseInfoProvider {
    String getName();
    boolean isCachedData();
    String getIndiceName();
}
