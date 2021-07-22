package com.holdbetter.stonks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holdbetter.stonks.databinding.StockListInstanceBinding;
import com.holdbetter.stonks.model.StockHttpGetData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StocksRecyclerAdapter extends RecyclerView.Adapter<StocksRecyclerAdapter.StocksViewHolder>
{
    private List<StockHttpGetData> stocks;

    @NonNull
    @NotNull
    @Override
    public StocksViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        View stockItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_instance, parent, false);
        return new StocksViewHolder(StockListInstanceBinding.bind(stockItem));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StocksRecyclerAdapter.StocksViewHolder holder, int position)
    {
        holder.stockNameView.setText(stocks.get(position).getStockName());
        holder.price.setText(stocks.get(position).getCurrentPrice());
    }

    @Override
    public int getItemCount()
    {
        return stocks != null ? stocks.size() : 0;
    }

    public void setStocks(@Nullable List<StockHttpGetData> stocks)
    {
        if (stocks != null) {
            this.stocks = stocks;
        } else {
            this.stocks.clear();
        }
        notifyDataSetChanged();
    }

    public static class StocksViewHolder extends RecyclerView.ViewHolder {
        private final TextView stockNameView;
        private final TextView price;

        public StocksViewHolder(@NonNull @NotNull StockListInstanceBinding binding)
        {
            super(binding.getRoot());

            stockNameView = binding.stockNameView;
            price = binding.stockPrice;
        }
    }
}
