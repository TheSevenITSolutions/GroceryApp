package com.delightbasket.grocery.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.MainCategoryAdapter;
import com.delightbasket.grocery.adapters.PostSubscriptionAdapter;
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
    RetrofitService service;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1000;
    private static final String TAG = "homefrag";
    FragmentSubscriptionListBinding binding;
    Categories listItem;
    PostSubscriptionAdapter categoryAdapter = new PostSubscriptionAdapter();
    List<Categories> listPrice;
    MainCategoryAdapter mainCategoryAdapter = new MainCategoryAdapter();
    RecyclerView rvsubs;
    private SessionManager sessionManager;
    BottomSheetDialog bottomSheetDialog;
    private String userid = null;
    private int start = 0;
    private List<Categories.Datum> dataList = new ArrayList<>();
    private MainCategory mainCategoryList;
    private boolean isLoding = true;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscription_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        service = RetrofitBuilder.create(getActivity());
        categoryAdapter = new PostSubscriptionAdapter();
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userid = sessionManager.getUser().getData().getUserId();
        }
        rvsubs = view.findViewById(R.id.rv_Subscription);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        rvsubs.setLayoutManager(layoutManager);

        getData();
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
