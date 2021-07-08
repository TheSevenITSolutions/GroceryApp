package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemSubsuseridBinding;
import com.delightbasket.grocery.model.ProductsItem;
import com.delightbasket.grocery.retrofit.Const;

import java.util.ArrayList;
import java.util.List;

public class SubsUserAdapter extends RecyclerView.Adapter<SubsUserAdapter.SubsViewHolder> {
    private List<ProductsItem> userId = new ArrayList<>();


    public SubsUserAdapter() {
    }


    public void updateData(List<ProductsItem> mList) {

        for (int i = 0; i < mList.size(); i++) {
            this.userId.add(mList.get(i));
            notifyItemInserted(this.userId.size() - 1);
        }

    }

    @NonNull
    @Override
    public SubsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subsuserid, parent, false);
        return new SubsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubsViewHolder holder, int position) {
        Glide.with(holder.binding.getRoot().getContext())
//                .load(userId.get(position).getProductImage())
                .load(Const.BASE_IMG_URL + Const.Product + "/" + userId.get(position).getProductImage())
                .placeholder(R.drawable.app_placeholder)
                .into(holder.binding.userproductImage);

        holder.binding.userproductname.setText(userId.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return userId.size();
    }

    public class SubsViewHolder extends RecyclerView.ViewHolder {
        ItemSubsuseridBinding binding;

        public SubsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSubsuseridBinding.bind(itemView);
        }
    }
}
