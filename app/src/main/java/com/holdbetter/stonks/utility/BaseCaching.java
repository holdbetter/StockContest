package com.holdbetter.stonks.utility;

import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.viewmodel.StocksViewModel;

import org.jetbrains.annotations.Nullable;

import java.io.File;

import io.reactivex.rxjava3.core.Observable;

abstract class BaseCaching<T> implements Caching<T> {
    abstract void writeDataInCacheFile(File cache, T objectToWrite);
    @Nullable
    abstract T readDataInCacheFile(File cache);
}
