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

import com.holdbetter.stonks.databinding.StockListInstanceBinding;
import com.holdbetter.stonks.model.StockHttpData;
import com.holdbetter.stonks.model.StockSocketData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class StocksRecyclerAdapter extends RecyclerView.Adapter<StocksRecyclerAdapter.StocksViewHolder> {
    private List<StockHttpData> stocks;

    @NonNull
    @Override
    public StocksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View stockItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_instance, parent, false);
        return new StocksViewHolder(StockListInstanceBinding.bind(stockItem));
    }

    @Override
    public void onBindViewHolder(@NonNull StocksRecyclerAdapter.StocksViewHolder holder, int position) {
        StockHttpData stockData = stocks.get(position);
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

        // set background type
        if (position % 2 == 0) {
            holder.root.setBackground(ContextCompat.getDrawable(holder.root.getContext(), R.drawable.symbol_instance_shape_even));
        } else {
            holder.root.setBackground(ContextCompat.getDrawable(holder.root.getContext(), R.drawable.symbol_instance_shape_odd));
        }

        // set image
//        Glide.with(holder.symbolImage)
//                .load()
    }

    @Override
    public long getItemId(int position) {
        return stocks.get(position).getSymbol().hashCode();
    }

    @Override
    public int getItemCount() {
        return stocks != null ? stocks.size() : 0;
    }

    public void setStocksChanged(TreeSet<StockSocketData> stocksChanged) {
        ArrayList<StockHttpData> stocksCopy = new ArrayList<>(stocks);
        for (StockSocketData stockSocketData : stocksChanged) {
            int index = -1;
            StockHttpData httpData = null;
            for (StockHttpData stockHttpData : stocksCopy) {
                if (stockSocketData.getS().equals(stockHttpData.getSymbol())) {
                    index = stocks.indexOf(stockHttpData);
                    httpData = stockHttpData;
                    break;
                }
            }

            if (index != -1) {
                //updatePrice
                httpData.setCurrentPrice(stockSocketData.getP());
                //notify
                notifyItemChanged(index);
                //delete from copy
                stocksCopy.remove(httpData);
            }
        }
    }

    public void setStocks(@Nullable List<StockHttpData> stocks) {
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
}
