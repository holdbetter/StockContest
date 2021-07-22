package com.holdbetter.stonks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holdbetter.stonks.databinding.StockListInstanceBinding;
import com.holdbetter.stonks.model.StockHttpData;
import com.holdbetter.stonks.model.StockSocketData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import io.reactivex.rxjava3.core.Observable;

public class StocksRecyclerAdapter extends RecyclerView.Adapter<StocksRecyclerAdapter.StocksViewHolder>
{
    private List<StockHttpData> stocks;

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
        holder.price.setText(String.format("%s", stocks.get(position).getCurrentPrice()));
    }

    @Override
    public int getItemCount()
    {
        return stocks != null ? stocks.size() : 0;
    }

    public void setStocksChanged(TreeSet<StockSocketData> stocksChanged)
    {
        ArrayList<StockHttpData> stocksCopy = new ArrayList<>(stocks);
        for (StockSocketData stockSocketData : stocksChanged) {
            int index = -1;
            StockHttpData httpData = null;
            for (StockHttpData stockHttpData : stocksCopy) {
                if (stockSocketData.getS().equals(stockHttpData.getStockName())) {
                    index = stocks.indexOf(stockHttpData);
                    httpData = stockHttpData;
                    break;
                }
            }

            if (index != -1) {
                //updatePrice
                httpData.setCurrentPrice(stockSocketData.getP());
                //notify
//                notifyItemChanged(index);
                notifyItemChanged(index);
//                not
                //delete from copy
                stocksCopy.remove(httpData);
            }
        }
    }

    public void setStocks(@Nullable List<StockHttpData> stocks)
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
