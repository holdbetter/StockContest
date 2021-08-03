package com.holdbetter.stonks.utility;

import androidx.lifecycle.ViewModel;

import com.holdbetter.stonks.viewmodel.StocksViewModel;

import java.io.File;

import io.reactivex.rxjava3.core.Observable;

interface Caching<T> {
    void cacheAll(T data, StocksViewModel viewModel);
    void cacheOnMemory(T data, StocksViewModel viewModel);
    void cacheOnDisk(T data);
    Observable<T> getMemoryCache(StocksViewModel stocksViewModel);
    Observable<T> getDiskCache();
}
