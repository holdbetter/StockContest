package com.holdbetter.stonks.utility;

import androidx.lifecycle.ViewModel;

import com.holdbetter.stonks.viewmodel.StocksViewModel;

import java.io.File;

import io.reactivex.rxjava3.core.Observable;

interface Caching<T> {
    void cache(T data, StocksViewModel viewModel);
    Observable<T> getMemoryCache(StocksViewModel stocksViewModel);
    Observable<T> getDiskCache();
}
