package com.delightbasket.grocery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.AddressAdapter;
import com.delightbasket.grocery.databinding.ActivityDeliveryAddressBinding;
import com.delightbasket.grocery.model.Address;
import com.delightbasket.grocery.model.RestResponse;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryAddressActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ActivityDeliveryAddressBinding binding;
    AddressAdapter addressAdapter = new AddressAdapter();
    SessionManager sessionManager;
    private String token, user_id;
    private RetrofitService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_address);
        sessionManager = new SessionManager(this);
        token = sessionManager.getUser().getData().getToken();
        user_id = sessionManager.getUser().getData().getUserId();
        service = RetrofitBuilder.create(this);

        initListnear();
        getData();
        binding.swipe.setOnRefreshListener(this);
        Log.d("TAG", "onCreate: tk " + token);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        binding.lyt404.setVisibility(View.GONE);
        if(binding.swipe.isRefreshing()) {
            binding.pBar.setVisibility(View.GONE);
        } else {
            binding.shimmer.startShimmer();
        }
        Call<Address> call = service.getAllAddress(Const.DEV_KEY, token, user_id);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                Log.d("TAG", "onResponse: " + response.code());
                if(response.code() == 200) {
                    assert response.body() != null;
                    addressAdapter.addData(response.body().getData());
                    binding.rvAddress.setAdapter(addressAdapter);
                    binding.swipe.setRefreshing(false);
                    binding.pBar.setVisibility(View.GONE);
                    Log.d("TAG", "onResponse: " + response.body().getData().size());
                } else {
                    binding.lyt404.setVisibility(View.VISIBLE);
                }
                binding.swipe.setRefreshing(false);
                binding.pBar.setVisibility(View.GONE);
                binding.shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.toString());
            }
        });


    }

    private void initListnear() {
        binding.fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddDeliveryAddressActivity.class)));
        addressAdapter.setOnClickDeleveryAddress(datum -> {
            if(binding.swipe.isRefreshing()) {
                binding.pBar.setVisibility(View.GONE);
            } else {
                binding.pBar.setVisibility(View.VISIBLE);
            }
            binding.pBar.setVisibility(View.VISIBLE);
            Call<RestResponse> call = service.deleteDeliveryAddress(Const.DEV_KEY, token, user_id,datum.getDeliveryAddressId());
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    if(response.code() == 200) {
                        if(response.body().getStatus() == 200) {
                            Toast.makeText(DeliveryAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            getData();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
                }
            });
        });
    }


    @Override
    public void onRefresh() {
        getData();
        binding.lyt404.setVisibility(View.GONE);
    }

    public void onClickBack(View view) {
        super.onBackPressed();
    }
}