package com.delightbasket.grocery.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.databinding.BottomsheetProductweightBinding;
import com.delightbasket.grocery.databinding.ItemSubsBinding;
import com.delightbasket.grocery.databinding.ItemSubscategoryBinding;
import com.delightbasket.grocery.model.SubscriptionListByCategoryIdResponse.subs;
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

public class SubsCategoryAdapter extends RecyclerView.Adapter<SubsCategoryAdapter.SubsViewHolder> {
    private List<subs> userId = new ArrayList<>();
    List<DataItem> list = new ArrayList<>();
    SessionManager sessionManager;
    ItemSubsBinding bindingsubs;
    private Context context;

    public SubsCategoryAdapter() {


    }


    public void updateData(List<subs> mList) {

        for (int i = 0; i < mList.size(); i++) {
            this.userId.add(mList.get(i));
            notifyItemInserted(this.userId.size() - 1);
        }

    }

    @NonNull
    @Override
    public SubsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscategory, parent, false);
        return new SubsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubsViewHolder holder, int position) {
        Glide.with(holder.binding.getRoot().getContext())
                .load(userId.get(position).getProductImage().get(0))
//                .load(Const.BASE_IMG_URL + Const.Product + "/" + userId.get(position).getProductImage())
                .placeholder(R.drawable.app_placeholder)
                .into(holder.binding.userproductImage);

        holder.binding.userproductname.setText(userId.get(position).getProductName());
        holder.binding.tvSubscribe.setOnClickListener(view -> {
            getSpinnerList(userId.get(position).getProductId(), userId.get(position).getPriceUnit().get(position).getPriceUnitId());
        });

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

    @Override
    public int getItemCount() {
        return userId.size();
    }

    public class SubsViewHolder extends RecyclerView.ViewHolder {
        ItemSubscategoryBinding binding;

        public SubsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSubscategoryBinding.bind(itemView);
        }
    }
}

