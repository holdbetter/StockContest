package com.holdbetter.stonks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.holdbetter.stonks.databinding.StocksListFragmentBinding;
import com.holdbetter.stonks.viewmodel.StocksViewModel;

import java.io.IOException;

import io.reactivex.rxjava3.disposables.Disposable;

public class StocksListFragment extends Fragment {
    public static Fragment getInstance() {
        return new StocksListFragment();
    }

    private Disposable subscribe;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        StocksListFragmentBinding binding = StocksListFragmentBinding.inflate(inflater, container, false);
        StocksRecyclerAdapter adapter = new StocksRecyclerAdapter();
        binding.stocksRecycler.setAdapter(adapter);
        binding.stocksRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        StocksViewModel stocksViewModel = new ViewModelProvider(requireActivity()).get(StocksViewModel.class);
        if (stocksViewModel.isStocksCached()) {
            adapter.setStocks(stocksViewModel.getCachedList());
        } else {
            try {
                subscribe = stocksViewModel.getStockObservable().subscribe(adapter::setStocks);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


//        adapter.

//        StocksViewModel stocksViewModel = new ViewModelProvider(requireActivity()).get(StocksViewModel.class);
//        if (stocksViewModel.getLiveStocks().getValue() == null) {
//            stocksViewModel.downloadStockNames();
//        }
//        stocksViewModel.getLiveStocks().observe(getViewLifecycleOwner(), adapter::setStocks);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
