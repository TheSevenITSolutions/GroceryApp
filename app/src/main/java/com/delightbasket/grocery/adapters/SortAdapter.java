package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.activities.ProductDetailActivity;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.databinding.BottomsheetProductweightBinding;
import com.delightbasket.grocery.databinding.ItemSearchProductBinding;
import com.delightbasket.grocery.model.SortRoot;
import com.delightbasket.grocery.retrofit.Const;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.SortViewHolder> {
    OnSortItemClick onSortItemClick;
    private Context context;
    private List<SortRoot.DataItem> mList = new ArrayList<>();
    AppDatabase db;


    public OnSortItemClick getOnSortItemClick() {
        return onSortItemClick;
    }

    public void setOnSortItemClick(OnSortItemClick onSearchItemClick) {
        this.onSortItemClick = onSearchItemClick;
    }

    @NonNull
    @Override
    public SortViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        db = Room.databaseBuilder(context,
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_product, parent, false);

        return new SortViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SortViewHolder holder, int position) {


        holder.setModel(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addData(List<SortRoot.DataItem> mList) {

        for(int i = 0; i < mList.size(); i++) {
            this.mList.add(mList.get(i));
            notifyItemInserted(this.mList.size() - 1);
        }

    }


    public interface OnSortItemClick {
        void onSearchClick(SortRoot.DataItem datum, ItemSearchProductBinding binding, String work, SortRoot.PriceUnitItem defultPriceUnit);
    }


    public class SortViewHolder extends RecyclerView.ViewHolder {
        ItemSearchProductBinding binding;
        private SortRoot.PriceUnitItem mpriceUnit;

        public SortViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSearchProductBinding.bind(itemView);
        }

        private long getCartdata(String id) {
            Log.d("qqqq1", "getCartdata: " + id);
            if(!db.cartDao().getCartProduct(id).isEmpty()) {
                return db.cartDao().getCartProduct(id).get(0).getQuantity();
            } else {
                return 0;
            }


        }

        public void setModel(SortRoot.DataItem datum) {
            Glide.with(binding.getRoot().getContext())
                    .load(datum.getProductImage().get(0))
                    .placeholder(R.drawable.app_placeholder)
                    .into(binding.imgProduct);

            mpriceUnit = datum.getPriceUnit().get(0);
            Log.d("TAG", "onBindViewHolder: " + mpriceUnit.getPriceUnitId());
            binding.tvProductprice.setText(mpriceUnit.getPrice().concat(context.getString(R.string.currency) + " /").concat(mpriceUnit.getUnit()));
            binding.tvProductWeight.setText(mpriceUnit.getUnit());

            binding.tvProductName.setText(datum.getProductName());
            binding.tvQuantity.setText(String.valueOf(getCartdata(datum.getPriceUnit().get(0).getPriceUnitId())));
            binding.tvProductWeight.setOnClickListener(v -> openBottomSheet(datum, binding));
            binding.tvAdd.setOnClickListener(v -> onSortItemClick.onSearchClick(datum, binding, "add", mpriceUnit));
            binding.imgPlus.setOnClickListener(v -> onSortItemClick.onSearchClick(datum, binding, "add", mpriceUnit));
            binding.imgMinus.setOnClickListener(v -> onSortItemClick.onSearchClick(datum, binding, "less", mpriceUnit));

            if(getCartdata(datum.getPriceUnit().get(0).getPriceUnitId()) == 0) {
                binding.tvAdd.setVisibility(View.VISIBLE);
                binding.lytPlusMinus.setVisibility(View.GONE);
            } else {
                binding.tvAdd.setVisibility(View.GONE);
                binding.lytPlusMinus.setVisibility(View.VISIBLE);
            }
            binding.imgProduct.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra(Const.ProductID, datum.getProductId());
                context.startActivity(intent);

            });
        }

        private void openBottomSheet(SortRoot.DataItem datum, ItemSearchProductBinding binding) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            BottomsheetProductweightBinding productweightBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_productweight, null, false);
            bottomSheetDialog.setContentView(productweightBinding.getRoot());

            AdapterWeightSort adapterWeight = new AdapterWeightSort(datum.getPriceUnit(), binding, priceUnit -> {
                mpriceUnit = priceUnit;
                binding.tvQuantity.setText(String.valueOf(getCartdata(priceUnit.getPriceUnitId())));
                binding.tvProductprice.setText(priceUnit.getPrice().concat(context.getString(R.string.currency) + " /").concat(priceUnit.getUnit()));
                binding.tvProductWeight.setText(priceUnit.getUnit());
                if(getCartdata(priceUnit.getPriceUnitId()) == 0) {
                    binding.tvAdd.setVisibility(View.VISIBLE);
                    binding.lytPlusMinus.setVisibility(View.GONE);
                } else {
                    binding.tvAdd.setVisibility(View.GONE);
                    binding.lytPlusMinus.setVisibility(View.VISIBLE);
                }
                bottomSheetDialog.dismiss();
            });
            productweightBinding.listProductWeight.setAdapter(adapterWeight);
            bottomSheetDialog.show();
            productweightBinding.tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());


        }


    }

}
