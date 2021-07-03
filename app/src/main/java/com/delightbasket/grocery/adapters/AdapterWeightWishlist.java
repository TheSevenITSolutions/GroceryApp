package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemTextviewListweightpriceBinding;
import com.delightbasket.grocery.databinding.ItemWishlistBinding;
import com.delightbasket.grocery.model.Wishlist;

import java.util.List;

public class AdapterWeightWishlist extends RecyclerView.Adapter<AdapterWeightWishlist.WeightViewHolder> {
    private Context context;
    private com.delightbasket.grocery.databinding.ItemWishlistBinding binding1;

    private List<Wishlist.PriceUnit> priceUnit;

    public AdapterWeightWishlist(List<Wishlist.PriceUnit> priceUnit, ItemWishlistBinding binding1, OnWishlistWeightItemClickListnear onWishlistWeightItemClickListnear) {
        this.priceUnit = priceUnit;
        this.binding1 = binding1;

        this.onWishlistWeightItemClickListnear = onWishlistWeightItemClickListnear;
    }

    OnWishlistWeightItemClickListnear onWishlistWeightItemClickListnear;

    public interface OnWishlistWeightItemClickListnear {
        void onWeightClick(Wishlist.PriceUnit priceUnit);
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_textview_listweightprice, parent, false);
        context = parent.getContext();
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        Wishlist.PriceUnit unit = priceUnit.get(position);


        if(unit.getUnit().equals(binding1.tvProductWeight.getText().toString())) {
            holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.color_black));
        }
        holder.binding.text1.setText(unit.getUnit());
        holder.binding.text1.setOnClickListener(v -> {
            onWishlistWeightItemClickListnear.onWeightClick(unit);
            holder.resetColor();
            holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        });
    }

    @Override
    public int getItemCount() {
        return priceUnit.size();
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
