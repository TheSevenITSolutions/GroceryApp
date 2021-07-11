package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.databinding.ItemSubsuseridBinding;
import com.delightbasket.grocery.model.DeleteSubsResponse;
import com.delightbasket.grocery.model.ProductsItem;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubsUserAdapter extends RecyclerView.Adapter<SubsUserAdapter.SubsViewHolder> {
    private List<ProductsItem> userId = new ArrayList<>();
    SessionManager sessionManager;
    private Context context;

    public SubsUserAdapter() {
    }


    public void updateData(List<ProductsItem> mList) {

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subsuserid, parent, false);
        return new SubsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubsViewHolder holder, int position) {
        Glide.with(holder.binding.getRoot().getContext())
//                .load(userId.get(position).getProductImage())
                .load(Const.BASE_IMG_URL + Const.Product + "/" + userId.get(position).getProductImage())
                .placeholder(R.drawable.app_placeholder)
                .into(holder.binding.userproductImage);

        holder.binding.userproductname.setText(userId.get(position).getProductName());
        holder.binding.ivdeletesubs.setOnClickListener(view -> {
            deleteSubs(position);
        });
    }

    private void deleteSubs(int position) {

        RetrofitService service;
        service = RetrofitBuilder.create(context);
        String token = sessionManager.getUser().getData().getToken();
        Call<DeleteSubsResponse> delete = service.getDeleteSubs(token, Const.DEV_KEY, userId.get(position).getSubscription_id());
        delete.enqueue(new Callback<DeleteSubsResponse>() {
            @Override
            public void onResponse(Call<DeleteSubsResponse> call, Response<DeleteSubsResponse> response) {
                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                userId.remove(position);
                notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<DeleteSubsResponse> call, Throwable t) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return userId.size();
    }

    public class SubsViewHolder extends RecyclerView.ViewHolder {
        ItemSubsuseridBinding binding;

        public SubsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSubsuseridBinding.bind(itemView);
        }
    }
}
