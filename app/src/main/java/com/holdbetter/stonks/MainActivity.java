package com.holdbetter.stonks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.TypedValue;

import com.holdbetter.stonks.databinding.ActivityMainBinding;
import com.holdbetter.stonks.utility.SupportedIndiceNames;
import com.holdbetter.stonks.viewmodel.StockViewModel;

public class MainActivity extends AppCompatActivity
{
    public static final String INDICE_TO_REQUEST = SupportedIndiceNames.DOW_JONES;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // ViewModel initialize
        new ViewModelProvider(this).get(StockViewModel.class);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        ConstraintLayout root = binding.getRoot();
        setContentView(root);

        binding.stocksHeader.getRoot().setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        binding.stocksHeader.getRoot().setText(R.string.stocksHeader);
        binding.favouriteHeader.getRoot().setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        binding.favouriteHeader.getRoot().setText(R.string.favouriteHeader);

        StockPagerAdapter pagerAdapter = new StockPagerAdapter(this);
        binding.stockPager.setAdapter(pagerAdapter);
        binding.stockPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position) {
                    case 0:
                        binding.favouriteHeader.getRoot().setSelected(false);
                        binding.favouriteHeader.getRoot().setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        binding.stocksHeader.getRoot().setSelected(true);
                        binding.stocksHeader.getRoot().setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                        binding.stocksHeader.headerTitle.requestLayout();
                        break;
                    case 1:
                        binding.stocksHeader.getRoot().setSelected(false);
                        binding.stocksHeader.getRoot().setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        binding.favouriteHeader.getRoot().setSelected(true);
                        binding.favouriteHeader.getRoot().setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                        binding.favouriteHeader.headerTitle.requestLayout();
                        break;
                }
            }
        });
    }
}