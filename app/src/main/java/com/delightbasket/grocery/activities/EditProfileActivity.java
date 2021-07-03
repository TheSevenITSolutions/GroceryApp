package com.delightbasket.grocery.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.databinding.ActivityEditProfileBinding;
import com.delightbasket.grocery.model.User;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.MediaStore.MediaColumns.DATA;

public class EditProfileActivity extends AppCompatActivity {
    private static final int GALLERY_CODE = 100;

    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final String TEXT_PLAIN = "text/plain";
    ActivityEditProfileBinding binding;
    RetrofitService service;
    SessionManager sessionManager;
    String token;
    Uri selectedImage = null;
    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        sessionManager = new SessionManager(this);
        binding.setUser(sessionManager.getUser());
        Glide.with(binding.getRoot().getContext())
                .load(Const.BASE_IMG_URL + sessionManager.getUser().getData().getProfileImage())
                .circleCrop()
                .placeholder(R.drawable.app_placeholder)
                .into(binding.imgProfile);
        token = sessionManager.getUser().getData().getToken();
        binding.etFirstName.setText(sessionManager.getUser().getData().getFirstName());
        binding.etLastName.setText(sessionManager.getUser().getData().getLastName());
        initView();
        initListnear();
    }

    private void initView() {
//ll
    }

    private void initListnear() {
        binding.btnSignup.setOnClickListener(v -> updateData());
        binding.imgProfile.setOnClickListener(v -> choosePhoto());
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
                choosePhoto();
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    private void choosePhoto() {
        if(checkPermission()) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, GALLERY_CODE);
        } else {
            requestPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();
            Glide.with(EditProfileActivity.this)
                    .load(selectedImage)

                    .circleCrop()
                    .into(binding.imgProfile);

            String[] filePathColumn = {DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


        }
    }


    private void updateData() {
        binding.pd.setVisibility(View.VISIBLE);
        service = RetrofitBuilder.create(this);

        RequestBody firstname = RequestBody.create(MediaType.parse(TEXT_PLAIN), binding.etFirstName.getText().toString());
        RequestBody lastname = RequestBody.create(MediaType.parse(TEXT_PLAIN), binding.etLastName.getText().toString());
        RequestBody email = RequestBody.create(MediaType.parse(TEXT_PLAIN), binding.etEmail.getText().toString());

        HashMap<String, RequestBody> map = new HashMap<>();

        MultipartBody.Part body = null;
        if(picturePath != null) {
            File file = new File(picturePath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("profile_image", file.getName(), requestFile);

        }
        map.put("first_name", firstname);
        map.put("last_name", lastname);
        map.put("email", email);


        Call<User> call = service.updateUser("gng!123", token, map, body);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                if(response.code() == 200) {
                    User user = response.body();
                    if(response.body().getStatus() == 200 && user != null) {
                        user.getData().setToken(token);
                        sessionManager.saveUser(user);
                        binding.pd.setVisibility(View.GONE);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                        binding.pd.setVisibility(View.GONE);
                    }

                } else {
                    binding.pd.setVisibility(View.GONE);
                    Toast.makeText(EditProfileActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("uuu", "onFailure: error " + t);
            }
        });
    }

    public void onClickBack(View view) {
        super.onBackPressed();
    }
}
