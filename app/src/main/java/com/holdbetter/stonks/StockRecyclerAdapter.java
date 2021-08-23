package com.holdbetter.stonks;

import android.content.res.TypedArray;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.holdbetter.stonks.databinding.StockListInstanceBinding;
import com.holdbetter.stonks.model.room.Price;
import com.holdbetter.stonks.model.room.Symbol;
import com.holdbetter.stonks.model.room.SymbolWithPrices;
import com.holdbetter.stonks.viewmodel.StockViewModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

import io.reactivex.rxjava3.schedulers.Schedulers;

public class StockRecyclerAdapter extends RecyclerView.Adapter<StockRecyclerAdapter.StockViewHolder> {
    private List<SymbolWithPrices> stocks;
    private final StockViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;

    public StockRecyclerAdapter(StockViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View stockItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_instance, parent, false);
        StockListInstanceBinding binding = StockListInstanceBinding.bind(stockItem);
        View root = binding.getRoot();

        switch (ViewType.values()[viewType]) {
            case EVEN:
                root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.symbol_instance_shape_even));
                break;
            case ODD:
                root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.symbol_instance_shape_odd));
                break;
        }

        return new StockViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Symbol symbol = stocks.get(position).symbol;
        List<Price> symbolPriceList = stocks.get(position).priceList;
        Price lastPrice = symbolPriceList.get(symbolPriceList.size() - 1);

        Price previousClose = symbolPriceList.stream().sorted(Comparator.comparingLong(p -> p.latestUpdateInMillis))
                .filter(p -> p.previousClose != null)
                .findFirst()
                .get();

        holder.stockName.setText(symbol.getName());
        holder.stockPrice.setText(lastPrice.getFormattedLatestPrice());
        holder.companyName.setText(symbol.companyName);

        String formattedPriceChange = lastPrice.getFormattedPriceChange(previousClose.previousClose);
        holder.stockPriceChange.setText(formattedPriceChange);

        if (formattedPriceChange.charAt(0) == '+') {
            holder.stockPriceChange.setTextColor(ContextCompat.getColor(holder.stockPriceChange.getContext(), R.color.positiveColor));
        } else if (formattedPriceChange.charAt(0) == '-') {
            holder.stockPriceChange.setTextColor(ContextCompat.getColor(holder.stockPriceChange.getContext(), R.color.negativeColor));
        } else {
            holder.stockPriceChange.setTextColor(ContextCompat.getColor(holder.stockPriceChange.getContext(), R.color.colorPrimaryDark));
        }

        setupSymbolImage(holder, symbol);

        viewModel.getDatabase()
                .getFavouriteDao()
                .isFavouriteSymbol(symbol.name)
                .observe(lifecycleOwner, holder.favouriteIndicator::setSelected);

        holder.favouriteIndicator.setOnClickListener(v -> {
            symbol.isFavourite = !v.isSelected();
            viewModel.getDatabase().getSymbolDao()
                    .updateSymbol(symbol)
                    .subscribeOn(Schedulers.io())
                    .subscribe(rowCount -> Log.d("FAV_UPDATE", "Count: " + rowCount));
        });
    }

    @Override
    public long getItemId(int position) {
        return stocks.get(position).symbol.name.hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? ViewType.EVEN.ordinal() : ViewType.ODD.ordinal();
    }

    @Override
    public int getItemCount() {
        return stocks != null ? stocks.size() : 0;
    }

    public void setStocks(@Nullable List<SymbolWithPrices> stocks) {
        if (stocks != null) {
            this.stocks = stocks;
            notifyDataSetChanged();
        }
    }

    public static class StockViewHolder extends RecyclerView.ViewHolder {
        private final TextView stockName;
        private final TextView companyName;
        private final TextView stockPriceChange;
        private final TextView stockPrice;
        private final ImageView symbolImage;
        private final ImageView favouriteIndicator;
        private final ConstraintLayout root;

        public StockViewHolder(@NonNull @NotNull StockListInstanceBinding binding) {
            super(binding.getRoot());

            root = binding.getRoot();
            stockName = binding.stockName;
            stockPrice = binding.stockPrice;
            symbolImage = binding.symbolImage;
            favouriteIndicator = binding.favouriteIndicator;
            companyName = binding.companyName;
            stockPriceChange = binding.stockPriceChange;
        }
    }

    private void setupSymbolImage(@NonNull StockViewHolder holder, Symbol symbol) {
        RequestOptions options = new RequestOptions()
                .transform(new CenterCrop(), new RoundedCorners(getImageCornerRadiusInDp(holder)))
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(holder.symbolImage)
                .load(symbol.logoUrl)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.symbolImage);
    }

    private int getImageCornerRadiusInDp(@NonNull StockViewHolder holder) {
        TypedValue typedValue = new TypedValue();
        int[] margin = new int[]{R.attr.imageCornerRadius};
        int indexOfAttrTextSize = 0;
        TypedArray a = holder.root.getContext().obtainStyledAttributes(typedValue.data, margin);
        float marginSizeInDp = a.getDimension(indexOfAttrTextSize, -1);
        a.recycle();
        return (int) marginSizeInDp;
    }

    private enum ViewType {
        EVEN,
        ODD
    }
}
