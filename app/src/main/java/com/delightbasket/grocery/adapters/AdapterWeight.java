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
import com.delightbasket.grocery.databinding.ItemProductBinding;
import com.delightbasket.grocery.databinding.ItemTextviewListweightpriceBinding;
import com.delightbasket.grocery.model.Categories;
import com.delightbasket.grocery.retrofit.Const;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class AdapterWeight extends RecyclerView.Adapter<AdapterWeight.WeightViewHolder> {
    private List<Categories.PriceUnit> priceUnits;
    private com.delightbasket.grocery.databinding.ItemProductBinding productadapterbinding;
    private BottomSheetDialog bottomSheetDialog;
    private Context context;

    public AdapterWeight(List<Categories.PriceUnit> priceUnits, ItemProductBinding productadapterbinding, BottomSheetDialog bottomSheetDialog) {
        this.priceUnits = priceUnits;
        this.productadapterbinding = productadapterbinding;
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
        Categories.PriceUnit unit = priceUnits.get(position);

        String s = productadapterbinding.tvProductweight.getText().toString();
        if(unit.getUnit().equals(s)) {
            holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.color_black));
        }
        holder.binding.text1.setText(unit.getUnit());
        holder.itemView.setOnClickListener(view -> {
            productadapterbinding.tvProductweight.setText(unit.getUnit());
            productadapterbinding.tvProductprice.setText(unit.getPrice().concat(context.getString(R.string.currency) + " /" + unit.getUnit()));
            Long quantity = getCartdata(unit.getPriceUnitId());
            if(quantity == 0) {
                productadapterbinding.tvProductadd.setVisibility(View.VISIBLE);
                productadapterbinding.lytAddMore.setVisibility(View.GONE);
            } else {
                productadapterbinding.tvProductadd.setVisibility(View.GONE);
                productadapterbinding.lytAddMore.setVisibility(View.VISIBLE);
                productadapterbinding.tvQuantity.setText(String.valueOf(quantity));
            }
            holder.resetColor();
            holder.binding.text1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            bottomSheetDialog.dismiss();

        });

    }


    @Override
    public int getItemCount() {
        return priceUnits.size();
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
