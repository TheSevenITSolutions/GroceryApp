package com.delightbasket.grocery.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.delightbasket.grocery.fragments.OrderItemsFragment;
import com.delightbasket.grocery.fragments.OrderSummaryFragment;
import com.delightbasket.grocery.model.OrderDetailRoot;
import com.google.gson.Gson;

public class OrderDetailFragmentsPagerAdapter extends FragmentPagerAdapter {

    private OrderDetailRoot.Data orderDetail;
    private String status;

    public OrderDetailFragmentsPagerAdapter(@NonNull FragmentManager fm, int behavior, OrderDetailRoot.Data orderDetail, String status) {
        super(fm, behavior);
        this.orderDetail = orderDetail;
        this.status = status;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("orderDetail", new Gson().toJson(orderDetail));
        if(position == 0) {

            OrderSummaryFragment orderSummaryFragment = new OrderSummaryFragment(status);
            orderSummaryFragment.setArguments(bundle);
            return orderSummaryFragment;
        } else {

            OrderItemsFragment orderItemsFragment = new OrderItemsFragment();
            orderItemsFragment.setArguments(bundle);
            return orderItemsFragment;
        }
    }


    @Override
    public int getCount() {
        return 2;
    }
}
