package com.holdbetter.stonks;

import android.os.Bundle;
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

import com.holdbetter.stonks.databinding.StocksListFragmentBinding;
import com.holdbetter.stonks.model.room.SymbolEntity;
import com.holdbetter.stonks.viewmodel.StocksViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class FavouriteListFragment extends Fragment {
    private StocksViewModel viewModel;

    public static FavouriteListFragment getInstance() {
        return new FavouriteListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(StocksViewModel.class);

        StocksListFragmentBinding binding = StocksListFragmentBinding.inflate(inflater, container, false);
        StocksRecyclerAdapter adapter = createAdapter(binding, viewModel);
        binding.stocksRecycler.setAdapter(adapter);


        viewModel.getFavouriteList().observe(getViewLifecycleOwner(), favEntity -> {
            adapter.setStocks(viewModel.getStockData().stream().filter(stockData -> {
                for (SymbolEntity symbolEntity : favEntity) {
                    if (stockData.getSymbol().equals(symbolEntity.name)) {
                        stockData.setFavourite(true);
                        return true;
                    }
                }
                stockData.setFavourite(false);
                return false;
            }).collect(Collectors.toList()));
        });

        return binding.getRoot();
    }

    @NotNull
    private StocksRecyclerAdapter createAdapter(StocksListFragmentBinding binding, StocksViewModel viewModel) {
        StocksRecyclerAdapter adapter = new StocksRecyclerAdapter(viewModel, getViewLifecycleOwner());
        adapter.setHasStableIds(true);
        ((SimpleItemAnimator) binding.stocksRecycler.getItemAnimator()).setSupportsChangeAnimations(false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_stock_list));
        binding.stocksRecycler.addItemDecoration(dividerItemDecoration);

        return adapter;
    }

}
