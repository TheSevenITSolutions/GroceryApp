package com.delightbasket.grocery.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.activities.SubscribeProductActivity;
import com.delightbasket.grocery.databinding.ItemMainCategoryImagesBinding;
import com.delightbasket.grocery.databinding.ItemProductBinding;
import com.delightbasket.grocery.model.Categories;
import com.delightbasket.grocery.model.MainCategory;

import java.util.ArrayList;
import java.util.List;

public class SubsMainCategoryAdapter extends RecyclerView.Adapter<SubsMainCategoryAdapter.CategoryViewHolder> {

    OnProductClickListener onProductClickListener;
    private List<MainCategory.Data1> mList = new ArrayList<>();

    public OnProductClickListener getOnProductClickListener() {
        return onProductClickListener;
    }

    public void setOnProductClickListener(OnProductClickListener onProductClickListener) {
        this.onProductClickListener = onProductClickListener;
    }

    public void updateData(List<MainCategory.Data1> mList) {

        for (int i = 0; i < mList.size(); i++) {
            this.mList.add(mList.get(i));
            notifyItemInserted(this.mList.size() - 1);
        }

    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_category_images, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

        holder.setModel(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnProductClickListener {
        void onProductClick(Categories.Product product, ItemProductBinding binding, String work, Categories.PriceUnit priceUnit, long quantity);

        void o();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemMainCategoryImagesBinding binding;


        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void setModel(int position) {
            binding.txtCategory.setText(mList.get(position).category_name);
            Glide.with(binding.getRoot().getContext())
                    .load(mList.get(position).category_image)
                    .circleCrop()
                    .placeholder(R.drawable.app_placeholder)
                    .into(binding.imgCategory);

//            productAdapter.setOnProductClickListener((product, binding, work, priceUnit, quantity) -> onProductClickListener.onProductClick(product, binding, work, priceUnit, quantity));
            binding.imgCategory.setOnClickListener(view -> {
                Intent intent = new Intent(binding.getRoot().getContext(), SubscribeProductActivity.class);
                intent.putExtra("cid", mList.get(position).id);
                intent.putExtra("cname", mList.get(position).category_name);
                binding.getRoot().getContext().startActivity(intent);
            });

        }
    }
}

