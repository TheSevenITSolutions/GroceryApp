package com.delightbasket.grocery.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.model.RazorpayOrderIdResponse;
import com.delightbasket.grocery.model.RazorpaySuccessResponse;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWalletRazorpay extends AppCompatActivity implements PaymentResultListener {

    SessionManager sessionManager;
    private String token;
    private String orderId;
    private String userId;
    private String mobileNo;
    private String amount;
    private RetrofitService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);
        sessionManager = new SessionManager(this);
        service = RetrofitBuilder.create(this);
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            userId = sessionManager.getUser().getData().getUserId();
            mobileNo = sessionManager.getUser().getData().getMobileNo();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        String initAmount = (getIntent().getStringExtra("Amount"));
        amount = String.valueOf(Integer.parseInt(initAmount) * 100);
        Checkout.preload(getApplicationContext());
        createOrderOnRazorPay();
    }

    private void createOrderOnRazorPay() {
        Call<RazorpayOrderIdResponse> call = service.getRazorPayId(Const.DEV_KEY, token, amount);
        call.enqueue(new Callback<RazorpayOrderIdResponse>() {
            @Override
            public void onResponse(Call<RazorpayOrderIdResponse> call, Response<RazorpayOrderIdResponse> response) {
                if (response.code() == 200 && response.body().getStatus() == 200 && response.body().getData() != null) {
                    orderId = response.body().getData().getOrderId();
                    Log.e("test", orderId);
                    startPayment(orderId);
                }
            }

            @Override
            public void onFailure(Call<RazorpayOrderIdResponse> call, Throwable t) {
                Toast.makeText(AddWalletRazorpay.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void startPayment(String orderId) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Const.RAZORPAY_API_KEY_ID);
        checkout.setImage(R.drawable.logo_app_png);
        Activity activity = this;
        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", "Test");
            options.put("image", ContextCompat.getDrawable(AddWalletRazorpay.this, R.drawable.logo_app_png));
            options.put("order_id", orderId); //from response of step 3.
            options.put("theme.color", "#2ca7bb");
            options.put("currency", Const.CURRENCY_INR);
            options.put("amount", amount); //pass amount in currency subunits
            options.put("prefill.email", "ibrahim.malada1234@gmail.com");
            options.put("prefill.contact", mobileNo);
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        updateRazorPayPayment(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e("error", s);
        onBackPressed();
    }

    private void updateRazorPayPayment(String razorPayPaymentID) {
        Call<RazorpaySuccessResponse> call = service.addWallet(Const.DEV_KEY, token, userId, orderId, amount, razorPayPaymentID, "success");
        call.enqueue(new Callback<RazorpaySuccessResponse>() {
            @Override
            public void onResponse(Call<RazorpaySuccessResponse> call, Response<RazorpaySuccessResponse> response) {
                if (response.code() == 200 && response.body().getStatus() == 200) {
                    Toast.makeText(AddWalletRazorpay.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<RazorpaySuccessResponse> call, Throwable t) {
                Toast.makeText(AddWalletRazorpay.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }
}