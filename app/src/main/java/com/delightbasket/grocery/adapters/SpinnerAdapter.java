package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.databinding.ItemSubsBinding;
import com.delightbasket.grocery.databinding.ItemTextviewListweightpriceBinding;
import com.delightbasket.grocery.model.postsubscription.PostSubscriptionResponse;
import com.delightbasket.grocery.model.schedulelistmodel.DataItem;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.WeightViewHolder> {
    private List<DataItem> priceUnits;
    private ItemSubsBinding productadapterbinding;
    private BottomSheetDialog bottomSheetDialog;
    private Context context;
    SessionManager sessionManager;
    String productId;
    String productUnitId;

    public SpinnerAdapter(List<DataItem> priceUnits, ItemSubsBinding productadapterbinding, BottomSheetDialog bottomSheetDialog, String productId, String priceUnitId) {
        this.priceUnits = priceUnits;
        this.productId = productId;
        this.productUnitId = priceUnitId;
        this.productadapterbinding = productadapterbinding;
        this.bottomSheetDialog = bottomSheetDialog;
    }


    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_textview_listweightprice, parent, false);
        context = parent.getContext();
        sessionManager = new SessionManager(context);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        DataItem unit = priceUnits.get(position);

        holder.binding.text1.setText(unit.getScheduleName());
        holder.itemView.setOnClickListener(view -> {
            callSubscribeApi(unit.getId());
            bottomSheetDialog.dismiss();

        });

    }

    private void callSubscribeApi(int id) {
        RetrofitService service;
        service = RetrofitBuilder.create(context);
        String token = sessionManager.getUser().getData().getToken();
        Call<PostSubscriptionResponse> call = service.getPostSubscription(token,
                Const.DEV_KEY, "", productId, String.valueOf(id), "1",
                productUnitId, "00E604F8", "123456");
        call.enqueue(new Callback<PostSubscriptionResponse>() {
            @Override
            public void onResponse(Call<PostSubscriptionResponse> call, Response<PostSubscriptionResponse> response) {
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<PostSubscriptionResponse> call, Throwable t) {

            }
        });


    }


    @Override
    public int getItemCount() {
        return priceUnits.size();
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

}
