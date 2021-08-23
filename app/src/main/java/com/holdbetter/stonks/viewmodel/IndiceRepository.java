package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.holdbetter.stonks.Credentials;
import com.holdbetter.stonks.model.IndiceBaseInfoProvider;
import com.holdbetter.stonks.model.http.IndiceHttp;
import com.holdbetter.stonks.utility.IndiceCache;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    public Single<? extends IndiceBaseInfoProvider> getIndice(StockViewModel stocksViewModel, IndiceCache cacheRepository, String indiceName) {
        return Observable.concat(cacheRepository.getCache(stocksViewModel, indiceName),
                queryIndiceNames(indiceName, stocksViewModel, cacheRepository))
                .subscribeOn(Schedulers.io())
                .firstElement()
                .toSingle();
    }

    private Observable<? extends IndiceBaseInfoProvider> queryIndiceNames(String indiceName, StockViewModel stocksViewModel, IndiceCache cacheRepository) {
        return Single.fromCallable(() -> getIndiceNames(indiceName))
                .doOnSuccess(freshIndiceData -> cacheRepository.cache(freshIndiceData, stocksViewModel))
                .toObservable();
    }

    private IndiceHttp getIndiceNames(String indiceName) {
        Log.d("INDICE_DOWNLOAD", Thread.currentThread().getName());
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.getConstituentsURL(indiceName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        IndiceHttp indiceHttp = new Gson().fromJson(answerJson, IndiceHttp.class);
        indiceHttp.setName(indiceName);

        return indiceHttp;
    }
}
