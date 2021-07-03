package com.delightbasket.grocery.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.MainCategoryAdapter;
import com.delightbasket.grocery.databinding.ActivityAllCategoriesBinding;
import com.delightbasket.grocery.model.MainCategory;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllCategoriesActivity extends AppCompatActivity {
    ActivityAllCategoriesBinding binding;
    MainCategoryAdapter mainCategoryAdapter;
    RetrofitService service;
    private SessionManager sessionManager;
    private String userid = null;
    private MainCategory mainCategoryList;
    private boolean isLoding = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_categories);
        initView();
    }

    private void initView() {
        sessionManager = new SessionManager(this);
        service = RetrofitBuilder.create(this);
        binding.shimmer.startShimmer();
        mainCategoryAdapter = new MainCategoryAdapter();
        binding.rvMainCategory.setAdapter(mainCategoryAdapter);
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userid = sessionManager.getUser().getData().getUserId();
        }
//        binding.pBar.setVisibility(View.GONE);
        getMainCategoryData();
    }


    private void getMainCategoryData() {
        isLoding = true;

        Call<MainCategory> categoriesCall = service.getCategoryList(Const.DEV_KEY, 0, 0);
        categoriesCall.enqueue(new Callback<MainCategory>() {
            @Override
            public void onResponse(Call<MainCategory> call, Response<MainCategory> response) {
                if (response.code() == 200) {
                    if (response.body().status == 200 && !response.body().data.data.isEmpty()) {

                        mainCategoryList = response.body();
                        mainCategoryAdapter.updateData(mainCategoryList.data.data);
//                        Log.d(TAG, "onScrolled: fetched " + mainCategoryList.data.size());


                    }
                    isLoding = false;
                    binding.pd2.setVisibility(View.GONE);
                    binding.swipe.setRefreshing(false);
                    binding.shimmer.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@Nullable Call<MainCategory> call, @Nullable Throwable t) {
                //ll
                binding.pd2.setVisibility(View.GONE);
                binding.swipe.setRefreshing(false);
                binding.shimmer.setVisibility(View.GONE);
            }
        });


    }


}