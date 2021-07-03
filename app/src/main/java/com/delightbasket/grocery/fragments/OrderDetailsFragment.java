package com.delightbasket.grocery.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.activities.LoginActivity;
import com.delightbasket.grocery.adapters.OrderDetailFragmentsPagerAdapter;
import com.delightbasket.grocery.databinding.BottomsheetHaveAnIssueBinding;
import com.delightbasket.grocery.databinding.FragmentOrderDetailsBinding;
import com.delightbasket.grocery.model.OrderDetailRoot;
import com.delightbasket.grocery.model.RestResponse;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsFragment extends Fragment {
    FragmentOrderDetailsBinding binding;
    private String orderID;
    SessionManager sessionManager;
    private String token;
    private OrderDetailRoot.Data detail;
    private BottomSheetDialog bottomSheetDialog;
    private RetrofitService service;
    private String status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_details, container, false);
        sessionManager = new SessionManager(getActivity());
        service = RetrofitBuilder.create(getActivity());
        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();

            binding.lyt404.setVisibility(View.GONE);
            orderID = getArguments().getString("orderId");
            status = getArguments().getString("status");
            if(orderID != null && !orderID.equals("")) {
                binding.tvOrderId.setText(orderID);
                getData(orderID);
            }
            initView();
            initListnear();

        }else{
            startActivity(new Intent(getActivity(), LoginActivity.class));

        }

        return binding.getRoot();
    }

    private void getData(String orderID) {
        binding.lyt404.setVisibility(View.GONE);
        binding.pBar.setVisibility(View.VISIBLE);
        Call<OrderDetailRoot> call = service.getOrdersDetail(Const.DEV_KEY, token, orderID);
        call.enqueue(new Callback<OrderDetailRoot>() {
            @Override
            public void onResponse(Call<OrderDetailRoot> call, Response<OrderDetailRoot> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus() == 200 && response.body().getData() != null) {
                        detail = response.body().getData();

                        OrderDetailFragmentsPagerAdapter pagerAdapter = new OrderDetailFragmentsPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, detail, status);
                        binding.viewPager.setAdapter(pagerAdapter);
                        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tablayout));

                        binding.pBar.setVisibility(View.GONE);
                    } else {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderDetailRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void initListnear() {
        binding.imgAerrowBack.setOnClickListener(v -> getActivity().onBackPressed());
        binding.lytBottom.setOnClickListener(v -> openBottomSheet());
        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//ll
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//ll
            }
        });
    }

    private void openBottomSheet() {

        bottomSheetDialog = new BottomSheetDialog(getActivity());
        BottomsheetHaveAnIssueBinding bottomsheetHaveAnIssueBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottomsheet_have_an_issue, null, false);
        bottomSheetDialog.setContentView(bottomsheetHaveAnIssueBinding.getRoot());
        bottomsheetHaveAnIssueBinding.tvSubmit.setOnClickListener(v -> sendIssue(bottomsheetHaveAnIssueBinding));
        bottomSheetDialog.show();
        bottomsheetHaveAnIssueBinding.btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }
    private void sendIssue(BottomsheetHaveAnIssueBinding bottomsheetHaveAnIssueBinding) {

        String title = bottomsheetHaveAnIssueBinding.etTitle.getText().toString();
        String des = bottomsheetHaveAnIssueBinding.etDes.getText().toString();
        String mobile = bottomsheetHaveAnIssueBinding.etMobile.getText().toString();
        if(title.equals("")) {
            Toast.makeText(getActivity(), "Write Title First", Toast.LENGTH_SHORT).show();
            return;
        }
        if(des.equals("")) {
            Toast.makeText(getActivity(), "Write Description First", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mobile.equals("")) {
            Toast.makeText(getActivity(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mobile.length() > 11) {
            Toast.makeText(getActivity(), "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mobile.length() < 10) {
            Toast.makeText(getActivity(), "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.pBar.setVisibility(View.VISIBLE);
        Call<RestResponse> call = service.raiseComplain(Const.DEV_KEY, token, orderID, title, mobile, des);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if(response.code() == 200 && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    binding.pBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                binding.pBar.setVisibility(View.GONE);
            }
        });
    }

    private void initView() {
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Summary"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Items"));


    }


}