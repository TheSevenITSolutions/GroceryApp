package com.delightbasket.grocery.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.adapters.FaqAdapter;
import com.delightbasket.grocery.databinding.FragmentFAQsBinding;
import com.delightbasket.grocery.model.FaqRoot;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAQsFragment extends Fragment {
    FragmentFAQsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_f_a_qs, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.pd.setVisibility(View.VISIBLE);
        Call<FaqRoot> call = RetrofitBuilder.create(getActivity()).getFaqs(Const.DEV_KEY);
        call.enqueue(new Callback<FaqRoot>() {
            @Override
            public void onResponse(Call<FaqRoot> call, Response<FaqRoot> response) {
                if(response.isSuccessful() && response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                    FaqAdapter faqAdapter = new FaqAdapter(response.body().getData());
                    binding.rvfaqs.setAdapter(faqAdapter);

                }
                binding.pd.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<FaqRoot> call, Throwable t) {
//ll
            }
        });
    }
}