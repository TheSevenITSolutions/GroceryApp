package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemDeliveryoptionsBinding;

public class DeliveryOptionsAdapter extends RecyclerView.Adapter<DeliveryOptionsAdapter.OptionsVIewHolder> {
    @NonNull
    @Override
    public OptionsVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deliveryoptions, parent, false);
        return new OptionsVIewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsVIewHolder holder, int position) {
//ll
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class OptionsVIewHolder extends RecyclerView.ViewHolder {
        ItemDeliveryoptionsBinding binding;

        public OptionsVIewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemDeliveryoptionsBinding.bind(itemView);
        }
    }
}
