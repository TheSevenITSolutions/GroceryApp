package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.databinding.ItemCategoryProductBinding;
import com.delightbasket.grocery.databinding.ItemTextviewListweightpriceBinding;
import com.delightbasket.grocery.model.CategoryProduct;
import com.delightbasket.grocery.model.SearchCatProduct;
import com.delightbasket.grocery.retrofit.Const;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class AdapterWeightAllProduct extends RecyclerView.Adapter<AdapterWeightAllProduct.WeightViewHolder> {
    private Context context;
    private int i;
    private List<CategoryProduct.PriceUnit> priceUnit;
    private List<SearchCatProduct.PriceUnit> unit2;
    private com.delightbasket.grocery.databinding.ItemCategoryProductBinding catBinding;
    private BottomSheetDialog bottomSheetDialog;

    public AdapterWeightAllProduct(int i, List<CategoryProduct.PriceUnit> priceUnit, List<SearchCatProduct.PriceUnit> unit, ItemCategoryProductBinding catBinding, BottomSheetDialog bottomSheetDialog) {
        this.i = i;

        this.priceUnit = priceUnit;
        unit2 = unit;
        this.catBinding = catBinding;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_textview_listweightprice, parent, false);
        context = parent.getContext();
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        if(i == 1) {
            CategoryProduct.PriceUnit unit = priceUnit.get(position);

            if(unit.getUnit().equals(catBinding.tvProductweight.getText().toString())) {
                holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            } else {
                holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.color_black));
            }
            holder.binding.text1.setText(unit.getUnit());
            holder.itemView.setOnClickListener(view -> {
                catBinding.tvProductweight.setText(unit.getUnit());
                catBinding.tvProductprice.setText(unit.getPrice());
                holder.resetColor();
                holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));


                if(getCartdata(unit.getPriceUnitId()) == 0) {
                    catBinding.tvAdd.setVisibility(View.VISIBLE);
                    catBinding.lytPlusMinus.setVisibility(View.GONE);
                } else {
                    catBinding.tvAdd.setVisibility(View.GONE);
                    catBinding.lytPlusMinus.setVisibility(View.VISIBLE);
                    catBinding.tvQuantity.setText(String.valueOf(getCartdata(unit.getPriceUnitId())));
                }

                bottomSheetDialog.dismiss();

            });
        } else if(i == 2) {
            SearchCatProduct.PriceUnit datum = unit2.get(position);

            if(datum.getUnit().equals(catBinding.tvProductweight.getText().toString())) {
                holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            } else {
                holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.color_black));
            }
            holder.binding.text1.setText(datum.getUnit());
            holder.itemView.setOnClickListener(view -> {
                catBinding.tvProductweight.setText(datum.getUnit());
                catBinding.tvProductprice.setText(datum.getPrice());
                holder.resetColor();
                holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

                if(getCartdata(datum.getPriceUnitId()) == 0) {
                    catBinding.tvAdd.setVisibility(View.VISIBLE);
                    catBinding.lytPlusMinus.setVisibility(View.GONE);
                } else {
                    catBinding.tvAdd.setVisibility(View.GONE);
                    catBinding.lytPlusMinus.setVisibility(View.VISIBLE);
                    catBinding.tvQuantity.setText(String.valueOf(getCartdata(datum.getPriceUnitId())));
                }
                bottomSheetDialog.dismiss();
            });
        }

    }

    @Override
    public int getItemCount() {
        if(i == 1) {
            return priceUnit.size();
        } else if(i == 2) {
            return unit2.size();
        }
        return 0;
    }

    public class WeightViewHolder extends RecyclerView.ViewHolder {
        ItemTextviewListweightpriceBinding binding;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

        }

        public void resetColor() {
            binding.text1.setTextColor(ContextCompat.getColor(context, R.color.color_black));
        }
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
}
