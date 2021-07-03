package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemCouponlistBinding;
import com.delightbasket.grocery.model.Coupon;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CoponViewHolder> {
    private List<Coupon.Datum> coupons;
    private long subtotal;
    private OnCouponClickListnear couponClickListnear;
    private Context context;

    public CouponAdapter(List<Coupon.Datum> coupons, long subtotal, OnCouponClickListnear couponClickListnear) {

        this.coupons = coupons;
        this.subtotal = subtotal;
        this.couponClickListnear = couponClickListnear;
    }

    @NonNull
    @Override
    public CoponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_couponlist, parent, false);
        return new CoponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoponViewHolder holder, int position) {

        holder.binding.setCoupon(coupons.get(position));
        holder.binding.tvMinAmount.setTextColor(ContextCompat.getColor(context, R.color.color_red));

        if(coupons.get(position).getDiscountType() == 1) {
            holder.binding.tvDiscount.setText(coupons.get(position).getCouponDiscount() + context.getString(R.string.currency));
        } else {
            holder.binding.tvDiscount.setText(coupons.get(position).getCouponDiscount() + "%");
        }
        long minAmount = Long.parseLong(coupons.get(position).getMinimumAmount());
        if(minAmount <= subtotal) {
            holder.binding.tvApply.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvApply.setVisibility(View.GONE);
        }
        holder.binding.tvApply.setOnClickListener(v -> couponClickListnear.onCouponClick(coupons.get(position)));

    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public interface OnCouponClickListnear {
        void onCouponClick(Coupon.Datum coupon);
    }

    public class CoponViewHolder extends RecyclerView.ViewHolder {
        ItemCouponlistBinding binding;

        public CoponViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCouponlistBinding.bind(itemView);
        }
    }
}
