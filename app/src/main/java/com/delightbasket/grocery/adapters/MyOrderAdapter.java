package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemOrdersListBinding;
import com.delightbasket.grocery.model.OrderRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyOrderViewHolder> {
    private List<OrderRoot.Datum> data = new ArrayList<>();

    public OnOrderClickListnear getOnOrderClickListnear() {
        return onOrderClickListnear;
    }

    public void setOnOrderClickListnear(OnOrderClickListnear onOrderClickListnear) {
        this.onOrderClickListnear = onOrderClickListnear;
    }

    private OnOrderClickListnear onOrderClickListnear;
    private Context context;


    @Override
    public void onBindViewHolder(@NonNull MyOrderViewHolder holder, int position) {
        holder.binding.setOrder(data.get(position));
        holder.itemView.setOnClickListener(v -> onOrderClickListnear.onOrderClick(data.get(position), false));
        if(Objects.equals(data.get(position).getStatus(), "Cancelled")) {
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_red));
            holder.binding.btnCancel.setVisibility(View.GONE);
        } else if(Objects.equals(data.get(position).getStatus(), "Completed")) {
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_green));
            holder.binding.btnCancel.setVisibility(View.GONE);
        } else {
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_green));
        }
        if(Objects.equals(data.get(position).getStatus(), "Processing")) {
            holder.binding.btnCancel.setVisibility(View.VISIBLE);
        } else {
            holder.binding.btnCancel.setVisibility(View.GONE);
        }
        holder.binding.btnCancel.setOnClickListener(v -> onOrderClickListnear.onOrderClick(data.get(position), true));
        holder.binding.tvItemCount.setText("Total Items: ".concat(String.valueOf(data.get(position).getTotalItem())));
        holder.binding.tvItemPrice.setText("Amount: $".concat(String.valueOf(data.get(position).getTotalAmount())));

        String dateStr = data.get(position).getOrderedAt();
        holder.binding.tvDate.setText(dateStr);


    }

    @NonNull
    @Override
    public MyOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orders_list, parent, false);
        return new MyOrderViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<OrderRoot.Datum> data) {
        this.data.clear();
        for(int i = 0; i < data.size(); i++) {
            this.data.add(data.get(i));
        }
        notifyDataSetChanged();
    }

    public interface OnOrderClickListnear {
        void onOrderClick(OrderRoot.Datum order, Boolean clickCancel);
    }

    public static class MyOrderViewHolder extends RecyclerView.ViewHolder {
        ItemOrdersListBinding binding;

        public MyOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemOrdersListBinding.bind(itemView);
        }
    }
}
