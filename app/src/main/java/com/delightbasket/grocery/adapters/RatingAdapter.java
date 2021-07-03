package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemRatingBinding;
import com.delightbasket.grocery.model.RatingRoot;

import java.util.ArrayList;
import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {
    private List<RatingRoot.DataItem> mList = new ArrayList<>();

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {

        RatingRoot.DataItem datam = mList.get(position);

        if(datam.getOrderId() != null) {
            holder.binding.tvOrderId.setText(datam.getOrderId());
        }
        if(datam.getReview() != null) {
            holder.binding.msg.setText(datam.getReview());
        }
        holder.binding.ratingbar.setRating(datam.getRating());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addData(List<RatingRoot.DataItem> mList) {
        for(int i = 0; i < mList.size(); i++) {
            this.mList.add(mList.get(i));
            notifyItemInserted(this.mList.size());
        }

    }

    public class RatingViewHolder extends RecyclerView.ViewHolder {
        ItemRatingBinding binding;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRatingBinding.bind(itemView);
        }
    }
}
