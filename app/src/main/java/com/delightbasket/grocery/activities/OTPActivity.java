package com.delightbasket.grocery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.configs.CommonFunctions;
import com.delightbasket.grocery.model.Otp;
import com.delightbasket.grocery.model.User;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {

    String TAG = "LoginActivity";
    SessionManager sessionManager;
    String userId = "";
    RetrofitService service;
    String smsToken = "";
    String mobileNumber = "";
    String OTP = "";
    boolean fromLogin = false;

    public  User userres;
    public  String usertoken;

    @BindView(R.id.txtLogin)
    AppCompatTextView txtLogin;
    @BindView(R.id.txtLoginContinue)
    AppCompatTextView txtLoginContinue;
    @BindView(R.id.txtPhone)
    AppCompatEditText txtPhone;
    @BindView(R.id.txtPassword)
    AppCompatEditText txtPassword;
    @BindView(R.id.btnSignin)
    AppCompatButton btnSignin;
    @BindView(R.id.cardSignin)
    CardView cardSignin;
    @BindView(R.id.txtNotAccount)
    TextView txtNotAccount;
    @BindView(R.id.txtSignin)
    TextView txtSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        ButterKnife.bind(this);


        service = RetrofitBuilder.create(this);

        sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userId = sessionManager.getUser().getData().getUserId();
        }

        Intent i = getIntent();
        Bundle b = i.getExtras();

        assert b != null;
        if (b.containsKey("otpToken")) {
            smsToken = b.getString("otpToken", "");
        } else {
            onBackPressed();
            Toast.makeText(this, "Something went wrong Please retry.. ", Toast.LENGTH_LONG).show();
        }

        if (b.containsKey("mobile")) {
            mobileNumber = b.getString("mobile", "");
        } else {
            onBackPressed();
            Toast.makeText(this, "Something went wrong Please retry.. ", Toast.LENGTH_LONG).show();
        }

        if (b.containsKey("fromlogin")) {
            fromLogin = b.getBoolean("fromlogin", false);
        }

    }

    @OnClick({R.id.btnSignin, R.id.txtSignin, R.id.txtLoginContinue})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignin:
                if (smsToken.equalsIgnoreCase("")) {
                    onBackPressed();
                    Toast.makeText(this, "Something went wrong Please retry.. ", Toast.LENGTH_LONG).show();
                }else if(Objects.requireNonNull(txtPhone.getText()).toString().equalsIgnoreCase("")){
                    Toast.makeText(OTPActivity.this, "Pleaese enter otp", Toast.LENGTH_LONG).show();
                } else {
                    checkOTP();
                }
                break;

            case R.id.txtLoginContinue:
                loginUser();
                break;
            case R.id.txtSignin:
                startActivity(new Intent(OTPActivity.this, SignUpActivity.class));
                break;
        }
    }


    private void loginUser() {
        Log.d(TAG, "registerUser: ");
        String notificationToken = sessionManager.getStringValue(Const.NOTIFICATION_TOKEN);
        Call<User> call = service.loginUser(
                Const.DEV_KEY,
                mobileNumber,
                "1", "1");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@Nullable Call<User> call, @Nullable Response<User> response) {
                if (response != null) {
                    Log.d(TAG, "Registration Success");
                    Log.d(TAG, response.toString());

                    if (response.body() != null) {
                        Log.d(TAG, String.valueOf(response.body().getData()));
                        if (response.body().getStatus() == 200) {
//                            sessionManager.saveUser(response.body());
                            smsToken = String.valueOf(response.body().getData().getSms().getDetails());
                            Log.d(TAG, response.body().getData().getSms().getDetails().toString());
                            service = RetrofitBuilder.create(OTPActivity.this);
//                            sessionManager.saveBooleanValue(Const.IS_LOGIN, true);
                            sessionManager = new SessionManager(OTPActivity.this);
                            if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                                userId = sessionManager.getUser().getData().getUserId();
                                Log.d(TAG, userId);
                            }
//                            startActivity(new Intent(OTPActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(OTPActivity.this, String.valueOf(response.body().getMessage()), Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }

            @Override
            public void onFailure(@Nullable Call<User> call, @Nullable Throwable t) {
                Log.d(TAG, TAG + t);
            }
        });
    }


    private void checkOTP() {
        try {
            if (CommonFunctions.checkConnection(this)){
                CommonFunctions.createProgressBar(this, "Checking OTP..");
                HashMap<String, String> requestBody = new HashMap<String, String>();
                requestBody.put("session_id", smsToken);
                requestBody.put("otp", String.valueOf(txtPhone.getText()));
                Call<Otp> call = service.checkOTP(
                        Const.DEV_KEY, requestBody);
                call.enqueue(new Callback<Otp>() {
                    @Override
                    public void onResponse(@Nullable Call<Otp> call, @Nullable Response<Otp> response) {
                        if (response != null) {
                            Log.d(TAG, "Registration Success");

                            Log.d(TAG, response.toString());

                            if (response.body() != null) {
                                Log.d(TAG, String.valueOf(response.body().getData()));
                                if (response.body().getSuccess_code() == 200) {
//                            sessionManager.saveStringValue(Const.USER_TOKEN, String.valueOf(response.body().getData().getToken()));
                                    if (response.body().getData().getStatus().equalsIgnoreCase("success")
                                    ) {


                                        sessionManager.saveBooleanValue(Const.IS_LOGIN, true);
                                        sessionManager.saveStringValue(Const.USER_TOKEN, LoginActivity.token);
                                        sessionManager.saveUser(LoginActivity.usersresponse);
                                        service = RetrofitBuilder.create(OTPActivity.this);

                                        sessionManager = new SessionManager(OTPActivity.this);

                                        CommonFunctions.destroyProgressBar();

                                        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                                            userId = sessionManager.getUser().getData().getUserId();
                                            Log.d(TAG, userId);
                                        }
//                                        startActivity(new Intent(OTPActivity.this, MainActivity.class));
                                        finish();
                                    }else{
                                        CommonFunctions.destroyProgressBar();
                                        Toast.makeText(OTPActivity.this, "Otp not sent Please resend..", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    CommonFunctions.destroyProgressBar();
                                    Toast.makeText(OTPActivity.this, "Please enter valid otp", Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    }

                    @Override
                    public void onFailure(@Nullable Call<Otp> call, @Nullable Throwable t) {
                        CommonFunctions.destroyProgressBar();
                        Log.d(TAG, TAG + t);
                        Toast.makeText(OTPActivity.this, "Please enter valid otp", Toast.LENGTH_LONG).show();
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromLogin){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else{
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        }
    }
}