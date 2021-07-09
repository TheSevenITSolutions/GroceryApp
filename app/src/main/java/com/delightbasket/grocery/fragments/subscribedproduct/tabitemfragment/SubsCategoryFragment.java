package com.delightbasket.grocery.fragments.subscribedproduct.tabitemfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.SubsCategoryAdapter;
import com.delightbasket.grocery.model.SubscriptionListByCategoryIdResponse;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SubsCategoryFragment extends Fragment {
    SubsCategoryAdapter subsUserAdapter = new SubsCategoryAdapter();
    RetrofitService service;
    List<SubscriptionListByCategoryIdResponse.subs> products = new ArrayList();
    RecyclerView rvsubscategoryid;
    String token;
    private String userid = null;
    private SessionManager sessionManager;

    public SubsCategoryFragment() {
        // Required empty public constructor
    }

    public static SubsCategoryFragment newInstance(String param1, String param2) {
        SubsCategoryFragment fragment = new SubsCategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subs_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        service = RetrofitBuilder.create(getActivity());
        sessionManager = new SessionManager(requireContext());
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userid = sessionManager.getUser().getData().getUserId();
        }
        token = sessionManager.getUser().getData().getToken();
        rvsubscategoryid = view.findViewById(R.id.rvSubsCategory);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        rvsubscategoryid.setLayoutManager(layoutManager);

        subsUserAdapter = new SubsCategoryAdapter();


        getuser();

    }

    private void getuser() {

        Call<SubscriptionListByCategoryIdResponse> call = service.getSubscriptionList(token, Const.DEV_KEY, "0", "100", "7");
        call.enqueue(new Callback<SubscriptionListByCategoryIdResponse>() {
            @Override
            public void onResponse(@NotNull Call<SubscriptionListByCategoryIdResponse> call, Response<SubscriptionListByCategoryIdResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus() == 401) {
                        Toast.makeText(requireContext(), "Not Subscribed Any Product", Toast.LENGTH_LONG).show();
                    } else {
                        products.addAll(response.body().getData().getProducts());
                        subsUserAdapter.updateData(products);
                        rvsubscategoryid.setAdapter(subsUserAdapter);
                    }
                }
            }

            @Override
            public void onFailure(@Nullable Call<SubscriptionListByCategoryIdResponse> call, @Nullable Throwable t) {
                Log.d("failed", "onFailure: " + t.getLocalizedMessage());

            }
        });

    }
}