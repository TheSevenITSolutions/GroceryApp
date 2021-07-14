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
import com.delightbasket.grocery.adapters.SubsUserAdapter;
import com.delightbasket.grocery.model.ProductsItem;
import com.delightbasket.grocery.model.SubsUserIdResponse;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SubsUserFragment extends Fragment {
    SubsUserAdapter subsUserAdapter = new SubsUserAdapter();
    RetrofitService service;
    List<ProductsItem> products = new ArrayList();
    RecyclerView rvsubsuserid;
    String token;
    private String userid = null;
    private SessionManager sessionManager;

    public SubsUserFragment() {
        // Required empty public constructor
    }


    public static SubsUserFragment newInstance(String param1, String param2) {
        SubsUserFragment fragment = new SubsUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_subs_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        service = RetrofitBuilder.create(getActivity());
        sessionManager = new SessionManager(requireContext());
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userid = sessionManager.getUser().getData().getUserId();
            token = sessionManager.getUser().getData().getToken();
        } else {
//            startActivity(new Intent(requireContext(), LoginActivity.class));
        }

        rvsubsuserid = view.findViewById(R.id.rvsubsUser);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        rvsubsuserid.setLayoutManager(layoutManager);

        subsUserAdapter = new SubsUserAdapter();


        getuser();

    }

    private void getuser() {

        Call<SubsUserIdResponse> call = service.getSubscriptionListByUserId(token, Const.DEV_KEY, "0", "100", userid);
        call.enqueue(new Callback<SubsUserIdResponse>() {
            @Override
            public void onResponse(@NotNull Call<SubsUserIdResponse> call, Response<SubsUserIdResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus() == 401) {
                        Toast.makeText(requireContext(), "Not Subscribed Any Product by userid", Toast.LENGTH_LONG).show();
                    } else {
                        products.addAll(response.body().getData().getProducts());
                        subsUserAdapter.updateData(products);
                        rvsubsuserid.setAdapter(subsUserAdapter);
                    }
                }
            }

            @Override
            public void onFailure(@Nullable Call<SubsUserIdResponse> call, @Nullable Throwable t) {
                Log.d("failed", "onFailure: " + t.getLocalizedMessage());

            }
        });

    }
}