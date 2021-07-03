package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.databinding.ActivityProductDetailBinding;
import com.delightbasket.grocery.databinding.ItemProductPriceBinding;
import com.delightbasket.grocery.model.ProductMain;
import com.delightbasket.grocery.retrofit.Const;

import java.util.List;

public class ProductPriceAdapter extends RecyclerView.Adapter<ProductPriceAdapter.PriceViewHolder> {
    Context context;
    ItemProductPriceBinding binding;
    OnPriceAdapterClickListnear onPriceAdapterClickListnear;
    private boolean isStock;
    private List<ProductMain.PriceUnit> priceUnit;
    com.delightbasket.grocery.databinding.ActivityProductDetailBinding binding1;
    private int checkPos;

    public ProductPriceAdapter(List<ProductMain.PriceUnit> priceUnit, ActivityProductDetailBinding binding1, OnPriceAdapterClickListnear onPriceAdapterClickListnear, boolean isStock) {

        this.priceUnit = priceUnit;
        this.binding1 = binding1;
        this.onPriceAdapterClickListnear = onPriceAdapterClickListnear;
        this.isStock = isStock;
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {

        if(checkPos == position) {
            holder.binding.radioBtn.setChecked(true);
            holder.binding.dot.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorAccent));

        } else {
            holder.binding.radioBtn.setChecked(false);
            holder.binding.lytPlusMinus.setVisibility(View.INVISIBLE);
            holder.binding.tvAdd.setVisibility(View.INVISIBLE);
            holder.binding.dot.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.color_gray_lightbtn));
        }

        holder.setModel(priceUnit.get(position), position);
        holder.binding.radioBtn.setOnClickListener(v -> {

            checkPos = position;
            notifyDataSetChanged();

        });
        holder.itemView.setOnClickListener(v -> {
            holder.binding.radioBtn.setChecked(true);
            checkPos = position;
            notifyDataSetChanged();
        });
        holder.binding.tvProductweight.setOnClickListener(v -> {
            holder.binding.radioBtn.setChecked(true);
            checkPos = position;
            notifyDataSetChanged();
        });

    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_price, parent, false);
        return new PriceViewHolder(view);

    }

    public interface OnPriceAdapterClickListnear {
        void onPriceAdapterClick(ProductMain.PriceUnit unit, ItemProductPriceBinding binding, String work);
    }

    @Override
    public int getItemCount() {
        return priceUnit.size();
    }


    public class PriceViewHolder extends RecyclerView.ViewHolder {
        ItemProductPriceBinding binding;

        public PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProductPriceBinding.bind(itemView);
        }

        private long getCartdata(String id) {
            AppDatabase db = Room.databaseBuilder(context,
                    AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();
            Log.d("qqqq1", "getCartdata: " + id);
            if(!db.cartDao().getCartProduct(id).isEmpty()) {
                return db.cartDao().getCartProduct(id).get(0).getQuantity();
            } else {
                return 0;
            }


        }

        public void setModel(ProductMain.PriceUnit priceUnit, int position) {
            binding.tvProductweight.setText(priceUnit.getUnit());
            binding.tvProductPrice.setText(priceUnit.getPrice().concat(context.getString(R.string.currency)));

            if(checkPos == position) {
                long quantity = getCartdata(priceUnit.getPriceUnitId());
                if(quantity == 0) {
                    binding.lytPlusMinus.setVisibility(View.INVISIBLE);
                    binding.tvAdd.setVisibility(View.VISIBLE);
                } else {
                    binding.tvAdd.setVisibility(View.INVISIBLE);
                    binding.lytPlusMinus.setVisibility(View.VISIBLE);
                    binding.tvQuantity.setText(String.valueOf(quantity));
                }
            } else {
                binding.lytPlusMinus.setVisibility(View.INVISIBLE);
                binding.tvAdd.setVisibility(View.INVISIBLE);

            }
            if (isStock) {
                binding.tvProductoutofstock.setVisibility(View.INVISIBLE);

            } else {
                binding.lytPlusMinus.setVisibility(View.INVISIBLE);
                binding.tvAdd.setVisibility(View.INVISIBLE);
                binding.tvProductoutofstock.setVisibility(View.VISIBLE);
            }
            binding.tvAdd.setOnClickListener(v -> onPriceAdapterClickListnear.onPriceAdapterClick(priceUnit, binding, "addOne"));
            binding.imgPlus.setOnClickListener(v -> onPriceAdapterClickListnear.onPriceAdapterClick(priceUnit, binding, "addOne"));
            binding.imgMinus.setOnClickListener(v -> onPriceAdapterClickListnear.onPriceAdapterClick(priceUnit, binding, "lessOne"));
        }
    }

}
