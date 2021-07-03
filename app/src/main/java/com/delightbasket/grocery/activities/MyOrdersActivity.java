package com.delightbasket.grocery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.MyOrderAdapter;
import com.delightbasket.grocery.databinding.ActivityMyOrdersBinding;
import com.delightbasket.grocery.databinding.BottomSheetconfirmationBinding;
import com.delightbasket.grocery.fragments.OrderDetailsFragment;
import com.delightbasket.grocery.model.OrderRoot;
import com.delightbasket.grocery.model.RestResponse;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "myordersact";
    ActivityMyOrdersBinding binding;
    SessionManager sessionManager;
    RetrofitService service;
    private String token;
    private MyOrderAdapter myOrderAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private int start = 0;
    private boolean isLoding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_orders);
        sessionManager = new SessionManager(this);
        service = RetrofitBuilder.create(this);
        binding.shimmer.startShimmer();
        initView();
        binding.swipe.setOnRefreshListener(this);
        initListnear();

    }


    private void initListnear() {
        myOrderAdapter = new MyOrderAdapter();
        binding.rvOrders.setAdapter(myOrderAdapter);
        myOrderAdapter.setOnOrderClickListnear((order, clickCancel) -> {

            if(Boolean.TRUE.equals(clickCancel)) {
                openCancelSheet(order);
            } else {
                OrderDetailsFragment fragment = new OrderDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("orderId", order.getOrderId());
                bundle.putString("status", order.getStatus());
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame, fragment).commit();
            }
        });


        binding.rvOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!binding.rvOrders.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvOrders.getLayoutManager();
                    Log.d(TAG, "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d(TAG, "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d(TAG, "onScrollStateChanged:188 " + totalitem);
                    if(!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {
                        Log.d(TAG, "onScrollStateChanged: " + start);
                        start = start + Const.LIMIT;
                        binding.pd2.setVisibility(View.VISIBLE);
                        getData();
                    }
                }
            }
        });

    }

    private void getData() {
        isLoding = true;
        binding.lyt404.setVisibility(View.GONE);

        Call<OrderRoot> call = service.getMyOrders(Const.DEV_KEY, token, start, Const.LIMIT);
        call.enqueue(new Callback<OrderRoot>() {
            @Override
            public void onResponse(Call<OrderRoot> call, Response<OrderRoot> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                        List<OrderRoot.Datum> data = response.body().getData();
                        isLoding = false;
                        myOrderAdapter.addData(data);


                    } else if(start == 0 && response.body().getStatus() == 401) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }
                binding.pBar.setVisibility(View.GONE);
                binding.pd2.setVisibility(View.GONE);
                binding.shimmer.setVisibility(View.GONE);
                binding.swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<OrderRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void openCancelSheet(OrderRoot.Datum order1) {
        bottomSheetDialog = new BottomSheetDialog(this);
        BottomSheetconfirmationBinding sheetconfirmationBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheetconfirmation, null, false);
        bottomSheetDialog.setContentView(sheetconfirmationBinding.getRoot());
        sheetconfirmationBinding.tvtext.setText("Do you want cancel this order ?");
        bottomSheetDialog.show();
        sheetconfirmationBinding.tvCencel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        sheetconfirmationBinding.tvYes.setOnClickListener(v -> cancelOrder(order1));
    }

    private void cancelOrder(OrderRoot.Datum order1) {
        binding.pBar.setVisibility(View.VISIBLE);
        Call<RestResponse> call = service.cancelOrder(Const.DEV_KEY, token, order1.getOrderId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus() == 200) {
                        binding.pBar.setVisibility(View.VISIBLE);
                        start = 0;
                        initView();
                        initListnear();
                        Toast.makeText(MyOrdersActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    } else {
                        Toast.makeText(MyOrdersActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    binding.pBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });
    }

    private void initView() {


        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();

            binding.pBar.setVisibility(View.GONE);
            getData();

        }else{
            startActivity(new Intent(this, LoginActivity.class));

        }
    }


    public void onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onRefresh() {
        start = 0;
        initView();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}