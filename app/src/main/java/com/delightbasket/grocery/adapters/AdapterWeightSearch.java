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
import com.delightbasket.grocery.model.Search;

import java.util.List;

public class AdapterWeightSearch extends RecyclerView.Adapter<AdapterWeightSearch.WeightViewHolder> {
    private List<Search.PriceUnit> priceUnit;
    private OnSearchWeightItemClick onSearchWeightItemClick;
    private com.delightbasket.grocery.databinding.ItemSearchProductBinding binding1;
    private Context context;

    public AdapterWeightSearch(List<Search.PriceUnit> priceUnit, ItemSearchProductBinding binding1, OnSearchWeightItemClick onSearchWeightItemClick) {

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
        Search.PriceUnit unit = priceUnit.get(position);
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

    public interface OnSearchWeightItemClick {
        void onWeightItemClick(Search.PriceUnit priceUnit);
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
