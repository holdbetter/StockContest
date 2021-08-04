package com.holdbetter.stonks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.holdbetter.stonks.databinding.StocksListFragmentBinding;
import com.holdbetter.stonks.viewmodel.StocksViewModel;

import org.jetbrains.annotations.NotNull;

public class FavouriteListFragment extends Fragment {

    StocksViewModel viewModel;

    public static FavouriteListFragment getInstance() {
        return new FavouriteListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StocksListFragmentBinding binding = StocksListFragmentBinding.inflate(inflater, container, false);
        StocksRecyclerAdapter adapter = createAdapter(binding);
        binding.stocksRecycler.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(StocksViewModel.class);
        adapter.setStocks(null);

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

}
