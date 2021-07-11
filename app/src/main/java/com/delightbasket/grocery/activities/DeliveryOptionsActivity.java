package com.delightbasket.grocery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.DeliveryAddressOptionsAdapter;
import com.delightbasket.grocery.configs.CommonFunctions;
import com.delightbasket.grocery.databinding.ActivityDeliveryOptionsBinding;
import com.delightbasket.grocery.model.Address;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryOptionsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ActivityDeliveryOptionsBinding binding;
    SessionManager sessionManager;
    private String token;
    private String user_id = "";
    private DeliveryAddressOptionsAdapter deliveryAddressOptionsAdapter;
    private String addressId;
    private boolean isEmpty = false;
    private String lat = "";
    private String lang = "";
    private Address.Datum addressObj;
    String typeFast = "2";
    String TAG = "DeliveryOptionsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_options);
        sessionManager = new SessionManager(this);


        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            user_id = sessionManager.getUser().getData().getUserId();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        initView();
        getData();
        initListnear();
        binding.swipe.setOnRefreshListener(this);
    }


    private void getData() {
        try{
            if (CommonFunctions.checkConnection(this)){

                token = sessionManager.getUser().getData().getToken();
                user_id = sessionManager.getUser().getData().getUserId();

                if(!binding.swipe.isRefreshing()) {
                    binding.pd.setVisibility(View.VISIBLE);
                }
                binding.lyt404.setVisibility(View.GONE);

                RetrofitService service = RetrofitBuilder.create(this);
                Call<Address> call = service.getAllAddress(Const.DEV_KEY, token, user_id);
                call.enqueue(new Callback<Address>() {
                    @Override
                    public void onResponse(Call<Address> call, Response<Address> response) {
                        if(response.code() == 200) {
                            if(response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                                deliveryAddressOptionsAdapter.addData(response.body().getData());
                                Log.d(TAG, response.body().getData().toString());
                                binding.rvAddress.setAdapter(deliveryAddressOptionsAdapter);
                                binding.pd.setVisibility(View.GONE);
                                addressId = Objects.requireNonNull(response.body()).getData().get(0).getDeliveryAddressId();
                                addressObj = Objects.requireNonNull(response.body()).getData().get(0);
                                lat = Objects.requireNonNull(response.body()).getData().get(0).getLatitude();
                                lang = Objects.requireNonNull(response.body()).getData().get(0).getLongitude();
                                isEmpty = false;
                                binding.tvPay.setText("Proceed To Pay");
                            } else if (response.body().getData().isEmpty()) {
                                isEmpty = true;
                                binding.tvPay.setText("Add New Address");
                                Toast.makeText(DeliveryOptionsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                binding.lyt404.setVisibility(View.VISIBLE);
                            }
                            binding.swipe.setRefreshing(false);
                            binding.pd.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Address> call, Throwable t) {
//ll
                        Toast.makeText(DeliveryOptionsActivity.this, t.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(DeliveryOptionsActivity.this, e.toString(), Toast.LENGTH_LONG).show();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void initListnear() {
        binding.tvPay.setOnClickListener(v -> {
            if(isEmpty) {
                startActivity(new Intent(this, AddDeliveryAddressActivity.class));
            } else {
                Intent intent = new Intent(this, PaymentSummaryActivity.class);
                intent.putExtra("addressId", addressId);
                intent.putExtra("address", new Gson().toJson(addressObj));
                intent.putExtra("lat", lat);
                intent.putExtra("lang", lang);
                intent.putExtra("type", typeFast);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        CheckBox radioBtn = findViewById(R.id.radioBtn);

        deliveryAddressOptionsAdapter = new DeliveryAddressOptionsAdapter();
        deliveryAddressOptionsAdapter.setOnAddressSelectListnear(address -> {
            addressId = address.getDeliveryAddressId();
            lat = address.getLatitude();
            lang = address.getLongitude();
            addressObj = address;
        });

        radioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    typeFast = "1";
                } else {
                    typeFast = "2";
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        getData();

        binding.lyt404.setVisibility(View.GONE);
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickadd(View view) {
        startActivity(new Intent(this, AddDeliveryAddressActivity.class));

    }
}