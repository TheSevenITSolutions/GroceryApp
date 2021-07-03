package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemMyordersBinding;
import com.delightbasket.grocery.model.OrderDetailRoot;
import com.delightbasket.grocery.retrofit.Const;

import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OderItemViewHolder> {
    private List<OrderDetailRoot.ItemDetailsItem> itemDetails;

    public OrderItemsAdapter(List<OrderDetailRoot.ItemDetailsItem> itemDetails) {

        this.itemDetails = itemDetails;
    }

    @NonNull
    @Override
    public OderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myorders, parent, false);
        return new OderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OderItemViewHolder holder, int position) {
        OrderDetailRoot.ItemDetailsItem item = itemDetails.get(position);
        holder.binding.setItem(item);
        holder.binding.tvProductPrice.setText(holder.binding.getRoot().getContext().getString(R.string.currency) + item.getPrice());
        holder.binding.tvProductWeight.setText(item.getUnit().concat(" * " + item.getQuantity()));
        Glide.with(holder.binding.getRoot().getContext())
                .load(Const.BASE_IMG_URL + item.getProductImage().get(0))
                .placeholder(R.drawable.app_placeholder)
                .into(holder.binding.imageOrderProduct);
    }

    @Override
    public int getItemCount() {
        return itemDetails.size();
    }

    public static class OderItemViewHolder extends RecyclerView.ViewHolder {
        ItemMyordersBinding binding;

        public OderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemMyordersBinding.bind(itemView);
        }
    }
}
