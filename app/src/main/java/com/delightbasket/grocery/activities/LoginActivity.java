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
import com.delightbasket.grocery.model.User;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.gms.common.internal.service.Common;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivity";
    SessionManager sessionManager;
    String userId = "";
    RetrofitService service;
    String smsToken = "";

    public static User usersresponse;
    public static String token;

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
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        service = RetrofitBuilder.create(this);

        sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userId = sessionManager.getUser().getData().getUserId();
        }

    }

    @OnClick({R.id.btnSignin, R.id.txtSignin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignin:
                if (Objects.requireNonNull(txtPhone.getText()).toString().equalsIgnoreCase("")){
                    Toast.makeText(LoginActivity.this, "Pleaese enter mobile number", Toast.LENGTH_LONG).show();

                }else{
                    loginUser();
                }

                break;
            case R.id.txtSignin:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
                break;
        }
    }


    private void loginUser() {
        try{
            if (CommonFunctions.checkConnection(this)) {
                CommonFunctions.createProgressBar(this, "Signing in..");
                Log.d(TAG, "registerUser: ");
                String notificationToken = sessionManager.getStringValue(Const.NOTIFICATION_TOKEN);
                Call<User> call = service.loginUser(
                        Const.DEV_KEY,
                        Objects.requireNonNull(txtPhone.getText()).toString(),
                        "1", "1");
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@Nullable Call<User> call, @Nullable Response<User> response) {
                        if (response != null) {
                            Log.d(TAG, "Registration Success");
//

                            if (response.code() == 500){
                                Toast.makeText(LoginActivity.this,"Not registered Please sign up..", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                                finish();
                            }

                            if (response.body() != null) {
                                Log.d(TAG, String.valueOf(response.body().getData()));
                                if (response.body().getStatus() == 200) {
                                    smsToken = String.valueOf(response.body().getData().getSms().getDetails());
                                    Log.d(TAG, response.body().getData().getSms().getDetails().toString());
                                    service = RetrofitBuilder.create(LoginActivity.this);
                                    sessionManager = new SessionManager(LoginActivity.this);

                                    if (response.body().getData().getSms().getStatus().equals("Success")) {

                                        token = String.valueOf(response.body().getData().getToken());
                                        usersresponse = response.body();

//                                        sessionManager.saveStringValue(Const.USER_TOKEN, String.valueOf(response.body().getData().getToken()));
//                                        sessionManager.saveUser(response.body());
//                                sessionManager.saveBooleanValue(Const.IS_LOGIN, true);

                                        userId = response.body().getData().getUserId();
                                        CommonFunctions.destroyProgressBar();
                                        startActivity(new Intent(LoginActivity.this, OTPActivity.class)
                                                .putExtra("otpToken", String.valueOf(response.body().getData().getSms().getDetails()))
                                                .putExtra("mobile", String.valueOf(Objects.requireNonNull(txtPhone.getText())))
                                                .putExtra("fromlogin", true)
                                        );
                                        finish();
                                    }else{
                                        CommonFunctions.destroyProgressBar();

                                        Toast.makeText(LoginActivity.this, "Message not sent please try again..", Toast.LENGTH_LONG).show();

                                    }
                                } else {
                                    CommonFunctions.destroyProgressBar();
                                    Toast.makeText(LoginActivity.this, String.valueOf(response.body().getMessage()), Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    }

                    @Override
                    public void onFailure(@Nullable Call<User> call, @Nullable Throwable t) {
                        CommonFunctions.destroyProgressBar();
                        Log.d(TAG, TAG + t);
                    }
                });

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}