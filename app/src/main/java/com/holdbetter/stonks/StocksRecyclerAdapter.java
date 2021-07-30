package com.holdbetter.stonks;

import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.holdbetter.stonks.databinding.StockListInstanceBinding;
import com.holdbetter.stonks.model.StockData;
import com.holdbetter.stonks.model.StockSocketData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class StocksRecyclerAdapter extends RecyclerView.Adapter<StocksRecyclerAdapter.StocksViewHolder> {
    private List<StockData> stocks;

    @NonNull
    @Override
    public StocksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        return new StocksViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StocksRecyclerAdapter.StocksViewHolder holder, int position) {
        StockData stockData = stocks.get(position);
        holder.stockName.setText(stockData.getSymbol());
        holder.stockPrice.setText(String.format("$%s", stockData.getCurrentPrice()));
        holder.companyName.setText(stockData.getCompanyName());
        holder.stockPriceChange.setText(stockData.getFormattedPriceChange());

        if (stockData.getFormattedPriceChange().charAt(0) == '+') {
            holder.stockPriceChange.setTextColor(ContextCompat.getColor(holder.stockPriceChange.getContext(), R.color.positiveColor));
        } else if (stockData.getFormattedPriceChange().charAt(0) == '-') {
            holder.stockPriceChange.setTextColor(ContextCompat.getColor(holder.stockPriceChange.getContext(), R.color.negativeColor));
        } else {
            holder.stockPriceChange.setTextColor(ContextCompat.getColor(holder.stockPriceChange.getContext(), R.color.colorPrimaryDark));
        }

        // set image
        TypedValue typedValue = new TypedValue();
        int[] margin = new int[]{R.attr.imageCornerRadius};
        int indexOfAttrTextSize = 0;
        TypedArray a = holder.root.getContext().obtainStyledAttributes(typedValue.data, margin);
        float marginSizeInDp = a.getDimension(indexOfAttrTextSize, -1);
        a.recycle();

        RequestOptions options = new RequestOptions()
                .transform(new CenterCrop(), new RoundedCorners((int) marginSizeInDp))
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(holder.symbolImage)
                .load(stockData.getUrl())
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.symbolImage);
    }

    @Override
    public long getItemId(int position) {
        return stocks.get(position).getSymbol().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? ViewType.EVEN.ordinal() : ViewType.ODD.ordinal();
    }

    @Override
    public int getItemCount() {
        return stocks != null ? stocks.size() : 0;
    }

    public void updateStocks(TreeSet<StockSocketData> stocksChanged) {
        ArrayList<StockData> stocksCopy = new ArrayList<>(stocks);
        for (StockSocketData stockSocketData : stocksChanged) {
            int index = -1;
            StockData stockToBeUpdated = null;
            for (StockData lastStockState : stocksCopy) {
                if (stockSocketData.getSymbol().equals(lastStockState.getSymbol())) {
                    index = stocks.indexOf(lastStockState);
                    stockToBeUpdated = lastStockState;
                    break;
                }
            }

            if (index != -1) {
                // updatePrice
                stockToBeUpdated.updatePrices(stockSocketData.getPrice());
                // notify
                notifyItemChanged(index);
                // delete from copy
                stocksCopy.remove(stockToBeUpdated);
            }
        }
    }

    public void setStocks(@Nullable List<StockData> stocks) {
        if (stocks != null) {
            this.stocks = stocks;
        } else {
            this.stocks.clear();
        }
        notifyDataSetChanged();
    }

    public static class StocksViewHolder extends RecyclerView.ViewHolder {
        private final TextView stockName;
        private final TextView companyName;
        private final TextView stockPriceChange;
        private final TextView stockPrice;
        private final ImageView symbolImage;
        private final ConstraintLayout root;

        public StocksViewHolder(@NonNull @NotNull StockListInstanceBinding binding) {
            super(binding.getRoot());

            root = binding.getRoot();
            stockName = binding.stockName;
            stockPrice = binding.stockPrice;
            symbolImage = binding.symbolImage;
            companyName = binding.companyName;
            stockPriceChange = binding.stockPriceChange;
        }
    }

    private enum ViewType {
        EVEN,
        ODD
    }
}
