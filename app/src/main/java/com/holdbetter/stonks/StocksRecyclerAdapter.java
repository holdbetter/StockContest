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
import com.holdbetter.stonks.databinding.StockListInstanceBinding;
import com.holdbetter.stonks.model.StockHttpData;
import com.holdbetter.stonks.model.StockSocketData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
        holder.symbolPrice.setText(String.format("$%s", stocks.get(position).getCurrentPrice()));

        // set background type
        if (position % 2 == 0) {
            holder.root.setBackground(ContextCompat.getDrawable(holder.root.getContext(), R.drawable.symbol_instance_shape_even));
        } else {
            holder.root.setBackground(ContextCompat.getDrawable(holder.root.getContext(), R.drawable.symbol_instance_shape_odd));
        }

        // set margin for first and last item
        setMarginToRoot(holder, position);

        // set image
//        Glide.with(holder.symbolImage)
//                .load()
    }

    private void setMarginToRoot(@NotNull StocksViewHolder holder, int position) {
        if (position == 0) {
            if (holder.root.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.root.getLayoutParams();

                TypedValue typedValue = new TypedValue();
                int[] margin = new int[]{R.attr.listSpaceBetweenItems};
                int indexOfAttrTextSize = 0;
                TypedArray a = holder.root.getContext().obtainStyledAttributes(typedValue.data, margin);
                float marginSizeInDp = a.getDimension(indexOfAttrTextSize, -1);
                a.recycle();

                params.setMargins(0, (int) marginSizeInDp, 0, 0);
            }
        }
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
        private final TextView symbolPrice;
        private final ImageView symbolImage;
        private final ConstraintLayout root;

        public StocksViewHolder(@NonNull @NotNull StockListInstanceBinding binding)
        {
            super(binding.getRoot());

            root = binding.getRoot();
            stockNameView = binding.stockName;
            symbolPrice = binding.stockPrice;
            symbolImage = binding.symbolImage;
        }
    }
}
