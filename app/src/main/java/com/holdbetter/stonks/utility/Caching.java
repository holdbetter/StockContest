package com.holdbetter.stonks.utility;

import androidx.lifecycle.ViewModel;

import com.holdbetter.stonks.viewmodel.StocksViewModel;

import java.io.File;

interface Caching<T> {
    void cache(T data, StocksViewModel viewModel, File diskCacheDir);
    String getCacheFileName();
}
