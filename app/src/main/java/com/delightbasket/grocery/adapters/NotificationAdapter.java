package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemNotificationBinding;
import com.delightbasket.grocery.model.NotificationRoot;
import com.delightbasket.grocery.retrofit.Const;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationRoot.DataItem> data = new ArrayList<>();

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        holder.setData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<NotificationRoot.DataItem> data) {

        for(int i = 0; i < data.size(); i++) {
            this.data.add(data.get(i));
            notifyItemInserted(this.data.size());
        }

    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNotificationBinding.bind(itemView);
        }

        public void setData(NotificationRoot.DataItem dataItem) {
            if(dataItem.getMessage() != null && !dataItem.getMessage().equals("")) {
                binding.tvNotification.setText(dataItem.getMessage());
            } else {
                binding.tvNotification.setVisibility(View.GONE);
            }

            if(dataItem.getTitle() != null && !dataItem.getTitle().equals("")) {
                binding.tvtitle.setText(dataItem.getTitle());
            } else {
                binding.tvtitle.setVisibility(View.GONE);
            }
            if(dataItem.getImage() == null) {
                binding.image.setVisibility(View.GONE);
            } else {
                binding.image.setVisibility(View.VISIBLE);
                Glide.with(binding.getRoot().getContext())
                        .load(Const.BASE_IMG_URL + dataItem.getImage())
                        .placeholder(R.drawable.app_placeholder)
                        .into(binding.image);
            }
        }
    }
}
