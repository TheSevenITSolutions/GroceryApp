package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.activities.ProductDetailActivity;
import com.delightbasket.grocery.dao.CartOffline;
import com.delightbasket.grocery.databinding.ItemCartBinding;
import com.delightbasket.grocery.retrofit.Const;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartVirwHolder> {
    private List<CartOffline> list = new ArrayList<>();
    OnCartItemClick onCartItemClick;
    private Context context;

    public OnCartItemClick getOnCartItemClick() {
        return onCartItemClick;
    }

    public void setOnCartItemClick(OnCartItemClick onCartItemClick) {
        this.onCartItemClick = onCartItemClick;
    }


    @Override
    public void onBindViewHolder(@NonNull CartVirwHolder holder, int position) {
        CartOffline product = list.get(position);


        holder.setModel(product, position);
    }

    @NonNull
    @Override
    public CartVirwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartVirwHolder(view);
    }

    public void removeItem(int position) {
        Log.d("cartfrag", "removeItem: " + list.get(position));
        list.remove(position);
        notifyDataSetChanged();
    }

    public interface OnCartItemClick {
        void onCartItemClick(CartOffline product, String work, ItemCartBinding binding, int position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addData(List<CartOffline> list) {
        for(int i = 0; i < list.size(); i++) {
            this.list.add(list.get(i));
            notifyItemInserted(this.list.size());
        }

    }

    public class CartVirwHolder extends RecyclerView.ViewHolder {
        ItemCartBinding binding;
        public CartVirwHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemCartBinding.bind(itemView);
        }

        public void setModel(CartOffline product, int position) {
            binding.tvProductName.setText(product.getName());
            binding.tvProductPrice.setText(product.getPrice().concat(context.getString(R.string.currency)));
            binding.tvProductWeight.setText(product.getUnit());
            binding.tvQuantity.setText(String.valueOf(product.getQuantity()));


            Glide.with(binding.getRoot().getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.app_placeholder)
                    .into(binding.imgProduct);
            initListnear(product, position);
        }

        private void initListnear(CartOffline product, int position) {
            binding.imgDelete.setOnClickListener(v -> onCartItemClick.onCartItemClick(product, "Delete", binding, position));
            binding.imgPlus.setOnClickListener(v -> onCartItemClick.onCartItemClick(product, "Addmore", binding, position));
            binding.imgMinus.setOnClickListener(v -> onCartItemClick.onCartItemClick(product, "LessOne", binding, position));
            binding.tvMovetoWishlist.setOnClickListener(v -> onCartItemClick.onCartItemClick(product, "moveToWishlist", binding, position));
            binding.imgProduct.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra(Const.ProductID, product.getPid());
                context.startActivity(intent);
            });
        }
    }
}
