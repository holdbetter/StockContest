package com.holdbetter.stonks.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.holdbetter.stonks.Credentials;
import com.holdbetter.stonks.model.Indice;
import com.holdbetter.stonks.model.StockHttpGetData;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class StocksViewModel extends ViewModel {
    private MutableLiveData<List<StockHttpGetData>> liveStocks;
    private List<StockHttpGetData> cachedList;

    public StocksViewModel() {
    }

    public MutableLiveData<List<StockHttpGetData>> getLiveStocks() {
        if (liveStocks == null) {
            liveStocks = new MutableLiveData<>();
        }
        return liveStocks;
    }

    public boolean isStocksCached() {
        return cachedList != null;
    }

    public List<StockHttpGetData> getCachedList() {
        return cachedList;
    }

    public Single<List<StockHttpGetData>> getStockObservable() throws IOException {
        return Observable.fromCallable(this::getIndiceNames)
                .flatMapIterable(stocks -> stocks)
                .map(this::getStockPrices)
                .toList()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(new Consumer<List<StockHttpGetData>>() {
                    @Override
                    public void accept(List<StockHttpGetData> stocks) throws Throwable {
                        cachedList = stocks;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private List<StockHttpGetData> getIndiceNames() throws IOException {
        String answerJson = IOUtils.toString(Credentials.GET_CONSTITUENTS_URL, StandardCharsets.UTF_8);
        Indice dowJones = new Gson().fromJson(answerJson, Indice.class);
        List<StockHttpGetData> stocks = new ArrayList<>();
        for (String constituent : dowJones.getConstituents()) {
            stocks.add(new StockHttpGetData(constituent));
        }
        return stocks;
    }

    private StockHttpGetData getStockPrices(StockHttpGetData stock) {
        StringBuilder builder = new StringBuilder(Credentials.GET_STOCK_PRICE);
        builder.append(stock.getStockName());
        builder.append("&token=");
        builder.append(Credentials.API_KEY);
        URL stockPriceURL = null;
        try {
            stockPriceURL = new URL(builder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) stockPriceURL.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("accept", "application/json");

        String answerJson = null;
        try {
            InputStream inputStream = connection.getInputStream();
            answerJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StockHttpGetData.Price price = new Gson().fromJson(answerJson, StockHttpGetData.Price.class);

        stock.setPrice(price);
        return stock;
    }

//    public void downloadStockNames() {
//        new Thread(() ->
//        {
//            Gson gson = new Gson();
//            try {
//                String answerJson = IOUtils.toString(Credentials.GET_CONSTITUENTS_URL, StandardCharsets.UTF_8);
//                Indice dowJones = gson.fromJson(answerJson, Indice.class);
//                List<Stock> stocks = new ArrayList<>();
//                for (String constituent : dowJones.getConstituents()) {
//                    stocks.add(new Stock(constituent));
//                }
//                liveStocks.postValue(stocks);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
}
