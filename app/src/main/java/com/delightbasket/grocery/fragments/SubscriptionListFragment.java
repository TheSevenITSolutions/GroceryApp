package com.delightbasket.grocery.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.activities.LoginActivity;
import com.delightbasket.grocery.adapters.MainCategoryAdapter;
import com.delightbasket.grocery.adapters.PostSubscriptionAdapter;
import com.delightbasket.grocery.adapters.SortAdapter;
import com.delightbasket.grocery.databinding.BottomsheetSortingBinding;
import com.delightbasket.grocery.databinding.FragmentSubscriptionListBinding;
import com.delightbasket.grocery.model.Categories;
import com.delightbasket.grocery.model.MainCategory;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SubscriptionListFragment extends Fragment {
    private static final int RC_SIGN_IN = 1000;
    private static final String TAG = "homefrag";
    private final int start = 0;
    FragmentSubscriptionListBinding binding;
    Categories listItem;
    PostSubscriptionAdapter categoryAdapter = new PostSubscriptionAdapter();
    List<Categories> listPrice;
    MainCategoryAdapter mainCategoryAdapter = new MainCategoryAdapter();
    RecyclerView rvsubs;
    BottomSheetDialog bottomSheetDialog;
    private final List<Categories.Datum> dataList = new ArrayList<>();
    private final int sortType = 1;
    private final String keyword = "";
    private String userid = null;
    private final int searchstart = 0;
    RetrofitService service;
    String token;
    private MainCategory mainCategoryList;
    private boolean isLoding = true;
    private GoogleSignInClient mGoogleSignInClient;
    private SessionManager sessionManager;
    private String userId = "";
    private BottomsheetSortingBinding sortingBinding;
    private SortAdapter sortAdapter;
    private boolean isSort = true;


    public SubscriptionListFragment() {
        // Required empty public constructor
    }

    public static SubscriptionListFragment newInstance(String param1, String param2) {
        SubscriptionListFragment fragment = new SubscriptionListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        service = RetrofitBuilder.create(getActivity());
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            userid = sessionManager.getUser().getData().getUserId();
            categoryAdapter = new PostSubscriptionAdapter();
            rvsubs = binding.rvSubscription;
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
            rvsubs.setLayoutManager(layoutManager);
            getData();
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }


    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subscription_list, container, false);
        return binding.getRoot();
    }

    private void getData() {
        isLoding = true;

        Call<Categories> categoriesCall = service.getAllData(Const.DEV_KEY, userid, start, Const.LIMIT);
        categoriesCall.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                        Log.d("response", "onResponse: " + response.body().getData().size());
                        dataList.addAll(response.body().getData());

                        categoryAdapter.updateData(dataList);
                        rvsubs.setAdapter(categoryAdapter);
//                        Log.d(TAG, "onScrolled: fetched " + dataList.getData().size());
                    }
                    isLoding = false;

                }

            }

            @Override
            public void onFailure(@Nullable Call<Categories> call, @Nullable Throwable t) {
                Log.d("failed", "onFailure: " + t.getLocalizedMessage());

            }
        });


    }
}
