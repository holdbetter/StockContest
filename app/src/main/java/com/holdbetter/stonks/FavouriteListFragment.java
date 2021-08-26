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

import java.util.Comparator;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavouriteListFragment extends Fragment {

    public static FavouriteListFragment getInstance() {
        return new FavouriteListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StockViewModel viewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);

        StocksListFragmentBinding binding = StocksListFragmentBinding.inflate(inflater, container, false);
        FavouriteRecyclerAdapter adapter = createAdapter(binding, viewModel);
        binding.stocksRecycler.setAdapter(adapter);

        viewModel.getFavouriteList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stocks -> adapter.setStocks(stocks));

        return binding.getRoot();
    }

    @NotNull
    private FavouriteRecyclerAdapter createAdapter(StocksListFragmentBinding binding, StockViewModel viewModel) {
        FavouriteRecyclerAdapter adapter = new FavouriteRecyclerAdapter(viewModel, getViewLifecycleOwner());
        adapter.setHasStableIds(true);
        ((SimpleItemAnimator) binding.stocksRecycler.getItemAnimator()).setMoveDuration(100);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_stock_list));
        binding.stocksRecycler.addItemDecoration(dividerItemDecoration);

        return adapter;
    }

}
