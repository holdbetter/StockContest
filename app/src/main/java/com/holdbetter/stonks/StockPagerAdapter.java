package com.holdbetter.stonks;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Arrays;

public class StockPagerAdapter extends FragmentStateAdapter {
    private final Fragment[] fragments = new Fragment[] { StocksListFragment.getInstance(), FavouriteListFragment.getInstance() };


    public StockPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }
}
