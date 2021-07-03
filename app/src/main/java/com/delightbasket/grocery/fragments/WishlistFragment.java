package com.delightbasket.grocery.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.activities.LoginActivity;
import com.delightbasket.grocery.activities.MainActivity;
import com.delightbasket.grocery.activities.ProductDetailActivity;
import com.delightbasket.grocery.adapters.WishlistAdapter;
import com.delightbasket.grocery.databinding.FragmentWishlistBinding;
import com.delightbasket.grocery.databinding.ItemLoginBinding;
import com.delightbasket.grocery.model.RestResponse;
import com.delightbasket.grocery.model.User;
import com.delightbasket.grocery.model.Wishlist;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WishlistFragment extends Fragment {
    private static final String TAG = "whilistfrag";
    FragmentWishlistBinding binding;
    WishlistAdapter wishlistAdapter = new WishlistAdapter();
    RetrofitService service;
    SessionManager sessionManager;
    List<Wishlist.Datum> data;
    private String token;
    private static final int RC_SIGN_IN = 100;
    BottomSheetDialog bottomSheetDialog;
    private String notificationToken;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wishlist, container, false);
        service = RetrofitBuilder.create(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getContext());
        Log.d("TAG", "onActivityCreated: " + sessionManager.getBooleanValue(Const.IS_LOGIN));
        binding.shimmer.startShimmer();
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {

            token = sessionManager.getUser().getData().getToken();
            binding.pBar.setVisibility(View.GONE);
            getData();
        } else {
            binding.shimmer.setVisibility(View.GONE);
            binding.pBar.setVisibility(View.GONE);
            getNotificationToken();
//            googleLogin();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        initView();
        initListnear();
//        binding.swipe.setOnRefreshListener(this);
    }

    private void getNotificationToken() {
        try {
            notificationToken = sessionManager.getStringValue(Const.NOTIFICATION_TOKEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void googleLogin() {
        service = RetrofitBuilder.create(getActivity());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END config_signin]
        if (getActivity() != null) {
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
            showGoogleSheet();
        }

    }

    private void showGoogleSheet() {
        if (getActivity() != null) {
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
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.show();
            itemLoginBinding.googlelogin.setOnClickListener(view -> gotoGoogleLogin());
            itemLoginBinding.btnclose.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).pos.set(1);
                    ((MainActivity) getActivity()).closeDrawer();
                    ((MainActivity) getActivity()).loadFragment(new HomeFragment());
                    ((MainActivity) getActivity()).setTitleText("Home");
                }

            });

        }
    }

    private void gotoGoogleLogin() {
        binding.pBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            binding.pBar.setVisibility(View.GONE);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    registerUser(account);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getIdToken());


                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void registerUser(GoogleSignInAccount account) {

        Log.d(TAG, "registerUser: " + account.getPhotoUrl());
        Uri photoUrl = account.getPhotoUrl();
        if (photoUrl == null) {
            photoUrl = Uri.parse("");
        }
        Call<User> call = service.registerUser(Const.DEV_KEY,
                account.getDisplayName(), account.getDisplayName(), account.getEmail(),
                "gmail", account.getEmail(), "1", notificationToken, String.valueOf(photoUrl)
        );
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@Nullable Call<User> call, @Nullable Response<User> response) {

                if (response != null) {
                    sessionManager.saveUser(response.body());
                    sessionManager.saveBooleanValue(Const.IS_LOGIN, true);
                    if (response.body() != null) {
                        saveUserToken(response.body().getData().getToken());
                    }
                    bottomSheetDialog.dismiss();

                }
                sessionManager = new SessionManager(getContext());
                Log.d("TAG", "onActivityCreated: " + sessionManager.getBooleanValue(Const.IS_LOGIN));
                binding.shimmer.startShimmer();
                if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {

                    token = sessionManager.getUser().getData().getToken();
                    binding.pBar.setVisibility(View.GONE);
                    getData();
                } else {
                    binding.shimmer.setVisibility(View.GONE);
                    binding.pBar.setVisibility(View.GONE);
                    getNotificationToken();
//                    googleLogin();
                    startActivity(new Intent(getActivity(), LoginActivity.class));

                }

                initView();
                initListnear();

            }

            @Override
            public void onFailure(@Nullable Call<User> call, @Nullable Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

    private void saveUserToken(String token) {
        sessionManager.saveStringValue(Const.USER_TOKEN, token);

        bottomSheetDialog.dismiss();
    }


    private void initListnear() {
        wishlistAdapter.setWishlistItemClickListnear((pos, datum, binding1, work, priceUnit) -> {
            switch (work) {
                case "Delete":
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setTitle("Are you Delete this Product ?");
                    builder.setPositiveButton("Yes", (dialog, which) -> removeWishlistItem(datum, pos));
                    builder.setNegativeButton("No", (dialog, which) -> builder.create().dismiss());
                    builder.setCancelable(false);
                    builder.create().show();


                    break;
                case "Weight":
                    break;
                case "Addmore":
                    //  q = priceUnit.
                    Log.d("qqqu", "initListnear: p quantity " + priceUnit.getQuantity());
                    Log.d("qqq", "initListnear: " + priceUnit.getQuantity());
                    long priceUnitQuantity = priceUnit.getQuantity() + 1;

                    break;
                case "Lessone":
                    Log.d("qqqu", "initListnear: p quantity " + priceUnit.getQuantity());
                    long priceUnitQuantity1 = priceUnit.getQuantity() - 1;

                    break;
                default:

            }
            if (work.equals("Open")) {
                openDeatailActivity(datum);
            }
        });
    }

    private void openDeatailActivity(Wishlist.Datum datum) {
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra(Const.ProductID, datum.getProductId());
        startActivity(intent);
    }


    private void removeWishlistItem(Wishlist.Datum datum, int pos) {
        binding.pBar.setVisibility(View.VISIBLE);
        Call<RestResponse> call = service.removeFromWishlist(Const.DEV_KEY, token, datum.getProductId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {

                    data.remove(pos);
                    wishlistAdapter.notifyItemRemoved(pos);
                    wishlistAdapter.notifyDataSetChanged();
                    binding.pBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                binding.pBar.setVisibility(View.GONE);
            }
        });
    }

    private void getData() {


        Call<Wishlist> call = service.getWishlist(Const.DEV_KEY, token);
        call.enqueue(new Callback<Wishlist>() {
            @Override
            public void onResponse(Call<Wishlist> call, Response<Wishlist> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                            data = response.body().getData();
                            wishlistAdapter.addData(data);
                            binding.rvWishlist.setAdapter(wishlistAdapter);

                        } else {
                            binding.lytEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                    binding.pBar.setVisibility(View.GONE);
                    binding.shimmer.setVisibility(View.GONE);
                    binding.swipe.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<Wishlist> call, Throwable t) {
//ll
            }
        });
    }

    private void initView() {
//ll
    }

//    @Override
//    public void onRefresh() {
//        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
//
//            token = sessionManager.getUser().getData().getToken();
//            getData();
//        } else {
//            binding.shimmer.setVisibility(View.GONE);
//            binding.pBar.setVisibility(View.GONE);
//            getNotificationToken();
////            googleLogin();
//            startActivity(new Intent(getActivity(), LoginActivity.class));
//
//        }
//
//        initView();
//
//    }
}