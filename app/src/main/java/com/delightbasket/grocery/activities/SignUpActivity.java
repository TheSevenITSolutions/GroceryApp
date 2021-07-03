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

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.txtLogin)
    AppCompatTextView txtLogin;
    @BindView(R.id.txtLoginContinue)
    AppCompatTextView txtLoginContinue;
    @BindView(R.id.txtName)
    AppCompatEditText txtName;
    @BindView(R.id.txtPhone)
    AppCompatEditText txtPhone;
    @BindView(R.id.txtEmail)
    AppCompatEditText txtEmail;
    @BindView(R.id.btnSignup)
    AppCompatButton btnSignup;
    @BindView(R.id.cardSignup)
    CardView cardSignup;
    @BindView(R.id.txtNotAccount)
    TextView txtNotAccount;
    @BindView(R.id.txtSignin)
    TextView txtSignin;

    String TAG = "SignUpActivity";
    SessionManager sessionManager;
    String userId = "";
    RetrofitService service;
    String smsToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        ButterKnife.bind(this);

        service = RetrofitBuilder.create(this);

        sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userId = sessionManager.getUser().getData().getUserId();
        }

    }

    @OnClick({R.id.btnSignup, R.id.txtSignin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignup:
                if (String.valueOf(txtName.getText()).equals("")) {
                    Toast.makeText(this, "Please enter you name", Toast.LENGTH_LONG).show();
                } else if (String.valueOf(txtPhone.getText()).equals("")) {
                    Toast.makeText(this, "Please enter your phone number ", Toast.LENGTH_LONG).show();

                } else {
                    registerUser();
                }


                break;
            case R.id.txtSignin:
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void registerUser() {

        try {
            if (CommonFunctions.checkConnection(this)){
                CommonFunctions.createProgressBar(this,"Signing Up..");
                Log.d(TAG, "registerUser: ");
                String notificationToken = sessionManager.getStringValue(Const.NOTIFICATION_TOKEN);
                Call<User> call = service.registerUser(
                        Const.DEV_KEY,
                        Objects.requireNonNull(txtName.getText()).toString(),
                        Objects.requireNonNull(txtPhone.getText()).toString(), Objects.requireNonNull(txtEmail.getText()).toString(),
                        "1", "1");
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@Nullable Call<User> call, @Nullable Response<User> response) {
                        if (response != null) {
                            Log.d(TAG, "Registration Success");
                            sessionManager.saveUser(response.body());

                            Log.d(TAG, response.toString());

                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {

//                            sessionManager.saveStringValue(Const.USER_TOKEN, String.valueOf(response.body().getData().getToken()));
                                    Log.d(TAG, response.body().getData().getSms().getDetails().toString());
                                    service = RetrofitBuilder.create(SignUpActivity.this);
                                    sessionManager = new SessionManager(SignUpActivity.this);
                                    if (response.body().getData().getSms().getStatus().equalsIgnoreCase("success")) {

//                                sessionManager.saveBooleanValue(Const.IS_LOGIN, true);
                                        smsToken = String.valueOf(response.body().getData().getSms().getDetails());
                                        sessionManager.saveStringValue(Const.USER_TOKEN, String.valueOf(response.body().getData().getToken()));
                                        sessionManager.saveUser(response.body());
                                        CommonFunctions.destroyProgressBar();
                                        startActivity(new Intent(SignUpActivity.this, OTPActivity.class)
                                                .putExtra("otpToken", smsToken)
                                                .putExtra("mobile", Objects.requireNonNull(txtPhone.getText()).toString())
                                        );
                                        finish();
                                    }else{
                                        CommonFunctions.destroyProgressBar();
                                        Toast.makeText(SignUpActivity.this, "Message not sent please try again.", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    CommonFunctions.destroyProgressBar();
                                    Toast.makeText(SignUpActivity.this, String.valueOf(response.body().getMessage()), Toast.LENGTH_LONG).show();
                                }

                            }
                        }

                    }

                    @Override
                    public void onFailure(@Nullable Call<User> call, @Nullable Throwable t) {
                        Log.d(TAG, TAG + t);
                        CommonFunctions.destroyProgressBar();
                        Toast.makeText(SignUpActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}