package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.holdbetter.stonks.Credentials;
import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.utility.ConstituentsCache;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class IndiceRepository extends Repository {
    private static IndiceRepository instance;

    private IndiceRepository() {

    }

    public static IndiceRepository getInstance() {
        if (instance == null) {
            instance = new IndiceRepository();
        }
        return instance;
    }

    public Single<Indice> getDowJonesIndice(StocksViewModel stocksViewModel, ConstituentsCache cacheRepository) {
        return Observable.concat(cacheRepository.getMemoryCache(stocksViewModel),
                cacheRepository.getDiskCache(),
                queryIndiceNames(stocksViewModel, cacheRepository))
                .subscribeOn(Schedulers.io())
                .firstElement()
                .toSingle();
    }

    private Observable<Indice> queryIndiceNames(StocksViewModel stocksViewModel, ConstituentsCache cacheRepository) {
        return Observable.fromCallable(this::getIndiceNames)
                .doOnNext(indice -> cacheRepository.cache(indice, stocksViewModel));
    }

    private Indice getIndiceNames() {
        Log.d("INDICE_LOADING", Thread.currentThread().getName());
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.GET_CONSTITUENTS_URL, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Indice indice = new Gson().fromJson(answerJson, Indice.class);
        indice.setLastUpdateTime(Calendar.getInstance().getTimeInMillis());
        Arrays.sort(indice.getConstituents());

        return indice;
    }
}
