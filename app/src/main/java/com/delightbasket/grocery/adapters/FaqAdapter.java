package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemFaqsBinding;
import com.delightbasket.grocery.model.FaqRoot;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {
    private List<FaqRoot.DataItem> data;

    public FaqAdapter(List<FaqRoot.DataItem> data) {

        this.data = data;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faqs, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {

        FaqRoot.DataItem model = data.get(position);
        if(model != null) {
            if(model.getQuestion() != null) {
                holder.binding.tvquestion.setText(model.getQuestion());
            }
            if(model.getAnswer() != null) {
                holder.binding.tvans.setText(model.getAnswer());
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class FaqViewHolder extends RecyclerView.ViewHolder {
        ItemFaqsBinding binding;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemFaqsBinding.bind(itemView);
        }
    }
}
