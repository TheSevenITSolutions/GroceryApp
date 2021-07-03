package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemComplainsBinding;
import com.delightbasket.grocery.model.ComplainRoot;

import java.util.ArrayList;
import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplainViewHolder> {
    private List<ComplainRoot.DataItem> data = new ArrayList<>();
    ComplainRoot.DataItem complain;



    @NonNull
    @Override
    public ComplainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complains, parent, false);
        return new ComplainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplainViewHolder holder, int position) {
        complain = data.get(position);
        holder.binding.tvDate.setText(complain.getCreatedAt());
        holder.binding.tvId.setText(complain.getComplaintId());
        holder.binding.tvStatus.setText(complain.getStatus());
        holder.binding.tvTitle.setText(complain.getDescription());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<ComplainRoot.DataItem> data) {

        for(int i = 0; i < data.size(); i++) {
            this.data.add(data.get(i));
            notifyItemInserted(this.data.size());
        }


    }

    public static class ComplainViewHolder extends RecyclerView.ViewHolder {
        ItemComplainsBinding binding;

        public ComplainViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemComplainsBinding.bind(itemView);
        }
    }
}
