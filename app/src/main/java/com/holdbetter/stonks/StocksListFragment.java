package com.holdbetter.stonks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.GsonBuilder;
import com.holdbetter.stonks.databinding.StocksListFragmentBinding;
import com.holdbetter.stonks.services.SocketMessageDeserializer;
import com.holdbetter.stonks.viewmodel.StocksRepository;
import com.holdbetter.stonks.viewmodel.StocksViewModel;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.TreeSet;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class StocksListFragment extends Fragment {
    public static Fragment getInstance() {
        return new StocksListFragment();
    }

    private Disposable subscribe1;
    private WebSocket socket;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StocksListFragmentBinding binding = StocksListFragmentBinding.inflate(inflater, container, false);
        StocksRecyclerAdapter adapter = new StocksRecyclerAdapter();
        binding.stocksRecycler.setAdapter(adapter);
        binding.stocksRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        WebSocketFactory factory = new WebSocketFactory();

        try {
            socket = factory.createSocket("wss://ws.finnhub.io?token=c3pg96iad3ifkq8gs3sg", 3000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PublishSubject<String> subject = PublishSubject.create();

        StocksViewModel stocksViewModel = new ViewModelProvider(requireActivity()).get(StocksViewModel.class);
        subscribe1 = StocksRepository.getInstance().getDowJonesConstituents()
                .doOnSuccess(stocksViewModel::setDowJonesSymbols)
                .flatMap(StocksRepository.getInstance()::getSymbolsPrice)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(httpData -> {
                    stocksViewModel.setHttpData(httpData);
                    adapter.setStocks(httpData);
                })
                .observeOn(Schedulers.io())
                .flatMap(httpData -> StocksRepository.getInstance().subscribeToSymbols(socket, subject, httpData))
                .subscribe(s -> Log.d("Socket", String.format("Obs2 Working on: %s%n", Thread.currentThread().getName())));

        Disposable subscribe2 = subject.flatMap(t -> Observable.just(new GsonBuilder()
                .registerTypeAdapter(TreeSet.class, new SocketMessageDeserializer())
                .create()
                .fromJson(t, TreeSet.class)))
                .filter(obj -> obj != null)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(StocksRepository.getInstance()::printSocketMessage)
                .subscribe(adapter::setStocksChanged, Throwable::printStackTrace);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscribe1 != null) {
            subscribe1.dispose();
        }
    }
}
