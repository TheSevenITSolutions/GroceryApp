package com.delightbasket.grocery.adapters;

import android.content.Context;
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
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.databinding.BottomsheetProductweightBinding;
import com.delightbasket.grocery.databinding.ItemSearchProductBinding;
import com.delightbasket.grocery.model.Search;
import com.delightbasket.grocery.retrofit.Const;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private List<Search.Datum> mList = new ArrayList<>();
    OnSearchItemClick onSearchItemClick;
    private Context context;
    private Search.PriceUnit defultPriceUnit;


    public OnSearchItemClick getOnSearchItemClick() {
        return onSearchItemClick;
    }

    public void setOnSearchItemClick(OnSearchItemClick onSearchItemClick) {
        this.onSearchItemClick = onSearchItemClick;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_product, parent, false);

        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        defultPriceUnit = mList.get(position).getPriceUnit().get(0);

        holder.binding.tvProductprice.setText(defultPriceUnit.getPrice().concat(context.getString(R.string.currency) + " /").concat(defultPriceUnit.getUnit()));
        holder.setModel(mList.get(position));

    }


    public void addData(List<Search.Datum> mList) {

        for(int i = 0; i < mList.size(); i++) {
            this.mList.add(mList.get(i));
            notifyItemInserted(this.mList.size());
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnSearchItemClick {
        void onSearchClick(Search.Datum datum, ItemSearchProductBinding binding, String work, Search.PriceUnit defultPriceUnit);
    }


    public class SearchViewHolder extends RecyclerView.ViewHolder {
        ItemSearchProductBinding binding;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSearchProductBinding.bind(itemView);
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

        public void setModel(Search.Datum datum) {
            Glide.with(binding.getRoot().getContext())
                    .load(Const.BASE_IMG_URL + datum.getProductImage().get(0))
                    .placeholder(R.drawable.app_placeholder)
                    .into(binding.imgProduct);

            binding.tvProductName.setText(datum.getProductName());
            binding.tvQuantity.setText(String.valueOf(getCartdata(datum.getPriceUnit().get(0).getPriceUnitId())));
            binding.tvProductWeight.setText(datum.getPriceUnit().get(0).getUnit());
            binding.tvProductWeight.setOnClickListener(v -> openBottomSheet(datum, binding));
            binding.tvAdd.setOnClickListener(v -> onSearchItemClick.onSearchClick(datum, binding, "add", defultPriceUnit));
            binding.imgPlus.setOnClickListener(v -> onSearchItemClick.onSearchClick(datum, binding, "add", defultPriceUnit));
            binding.imgMinus.setOnClickListener(v -> onSearchItemClick.onSearchClick(datum, binding, "less", defultPriceUnit));

            if(getCartdata(datum.getPriceUnit().get(0).getPriceUnitId()) == 0) {
                binding.tvAdd.setVisibility(View.VISIBLE);
                binding.lytPlusMinus.setVisibility(View.GONE);
            } else {
                binding.tvAdd.setVisibility(View.GONE);
                binding.lytPlusMinus.setVisibility(View.VISIBLE);
            }

            if(datum.getStockQuantity().equals("Out of Stock")) {
                binding.tvProductoutofstock.setVisibility(View.VISIBLE);
                binding.tvAdd.setVisibility(View.GONE);
                binding.lytPlusMinus.setVisibility(View.GONE);
            } else {
                binding.tvProductoutofstock.setVisibility(View.GONE);
            }
        }

        private void openBottomSheet(Search.Datum datum, ItemSearchProductBinding binding) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            BottomsheetProductweightBinding productweightBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_productweight, null, false);
            bottomSheetDialog.setContentView(productweightBinding.getRoot());

            AdapterWeightSearch adapterWeight = new AdapterWeightSearch(datum.getPriceUnit(), binding, priceUnit -> {
                defultPriceUnit = priceUnit;
                binding.tvQuantity.setText(String.valueOf(getCartdata(priceUnit.getPriceUnitId())));
                binding.tvProductprice.setText(priceUnit.getPrice().concat(context.getString(R.string.currency) + " /").concat(priceUnit.getUnit()));
                if(getCartdata(datum.getPriceUnit().get(0).getPriceUnitId()) == 0) {
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
