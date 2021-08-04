package com.holdbetter.stonks;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StockPagerAdapter extends FragmentStateAdapter {
    public StockPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return StocksListFragment.getInstance();
            case 1:
                return FavouriteListFragment.getInstance();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
