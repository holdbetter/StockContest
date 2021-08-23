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

import com.holdbetter.stonks.databinding.StocksListFragmentBinding;
import com.holdbetter.stonks.model.room.Symbol;
import com.holdbetter.stonks.utility.IndiceCache;
import com.holdbetter.stonks.utility.PriceCache;
import com.holdbetter.stonks.utility.SymbolCache;
import com.holdbetter.stonks.viewmodel.MainRepository;
import com.holdbetter.stonks.viewmodel.StockViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class StockListFragment extends Fragment implements LifecycleObserver {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private StockRecyclerAdapter adapter;
    private MainRepository repository;
    private StockViewModel viewModel;
    private PublishSubject<String> subject;
    private IndiceCache constituentsCache;
    private SymbolCache stockCache;
    private PriceCache priceCache;
    private StocksListFragmentBinding binding;

    public StockListFragment() {

    }

    public static Fragment getInstance() {
        return new StockListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);
        subject = viewModel.getSubject();
        repository = viewModel.getRepository();
        constituentsCache = viewModel.getConstituentsCache();
        stockCache = viewModel.getSymbolCache();
        priceCache = viewModel.getPriceCache();

        binding = StocksListFragmentBinding.inflate(inflater, container, false);
        adapter = createAdapter(binding, viewModel);
        binding.stocksRecycler.setAdapter(adapter);

        getViewLifecycleOwner().getLifecycle().addObserver(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Disposable setupData = setupConnection(viewModel, constituentsCache, stockCache);
        Disposable socketUpdate = handleSocketMessage();

        viewModel.getDatabase()
                .getPriceDao()
                .getSymbolWithPrices(MainActivity.INDICE_TO_REQUEST)
                .observe(getViewLifecycleOwner(), adapter::setStocks);

        compositeDisposable.addAll(setupData, socketUpdate);
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    void reconnect() {
        Log.d("ON_RECONNECT", "Subscription count: " + compositeDisposable.size());
        Completable.create(emitter -> {
            List<Symbol> symbolsByIndiceName = viewModel.getDatabase().getSymbolDao().getSymbolsByIndiceName(MainActivity.INDICE_TO_REQUEST);
            if ((symbolsByIndiceName == null || symbolsByIndiceName.size() == 0) && compositeDisposable.size() == 0) {
                Log.d("RESTORE", "FULL");
                compositeDisposable.add(setupConnection(viewModel, constituentsCache, stockCache));
                compositeDisposable.add(handleSocketMessage());
            } else if (compositeDisposable.size() == 0) {
                Log.d("RESTORE", "SOCKET");
                compositeDisposable.add(repository.socketReconnect(subject, viewModel, MainActivity.INDICE_TO_REQUEST));
                compositeDisposable.add(handleSocketMessage());
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    void disconnect() {
        Log.d("ON_DISCONNECT", "Subscription count: " + compositeDisposable.size());
        compositeDisposable.clear();
        repository.socketDisconnect();
    }

    @NotNull
    private Disposable setupConnection(StockViewModel stocksViewModel, IndiceCache constituentsCache, SymbolCache symbolCache) {
        return repository.getIndice(stocksViewModel, constituentsCache, MainActivity.INDICE_TO_REQUEST)
                .flatMap(indice -> repository.getSymbolData(stocksViewModel, indice, symbolCache))
                .flatMapMaybe(symbolNameList -> repository.symbolRepository.checkForDataUpdate(symbolNameList, viewModel))
                .flatMapSingle(symbolNameList -> repository.subscribeToSymbols(subject, symbolNameList))
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.d("SOCKET_CONNECTION", s.getState() + ""));
    }

    @NotNull
    private Disposable handleSocketMessage() {
        return repository.socketRepository.startHandleSocketMessage(subject, priceCache, viewModel);
    }

    @NotNull
    private StockRecyclerAdapter createAdapter(StocksListFragmentBinding binding, StockViewModel viewModel) {
        StockRecyclerAdapter adapter = new StockRecyclerAdapter(viewModel, getViewLifecycleOwner());
        adapter.setHasStableIds(true);
        ((SimpleItemAnimator) binding.stocksRecycler.getItemAnimator()).setSupportsChangeAnimations(false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_stock_list));
        binding.stocksRecycler.addItemDecoration(dividerItemDecoration);
        return adapter;
    }
}
