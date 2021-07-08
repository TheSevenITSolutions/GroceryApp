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
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.activities.ProductDetailActivity;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.databinding.BottomsheetProductweightBinding;
import com.delightbasket.grocery.databinding.ItemProductBinding;
import com.delightbasket.grocery.databinding.ItemSubsBinding;
import com.delightbasket.grocery.model.Categories;
import com.delightbasket.grocery.model.schedulelistmodel.DataItem;
import com.delightbasket.grocery.model.schedulelistmodel.ScheduleListResponse;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    ItemProductBinding binding;
    List<DataItem> list = new ArrayList<>();
    ItemSubsBinding bindingsubs;
    OnProductClickListener onProductClickListener;
    Context context;
    private List<Categories.Product> mList = new ArrayList<>();
    String tag = "rrrrrr";
    SessionManager sessionManager;
    Boolean subs;
    public ProductAdapter(Boolean subs) {
        this.subs = subs;

    }

    public OnProductClickListener getOnProductClickListener() {
        return onProductClickListener;
    }

    public void setOnProductClickListener(OnProductClickListener onProductClickListener) {
        this.onProductClickListener = onProductClickListener;
    }

    public void updateData(List<Categories.Product> mList) {
        for (int i = 0; i < mList.size(); i++) {
            this.mList.add(mList.get(i));
            notifyItemInserted(this.mList.size() - 1);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {


        long cartquantity = getCartdata(mList.get(position).getPriceUnit().get(0).getPriceUnitId());
        Log.d("qqqq", "onBindViewHolder: " + mList.get(position).getPriceUnit().get(0).getPrice());
        holder.setModel(mList.get(position), cartquantity);


    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager(context);
        View view;
        if (subs) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subs, parent, false);
        }

        return new ProductViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public interface OnProductClickListener {
        void onProductClick(Categories.Product product, ItemProductBinding binding, String work, Categories.PriceUnit priceUnit, long quantity);
    }

    private long getCartdata(String id) {
        AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();
        Log.d("qqqq1", "getCartdata: " + id);
        if (!db.cartDao().getCartProduct(id).isEmpty()) {
            return db.cartDao().getCartProduct(id).get(0).getQuantity();
        } else {
            return 0;
        }


    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            if (subs) {
                binding = DataBindingUtil.bind(itemView);
            } else {
                bindingsubs = DataBindingUtil.bind(itemView);
            }


        }

        public void setModel(Categories.Product model, long cartObj) {
            if (subs) {
                binding.tvProductname.setText(model.getProductName());
                binding.tvProductprice.setText(model.getPriceUnit().get(0).getPrice().concat(context.getString(R.string.currency) + " /" + model.getPriceUnit().get(0).getUnit()));
                binding.tvProductweight.setText(model.getPriceUnit().get(0).getUnit());
                Log.d("qqqq", "setModel: q" + cartObj);
                if (cartObj == 0) {
                    binding.tvProductadd.setVisibility(View.VISIBLE);
                    binding.lytAddMore.setVisibility(View.GONE);
                } else {
                    Log.d("qqqqe", "setModel: qua " + cartObj);
                    binding.tvProductadd.setVisibility(View.GONE);
                    binding.lytAddMore.setVisibility(View.VISIBLE);
                    binding.tvQuantity.setText(String.valueOf(cartObj));
                }


                if (model.getStockQuantity().equals("Out of Stock")) {
                    binding.tvProductoutofstock.setVisibility(View.VISIBLE);
                    binding.tvProductadd.setVisibility(View.GONE);
                    binding.lytAddMore.setVisibility(View.GONE);
                } else {
                    binding.tvProductoutofstock.setVisibility(View.GONE);
                }
                Glide.with(binding.getRoot().getContext())
                        .load(model.getProductImage().get(0))
//                    .load(Const.BASE_IMG_URL + Const.Product + "/" + model.getProductImage().get(0))
                        .placeholder(R.drawable.app_placeholder)
                        .into(binding.imgProduct);

                initListener(binding, model, cartObj);
            }

            if (!subs) {
                bindingsubs.btnsubscribe.setOnClickListener(itemView -> {

                    getSpinnerList(model.getProductId(), model.getPriceUnit().get(0).getPriceUnitId());
                });
                bindingsubs.tvProductname.setText(model.getProductName());
                bindingsubs.tvProductprice.setText(model.getPriceUnit().get(0).getPrice().concat(context.getString(R.string.currency) + " /" + model.getPriceUnit().get(0).getUnit()));
                bindingsubs.tvProductweight.setText(model.getPriceUnit().get(0).getUnit());
                Glide.with(bindingsubs.getRoot().getContext())
                        .load(model.getProductImage().get(0))
//                    .load(Const.BASE_IMG_URL + Const.Product + "/" + model.getProductImage().get(0))
                        .placeholder(R.drawable.app_placeholder)
                        .into(bindingsubs.imgProduct);
            }



        }

        private void getSpinnerList(String productId, String priceUnitId) {
            list.clear();
            RetrofitService service;
            service = RetrofitBuilder.create(context);
            String token = sessionManager.getUser().getData().getToken();
            Call<ScheduleListResponse> call = service.getScheduleList(token, Const.DEV_KEY);
            call.enqueue(new Callback<ScheduleListResponse>() {
                @Override
                public void onResponse(Call<ScheduleListResponse> call, Response<ScheduleListResponse> response) {
                    list.addAll(response.body().getData());
                    openBottomSheetWeight(list, bindingsubs, productId, priceUnitId);
                }

                @Override
                public void onFailure(Call<ScheduleListResponse> call, Throwable t) {

                }
            });

        }
        private void openBottomSheetWeight(List<DataItem> product, ItemSubsBinding binding, String productId, String priceUnitId) {
            if (context != null) {
                BottomSheetDialog bottomSheetDialog;
                bottomSheetDialog = new BottomSheetDialog(context);
                BottomsheetProductweightBinding productweightBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_productweight, null, false);
                bottomSheetDialog.setContentView(productweightBinding.getRoot());

                SpinnerAdapter adapterWeight = new SpinnerAdapter(product, bindingsubs, bottomSheetDialog, productId, priceUnitId);
                productweightBinding.listProductWeight.setAdapter(adapterWeight);
                bottomSheetDialog.show();
                productweightBinding.tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());


            }
        }
        private void initListener(ItemProductBinding binding, Categories.Product model, long quantity) {
            binding.tvProductweight.setOnClickListener(view -> onProductClickListener.onProductClick(model, binding, "WEIGHT", null, quantity));
            binding.imgProduct.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra(Const.ProductID, model.getProductId());
                context.startActivity(intent);
            });
            binding.tvProductadd.setOnClickListener(v -> {

                Categories.PriceUnit priceUnit = getPriceUnit(binding.tvProductweight.getText().toString(), model);
                long currentQuantity = getCartdata(priceUnit.getPriceUnitId());

                onProductClickListener.onProductClick(model, binding, "addMore", priceUnit, currentQuantity);
            });
        }


        private Categories.PriceUnit getPriceUnit(String toString, Categories.Product model) {
            for (int i = 0; i <= model.getPriceUnit().size() - 1; i++) {
                Log.d(tag, "getPriceUnit: " + model.getPriceUnit().get(i).getUnit());
                Log.d(tag, "getPriceUnit: " + model.getPriceUnit().get(i).getPriceUnitId());
                if (model.getPriceUnit().get(i).getUnit().equals(toString)) {

                    Log.d(tag, "getPriceUnit: trueee");

                    return model.getPriceUnit().get(i);
                }
            }
            return null;
        }

    }

}
