package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.activities.ProductDetailActivity;
import com.delightbasket.grocery.databinding.ItemWishlistBinding;
import com.delightbasket.grocery.model.Wishlist;
import com.delightbasket.grocery.retrofit.Const;

import java.util.ArrayList;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistVIewHolder> {
    private List<Wishlist.Datum> data = new ArrayList<>();
    OnWishlistItemClickListnear wishlistItemClickListnear;
    private Context contex;

    public OnWishlistItemClickListnear getWishlistItemClickListnear() {
        return wishlistItemClickListnear;
    }

    public void setWishlistItemClickListnear(OnWishlistItemClickListnear wishlistItemClickListnear) {
        this.wishlistItemClickListnear = wishlistItemClickListnear;
    }

    public interface OnWishlistItemClickListnear {
        void onWishlistItemClick(int pos, Wishlist.Datum datum, ItemWishlistBinding binding, String work, Wishlist.PriceUnit priceUnit);
    }

    @NonNull
    @Override
    public WishlistVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        contex = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false);

        return new WishlistVIewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistVIewHolder holder, int position) {
        holder.setModel(data.get(position), position);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public void addData(List<Wishlist.Datum> data) {
        for(int i = 0; i < data.size(); i++) {
            this.data.add(data.get(i));
            notifyItemInserted(this.data.size() - 1);
        }

    }

    public class WishlistVIewHolder extends RecyclerView.ViewHolder {
        ItemWishlistBinding binding;

        public WishlistVIewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemWishlistBinding.bind(itemView);
        }

        public void setModel(Wishlist.Datum datum, int position) {

            Glide.with(binding.getRoot().getContext())
                    .load("http://delightbasket.in/admin/public/upload/product/" +
                            datum.getProductImage().get(0))
                    .placeholder(R.drawable.app_placeholder)
                    .into(binding.imgProduct);

            binding.tvProductName.setText(datum.getName());
            binding.tvProductWeight.setText(datum.getPriceUnit().get(0).getUnit());
            binding.tvProductPrice.setText(datum.getPriceUnit().get(0).getPrice().concat(contex.getString(R.string.currency) + " / ").concat(datum.getPriceUnit().get(0).getUnit()));
            binding.tvProductWeight.setOnClickListener(v -> wishlistItemClickListnear.onWishlistItemClick(position, datum, binding, "Weight", null));
            binding.imgDelete.setOnClickListener(v -> wishlistItemClickListnear.onWishlistItemClick(position, datum, binding, "Delete", null));

            binding.imgPlus.setOnClickListener(v -> wishlistItemClickListnear.onWishlistItemClick(position, datum, binding, "Addmore", getPriceUnit(binding.tvProductWeight.getText().toString(), datum)));
            binding.tvProductadd.setOnClickListener(v -> wishlistItemClickListnear.onWishlistItemClick(position, datum, binding, "Addmore", getPriceUnit(binding.tvProductWeight.getText().toString(), datum)));
            if(getPriceUnit(binding.tvProductWeight.getText().toString(), datum).getQuantity() != 0) {
                binding.imgMinus.setOnClickListener(v -> {
                    if(getPriceUnit(binding.tvProductWeight.getText().toString(), datum).getQuantity() != 0) {
                        Log.d("qqqu", "setModel:adapter  " + getPriceUnit(binding.tvProductWeight.getText().toString(), datum).getQuantity());
                        wishlistItemClickListnear.onWishlistItemClick(position, datum, binding, "Lessone", getPriceUnit(binding.tvProductWeight.getText().toString(), datum));
                    } else {
                        Toast.makeText(contex, "You reached minimum Limit", Toast.LENGTH_SHORT).show();
                    }
                });
                Glide.with(binding.getRoot().getContext())
                        .load(Const.BASE_IMG_URL + datum.getProductImage().get(0))
                        .placeholder(R.drawable.app_placeholder)
                        .into(binding.imgProduct);

            }
            binding.imgProduct.setOnClickListener(view -> {
                Intent intent = new Intent(contex, ProductDetailActivity.class);
                intent.putExtra(Const.ProductID, datum.getProductId());
                contex.startActivity(intent);

            });
        }

        private Wishlist.PriceUnit getPriceUnit(String toString, Wishlist.Datum model) {
            for(int i = 0; i <= model.getPriceUnit().size() - 1; i++) {

                if(model.getPriceUnit().get(i).getUnit().equals(toString)) {

                    return model.getPriceUnit().get(i);
                }
            }
            return null;
        }

    }

}
