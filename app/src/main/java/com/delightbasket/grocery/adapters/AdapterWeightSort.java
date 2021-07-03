package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemSearchProductBinding;
import com.delightbasket.grocery.databinding.ItemTextviewListweightpriceBinding;
import com.delightbasket.grocery.model.SortRoot;

import java.util.List;

public class AdapterWeightSort extends RecyclerView.Adapter<AdapterWeightSort.WeightViewHolder> {
    private final List<SortRoot.PriceUnitItem> priceUnit;
    private final OnSortWeightItemClick onSearchWeightItemClick;
    private final ItemSearchProductBinding binding1;
    private Context context;


    public AdapterWeightSort(List<SortRoot.PriceUnitItem> priceUnit, ItemSearchProductBinding binding1, OnSortWeightItemClick onSearchWeightItemClick) {

        this.priceUnit = priceUnit;
        this.onSearchWeightItemClick = onSearchWeightItemClick;
        this.binding1 = binding1;
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_textview_listweightprice, parent, false);

        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        SortRoot.PriceUnitItem unit = priceUnit.get(position);
        holder.binding.text1.setText(unit.getUnit());

        String s = binding1.tvProductWeight.getText().toString();
        if(unit.getUnit().equals(s)) {
            holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.color_black));
        }
        holder.binding.text1.setOnClickListener(v -> {
            holder.resetColor();
            binding1.tvProductWeight.setText(unit.getUnit());
            binding1.tvProductprice.setText(unit.getPrice());
            onSearchWeightItemClick.onWeightItemClick(priceUnit.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return priceUnit.size();
    }

    public interface OnSortWeightItemClick {
        void onWeightItemClick(SortRoot.PriceUnitItem priceUnit);
    }

    public class WeightViewHolder extends RecyclerView.ViewHolder {
        ItemTextviewListweightpriceBinding binding;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTextviewListweightpriceBinding.bind(itemView);
        }

        public void resetColor() {
            binding.text1.setTextColor(ContextCompat.getColor(context, R.color.color_black));
        }
    }
}
