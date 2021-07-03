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
import com.delightbasket.grocery.databinding.ItemCategoryProductBinding;
import com.delightbasket.grocery.model.CategoryProduct;
import com.delightbasket.grocery.model.SearchCatProduct;
import com.delightbasket.grocery.retrofit.Const;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductAdapter extends RecyclerView.Adapter<CategoryProductAdapter.CategoryProductViewHolder> {


    private List<CategoryProduct.Product> data = new ArrayList<>();
    OnCatAllProductClickListnear onCatAllProductClickListnear;
    private Context context;
    private int i;
    private List<SearchCatProduct.Datum> data2 = new ArrayList<>();

    public CategoryProductAdapter(OnCatAllProductClickListnear onCatAllProductClickListnear) {
        this.onCatAllProductClickListnear = onCatAllProductClickListnear;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryProductViewHolder holder, int position) {
        if(i == 1) {
            holder.binding.setProduct(data.get(position));
            holder.setModel(data.get(position));
            holder.initListnear(data.get(position), null);
            holder.binding.tvProductweight.setOnClickListener(v -> holder.openBottomSheet(data.get(position), null));
        } else if(i == 2) {
            holder.setModel2(data2.get(position));
            holder.binding.tvProductweight.setOnClickListener(v -> holder.openBottomSheet(null, data2.get(position)));
            holder.initListnear(null, data2.get(position));
        }

    }

    @NonNull
    @Override
    public CategoryProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_product, parent, false);
        context = parent.getContext();
        return new CategoryProductViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if(i == 1) {
            return data.size();
        } else if(i == 2) {
            return data2.size();
        }
        return 0;

    }

    public void addData(List<CategoryProduct.Product> data, int i) {
        for(int j = 0; j < data.size(); j++) {
            this.data.add(data.get(j));
            notifyItemInserted(this.data.size());
        }

        this.i = i;
    }

    public void addData2(List<SearchCatProduct.Datum> data2, int i) {
        for(int j = 0; j < data2.size(); j++) {
            this.data2.add(data2.get(j));
            notifyItemInserted(this.data2.size());
        }

        this.i = i;
    }

    public interface OnCatAllProductClickListnear {
        void onCatProductClick(CategoryProduct.Product product1, SearchCatProduct.Datum product2, int i, String work, ItemCategoryProductBinding binding);
    }


    public class CategoryProductViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryProductBinding binding;

        public CategoryProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void setModel(CategoryProduct.Product product) {
            binding.tvProductName.setText(product.getProductName());
            binding.tvProductprice.setText(product.getPriceUnit().get(0).getPrice());
            binding.tvProductweight.setText(product.getPriceUnit().get(0).getUnit());
            binding.tvQuantity.setText(String.valueOf(getCartdata(product.getPriceUnit().get(0).getPriceUnitId())));
            Glide.with(binding.getRoot().getContext())
                    .load(Const.BASE_IMG_URL + product.getProductImage().get(0))
                    .placeholder(R.drawable.app_placeholder)
                    .into(binding.imgProduct);
            if(getCartdata(product.getPriceUnit().get(0).getPriceUnitId()) == 0) {
                binding.tvAdd.setVisibility(View.VISIBLE);
                binding.lytPlusMinus.setVisibility(View.GONE);
            } else {
                binding.tvAdd.setVisibility(View.GONE);
                binding.lytPlusMinus.setVisibility(View.VISIBLE);
            }
            if(product.getStockQuantity().equals("Out of Stock")) {
                binding.tvProductoutofstock.setVisibility(View.VISIBLE);
                binding.tvAdd.setVisibility(View.GONE);
                binding.lytPlusMinus.setVisibility(View.GONE);
            } else {
                binding.tvProductoutofstock.setVisibility(View.GONE);
            }

        }

        private void openBottomSheet(CategoryProduct.Product product, SearchCatProduct.Datum datum) {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            BottomsheetProductweightBinding productweightBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_productweight, null, false);
            bottomSheetDialog.setContentView(productweightBinding.getRoot());
            if(i == 1) {
                AdapterWeightAllProduct adapterWeight = new AdapterWeightAllProduct(1, product.getPriceUnit(), null, binding, bottomSheetDialog);
                productweightBinding.listProductWeight.setAdapter(adapterWeight);
                bottomSheetDialog.show();
            } else if(i == 2) {
                AdapterWeightAllProduct adapterWeight = new AdapterWeightAllProduct(2, null, datum.getPriceUnit(), binding, bottomSheetDialog);
                productweightBinding.listProductWeight.setAdapter(adapterWeight);
                bottomSheetDialog.show();
            }

            productweightBinding.tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());


        }

        public void setModel2(SearchCatProduct.Datum datum) {
            binding.tvProductName.setText(datum.getProductName());
            binding.tvProductprice.setText(datum.getPriceUnit().get(0).getPrice());
            binding.tvProductweight.setText(datum.getPriceUnit().get(0).getUnit());
            binding.tvQuantity.setText(String.valueOf(getCartdata(datum.getPriceUnit().get(0).getPriceUnitId())));
            Glide.with(binding.getRoot().getContext())
                    .load(Const.BASE_IMG_URL + datum.getProductImage().get(0))
                    .placeholder(R.drawable.app_placeholder)
                    .into(binding.imgProduct);

            if(getCartdata(datum.getPriceUnit().get(0).getPriceUnitId()) == 0) {
                binding.tvAdd.setVisibility(View.VISIBLE);
                binding.lytPlusMinus.setVisibility(View.GONE);
            } else {
                binding.tvAdd.setVisibility(View.GONE);
                binding.lytPlusMinus.setVisibility(View.VISIBLE);
            }
        }

        public void initListnear(CategoryProduct.Product product, SearchCatProduct.Datum datum) {
            if(i == 1) {
                binding.tvAdd.setOnClickListener(v -> onCatAllProductClickListnear.onCatProductClick(product, null, 1, "Add", binding));
                binding.imgPlus.setOnClickListener(v -> onCatAllProductClickListnear.onCatProductClick(product, null, 1, "Add", binding));
                binding.imgMinus.setOnClickListener(v -> onCatAllProductClickListnear.onCatProductClick(product, null, 1, "Less", binding));
                binding.imgProduct.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra(Const.ProductID, product.getProductId());
                    context.startActivity(intent);
                });
            } else if(i == 2) {
                binding.tvAdd.setOnClickListener(v -> onCatAllProductClickListnear.onCatProductClick(null, datum, 2, "Add", binding));
                binding.imgPlus.setOnClickListener(v -> onCatAllProductClickListnear.onCatProductClick(null, datum, 2, "Add", binding));
                binding.imgMinus.setOnClickListener(v -> onCatAllProductClickListnear.onCatProductClick(null, datum, 2, "Less", binding));
                binding.imgProduct.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra(Const.ProductID, datum.getProductId());
                    context.startActivity(intent);
                });
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


}
