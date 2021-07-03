package com.delightbasket.grocery.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemProductImageBinding;
import com.delightbasket.grocery.retrofit.Const;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class ProductImageViewPager extends SliderViewAdapter<ProductImageViewPager.SliderAdapterVH> {
    private List<String> productImages;
    private OnImageClickListnear onImageClickListnear;

    public ProductImageViewPager(List<String> productImage, OnImageClickListnear onImageClickListnear) {

        this.productImages = productImage;
        this.onImageClickListnear = onImageClickListnear;
    }

    @Override
    public int getCount() {
        return productImages.size();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_image, parent, false);

        return new SliderAdapterVH(layout);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        String image = productImages.get(position);
        Glide.with(viewHolder.binding.getRoot().getContext())
//                .load(Const.BASE_IMG_URL + image)
                .load(image)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        viewHolder.binding.pBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .placeholder(R.drawable.app_placeholder)
                .into(viewHolder.binding.ivProduct);
        viewHolder.binding.ivProduct.setOnClickListener(v -> onImageClickListnear.onImageClick());


    }

    public interface OnImageClickListnear {
        void onImageClick();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        ItemProductImageBinding binding;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            binding = ItemProductImageBinding.bind(itemView);
        }
    }
}
