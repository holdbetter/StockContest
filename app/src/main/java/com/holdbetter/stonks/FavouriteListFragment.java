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
import com.holdbetter.stonks.viewmodel.StockViewModel;

import org.jetbrains.annotations.NotNull;

public class FavouriteListFragment extends Fragment {

    public static FavouriteListFragment getInstance() {
        return new FavouriteListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StockViewModel viewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);

        StocksListFragmentBinding binding = StocksListFragmentBinding.inflate(inflater, container, false);
        StockRecyclerAdapter adapter = createAdapter(binding, viewModel);
        binding.stocksRecycler.setAdapter(adapter);

        viewModel.getFavouriteList().observe(getViewLifecycleOwner(), adapter::setStocks);

        return binding.getRoot();
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
