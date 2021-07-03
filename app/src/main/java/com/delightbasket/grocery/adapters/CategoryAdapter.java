package com.delightbasket.grocery.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.activities.AllProductsActivity;
import com.delightbasket.grocery.databinding.ItemCategoiresBinding;
import com.delightbasket.grocery.databinding.ItemProductBinding;
import com.delightbasket.grocery.model.Categories;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    OnProductClickListener onProductClickListener;
    private List<Categories.Datum> mList = new ArrayList<>();

    public OnProductClickListener getOnProductClickListener() {
        return onProductClickListener;
    }

    public void setOnProductClickListener(OnProductClickListener onProductClickListener) {
        this.onProductClickListener = onProductClickListener;
    }

    public void updateData(List<Categories.Datum> mList) {

        for (int i = 0; i < mList.size(); i++) {
            this.mList.add(mList.get(i));
            notifyItemInserted(this.mList.size() - 1);
        }

    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoires, parent, false);
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
        ItemCategoiresBinding binding;
        ProductAdapter productAdapter = new ProductAdapter();

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void setModel(int position) {
            binding.recyclearProduct.setAdapter(productAdapter);
            productAdapter.updateData(mList.get(position).getProducts());
            binding.tvCatTitle.setText(mList.get(position).getCategoryName());

            productAdapter.setOnProductClickListener((product, binding, work, priceUnit, quantity) -> onProductClickListener.onProductClick(product, binding, work, priceUnit, quantity));
            binding.tvViewall.setOnClickListener(view -> {
                Intent intent = new Intent(binding.getRoot().getContext(), AllProductsActivity.class);
                intent.putExtra("cid", mList.get(position).getCategoryId());
                intent.putExtra("cname", mList.get(position).getCategoryName());
                binding.getRoot().getContext().startActivity(intent);
            });

        }
    }
}
