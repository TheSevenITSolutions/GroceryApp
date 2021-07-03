package com.delightbasket.grocery.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ImageSliderLayoutItemBinding;
import com.delightbasket.grocery.model.BannerRoot;
import com.delightbasket.grocery.retrofit.Const;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapterExample extends
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private List<BannerRoot.Datum> mSliderItems = new ArrayList<>();

    public SliderAdapterExample(List<BannerRoot.Datum> mSliderItems) {

        this.mSliderItems = mSliderItems;
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        BannerRoot.Datum sliderItem = mSliderItems.get(position);


        Glide.with(viewHolder.itemView)
                .load(sliderItem.getBannerImg())
                .fitCenter()
                .placeholder(R.drawable.app_placeholder)
                .into(viewHolder.binding.imgBanner);


    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {


        ImageSliderLayoutItemBinding binding;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            binding = ImageSliderLayoutItemBinding.bind(itemView);
        }
    }

}
