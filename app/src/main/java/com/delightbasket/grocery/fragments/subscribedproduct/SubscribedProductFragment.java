package com.delightbasket.grocery.fragments.subscribedproduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.activities.LoginActivity;
import com.delightbasket.grocery.databinding.FragmentSubscribedProductBinding;
import com.delightbasket.grocery.fragments.subscribedproduct.tabitemfragment.SubsCategoryFragment;
import com.delightbasket.grocery.fragments.subscribedproduct.tabitemfragment.SubsUserFragment;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class SubscribedProductFragment extends Fragment {

    SessionManager sessionManager;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SubsUserFragment subsUserFragment;
    private SubsCategoryFragment subsCategoryFragment;
    RetrofitService service;
    String token;
    FragmentSubscribedProductBinding binding;
    private String userid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subscribed_product, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            viewPager = binding.viewPager;
            tabLayout = binding.tabLayout;

            subsUserFragment = new SubsUserFragment();

            subsCategoryFragment = new SubsCategoryFragment();

            tabLayout.setupWithViewPager(viewPager);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);

            viewPagerAdapter.addFragment(subsCategoryFragment, "Category ID");
            viewPagerAdapter.addFragment(subsUserFragment, "User ID");
            viewPager.setAdapter(viewPagerAdapter);
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

}


class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragmentTitles = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    //add fragment to the viewpager
    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        fragmentTitles.add(title);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    //to setup title of the tab layout
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }
}
