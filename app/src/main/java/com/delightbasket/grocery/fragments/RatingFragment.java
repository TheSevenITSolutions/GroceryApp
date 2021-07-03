package com.delightbasket.grocery.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.activities.LoginActivity;
import com.delightbasket.grocery.adapters.RatingAdapter;
import com.delightbasket.grocery.databinding.FragmentRatingBinding;
import com.delightbasket.grocery.databinding.ItemLoginBinding;
import com.delightbasket.grocery.model.RatingRoot;
import com.delightbasket.grocery.model.User;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RatingFragment extends Fragment {

    private static final String TAG = "rati1gfrag";
    private static final int RC_SIGN_IN = 100;
    FragmentRatingBinding binding;
    SessionManager sessionManager;
    RatingAdapter ratingAdapter = new RatingAdapter();
    private String token;
    private int start = 0;
    private boolean isLoding = false;
    String notificationToken;
    private GoogleSignInClient mGoogleSignInClient;
    private RetrofitService service;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rating, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getContext());
        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            getData();

            binding.rvrating.setAdapter(ratingAdapter);
            initListnear();
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

    private void googleLogin() {
        service = RetrofitBuilder.create(getActivity());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END config_signin]
        if(getActivity() != null) {
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
            showGoogleSheet();
        }

    }

    private void showGoogleSheet() {
        if(getActivity() != null) {
            bottomSheetDialog = new BottomSheetDialog(getActivity());
            bottomSheetDialog.setOnShowListener(dialog -> {

                // In a previous life I used this method to get handles to the positive and negative buttons
                // of a dialog in order to change their Typeface. Good ol' days.

                BottomSheetDialog d = (BottomSheetDialog) dialog;

                // This is gotten directly from the source of BottomSheetDialog
                // in the wrapInBottomSheet() method
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);

                // Right here!
                BottomSheetBehavior.from(bottomSheet)
                        .setState(BottomSheetBehavior.STATE_EXPANDED);
            });

            ItemLoginBinding itemLoginBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.item_login, null, false);
            bottomSheetDialog.setContentView(itemLoginBinding.getRoot());
            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);
            itemLoginBinding.googlelogin.setOnClickListener(view -> gotoGoogleLogin());
        }
    }

    private void gotoGoogleLogin() {
        binding.pd.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            binding.pd.setVisibility(View.GONE);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if(account != null) {
                    registerUser(account);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getIdToken());


                }
            } catch(ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void registerUser(GoogleSignInAccount account) {

        Log.d(TAG, "registerUser: " + account.getPhotoUrl());
        Uri photoUrl = account.getPhotoUrl();
        if(photoUrl == null) {
            photoUrl = Uri.parse("");
        }
        Call<User> call = service.registerUser(Const.DEV_KEY,
                account.getDisplayName(), account.getDisplayName(), account.getEmail(),
                "gmail", account.getEmail(), "1", notificationToken, String.valueOf(photoUrl)
        );
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@Nullable Call<User> call, @Nullable Response<User> response) {

                if(response != null) {
                    sessionManager.saveUser(response.body());
                    sessionManager.saveBooleanValue(Const.IS_LOGIN, true);

                    bottomSheetDialog.dismiss();

                    if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                        token = sessionManager.getUser().getData().getToken();
                        binding.shimmer.setVisibility(View.VISIBLE);
                        getData();
                    } else {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                    binding.rvrating.setAdapter(ratingAdapter);
                    initListnear();


                }

            }

            @Override
            public void onFailure(@Nullable Call<User> call, @Nullable Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

    private void initListnear() {
        binding.rvrating.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!binding.rvrating.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvrating.getLayoutManager();
                    Log.d(TAG, "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d(TAG, "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d(TAG, "onScrollStateChanged:188 " + totalitem);
                    if(!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {
                        Log.d(TAG, "onScrollStateChanged: " + start);
                        start = start + Const.LIMIT;
                        binding.pd.setVisibility(View.VISIBLE);
                        getData();
                    }
                }
            }
        });

    }

    void getData() {
        Call<RatingRoot> call = RetrofitBuilder.create(getActivity()).getRatings(Const.DEV_KEY, token, start, Const.LIMIT);
        call.enqueue(new Callback<RatingRoot>() {
            @Override
            public void onResponse(Call<RatingRoot> call, Response<RatingRoot> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                        ratingAdapter.addData(response.body().getData());
                    } else if(start == 0 && response.body().getStatus() != 200) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }
                binding.pd.setVisibility(View.GONE);
                binding.shimmer.setVisibility(View.GONE);
                binding.swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<RatingRoot> call, Throwable t) {
                binding.pd.setVisibility(View.GONE);
            }
        });
    }
}