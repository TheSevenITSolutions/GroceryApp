package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.dao.CartOffline;
import com.delightbasket.grocery.databinding.ItemOrdersTextBinding;
import com.delightbasket.grocery.retrofit.Const;

import java.util.List;

public class OrderItemsTextAdapter extends RecyclerView.Adapter<OrderItemsTextAdapter.OrderItemTextViewHOlder> {
    private List<CartOffline> items;

    public OrderItemsTextAdapter(List<CartOffline> items) {

        this.items = items;
    }

    @NonNull
    @Override
    public OrderItemTextViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orders_text, parent, false);
        return new OrderItemTextViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemTextViewHOlder holder, int position) {
        CartOffline item = items.get(position);
        holder.binding.tvProductName.setText(item.getName());
        holder.binding.tvPrice.setText(holder.binding.getRoot().getContext().getString(R.string.currency) + item.getPrice());
        holder.binding.tvQuantity.setText(item.getUnit().concat(" " + item.getQuantity()));
        holder.binding.tvWeight.setText(item.getUnit());

        Glide.with(holder.binding.getRoot().getContext())
                .load(Const.BASE_IMG_URL + item.getImageUrl())
                .placeholder(R.drawable.app_placeholder)
                .into(holder.binding.imgproduct);


//ll
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class OrderItemTextViewHOlder extends RecyclerView.ViewHolder {
        ItemOrdersTextBinding binding;
        public OrderItemTextViewHOlder(@NonNull View itemView) {
            super(itemView);
            binding = ItemOrdersTextBinding.bind(itemView);
        }
    }
}
