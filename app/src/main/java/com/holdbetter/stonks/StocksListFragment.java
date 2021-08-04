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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.TreeSet;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class StocksListFragment extends Fragment implements LifecycleObserver {
    private File cacheDirectory;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private StocksRecyclerAdapter adapter;
    private StocksRepository repository;
    private StocksViewModel viewModel;
    private PublishSubject<String> subject;
    private ConstituentsCache constituentsCache;
    private StockCache stockCache;

    public StocksListFragment() {

    }

    public static Fragment getInstance() {
        return new StocksListFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        cacheDirectory = getContext().getFilesDir();
        subject = PublishSubject.create();
        viewModel = new ViewModelProvider(requireActivity()).get(StocksViewModel.class);
        repository = StocksRepository.getInstance();

        constituentsCache = ConstituentsCache.getInstance(cacheDirectory);
        stockCache = StockCache.getInstance(cacheDirectory);

        StocksListFragmentBinding binding = StocksListFragmentBinding.inflate(inflater, container, false);
        adapter = createAdapter(binding);

        getViewLifecycleOwner().getLifecycle().addObserver(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Disposable setupData = setupConnection(subject, viewModel, constituentsCache, stockCache);
        Disposable socketUpdate = handleSocketMessage(subject);

        compositeDisposable.addAll(setupData, socketUpdate);
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    void reconnect() {
        Log.d("ON_RECONNECT", "Subscription count: " + compositeDisposable.size());
        if (viewModel.getStockData() == null && compositeDisposable.size() == 0) {
            Log.d("RESTORE", "FULL");
            compositeDisposable.add(setupConnection(subject, viewModel, constituentsCache, stockCache));
            compositeDisposable.add(handleSocketMessage(subject));
        } else if (compositeDisposable.size() == 0) {
            Log.d("RESTORE", "SOCKET");
            compositeDisposable.add(repository.socketReconnect(cacheDirectory, subject, viewModel));
            compositeDisposable.add(handleSocketMessage(subject));
        }
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    void disconnect() {
        Log.d("ON_DISCONNECT", "Subscription count: " + compositeDisposable.size());
        compositeDisposable.clear();
        repository.socketDisconnect();
    }

    @NotNull
    private Disposable setupConnection(PublishSubject<String> subject, StocksViewModel stocksViewModel, ConstituentsCache constituentsCache, StockCache stockCache) {
        return repository.getDowJonesIndice(stocksViewModel, constituentsCache)
                .flatMap(indice -> repository.getStocksData(stocksViewModel, indice, stockCache))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(adapter::setStocks)
                .observeOn(Schedulers.io())
                .flatMapMaybe(httpData -> repository.subscribeToSymbols(subject, httpData))
                .subscribe(s -> Log.d("SOCKET_CONNECTION", s.getState() + ""));
    }

    @NotNull
    private Disposable handleSocketMessage(PublishSubject<String> subject) {
        return subject.flatMap(t -> Observable.just(new GsonBuilder()
                .registerTypeAdapter(TreeSet.class, new SocketMessageDeserializer())
                .create()
                .fromJson(t, TreeSet.class)))
                .filter(treeSet -> !treeSet.isEmpty())
                .doOnNext(SocketRepository::printSocketMessage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::updateStocks, Throwable::printStackTrace);
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
}
