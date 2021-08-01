package com.holdbetter.stonks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.GsonBuilder;
import com.holdbetter.stonks.databinding.StocksListFragmentBinding;
import com.holdbetter.stonks.services.SocketMessageDeserializer;
import com.holdbetter.stonks.utility.ConstituentsCache;
import com.holdbetter.stonks.utility.StockCache;
import com.holdbetter.stonks.viewmodel.SocketRepository;
import com.holdbetter.stonks.viewmodel.StocksRepository;
import com.holdbetter.stonks.viewmodel.StocksViewModel;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.jetbrains.annotations.NotNull;

import java.io.File;
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
        StocksRecyclerAdapter adapter = createAdapter(binding);


        WebSocketFactory factory = new WebSocketFactory();

        try {
            socket = factory.createSocket(Credentials.GET_SOCKET_URL, 3000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PublishSubject<String> subject = PublishSubject.create();

        StocksViewModel stocksViewModel = new ViewModelProvider(requireActivity()).get(StocksViewModel.class);
        File cacheDir = getContext().getFilesDir();
        StocksRepository repository = StocksRepository.getInstance();
        ConstituentsCache constituentsCache = ConstituentsCache.getInstance(cacheDir);
        StockCache stockCache = StockCache.getInstance(cacheDir);

        subscribe1 = repository.getDowJonesIndice(stocksViewModel, constituentsCache)
                .flatMap(indice -> repository.getStocksData(stocksViewModel, indice, stockCache))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(adapter::setStocks)
                .observeOn(Schedulers.io())
                .flatMapMaybe(httpData -> repository.subscribeToSymbols(socket, subject, httpData))
                .subscribe(s -> Log.d("Socket", String.format("State: %s", s.getState())));

        Disposable subscribe2 = subject.flatMap(t -> Observable.just(new GsonBuilder()
                .registerTypeAdapter(TreeSet.class, new SocketMessageDeserializer())
                .create()
                .fromJson(t, TreeSet.class)))
                .filter(treeSet -> !treeSet.isEmpty())
                .doOnNext(SocketRepository::printSocketMessage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::updateStocks, Throwable::printStackTrace);

        return binding.getRoot();
    }

    @NotNull
    private StocksRecyclerAdapter createAdapter(StocksListFragmentBinding binding) {
        StocksRecyclerAdapter adapter = new StocksRecyclerAdapter();
        adapter.setHasStableIds(true);
        ((SimpleItemAnimator) binding.stocksRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.stocksRecycler.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_stock_list));
        binding.stocksRecycler.addItemDecoration(dividerItemDecoration);
        return adapter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscribe1 != null) {
            subscribe1.dispose();
        }
    }
}
