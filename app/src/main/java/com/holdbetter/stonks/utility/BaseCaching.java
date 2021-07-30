package com.holdbetter.stonks.utility;

import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.viewmodel.StocksViewModel;

import java.io.File;

import io.reactivex.rxjava3.core.Observable;

abstract class BaseCaching<T> implements Caching<T> {
    abstract void writeDataInCacheFile(File cache, T objectToWrite);
    abstract T readDataInCacheFile(File cache);
    abstract Observable<T> getMemoryCache(StocksViewModel stocksViewModel);
    abstract Observable<T> getDiskCache(File cacheFolder);
}
