package com.delightbasket.grocery.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.delightbasket.grocery.BuildConfig;
import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.activities.AddWalletRazorpay;
import com.delightbasket.grocery.activities.DeliveryAddressActivity;
import com.delightbasket.grocery.activities.EditProfileActivity;
import com.delightbasket.grocery.activities.LoginActivity;
import com.delightbasket.grocery.activities.MainActivity;
import com.delightbasket.grocery.activities.MyOrdersActivity;
import com.delightbasket.grocery.activities.WebActivity;
import com.delightbasket.grocery.databinding.BottomSheetconfirmationBinding;
import com.delightbasket.grocery.databinding.FragmentProfileBinding;
import com.delightbasket.grocery.databinding.ItemLoginBinding;
import com.delightbasket.grocery.model.Address;
import com.delightbasket.grocery.model.RestResponse2;
import com.delightbasket.grocery.model.User;
import com.delightbasket.grocery.model.WalletDataResponse;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "profilefrag";
    private static final int RC_SIGN_IN = 100;
    private static final String TITLESSTR = "TITLE";
    RetrofitService service;
    FragmentProfileBinding binding;
    String notificationToken;
    SessionManager sessionManager;
    BottomSheetDialog bottomSheetDialog;
//    private GoogleSignInClient mGoogleSignInClient;
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            sessionManager = new SessionManager(getActivity());
        }
        service = RetrofitBuilder.create(getActivity());

        String verson = BuildConfig.VERSION_NAME;
        binding.tvVerson.setText("Verson : " + verson);
        getUserFromLocal();
        initListnear();
        binding.swipe.setOnRefreshListener(this);
        binding.ivAddWallet.setOnClickListener(v -> {
            showAddPriceDialog();
        });
    }

    private void showAddPriceDialog() {
        View mDialogView = LayoutInflater.from(getContext()).inflate(R.layout.add_wallet_dialog, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog).setView(mDialogView);
        mBuilder.setCancelable(true);
        AlertDialog mAlertDialog = mBuilder.show();
        EditText etPrice;
        TextView btnAddMoney;
        btnAddMoney = mDialogView.findViewById(R.id.btnAddMoney);
        etPrice = mDialogView.findViewById(R.id.etPrice);
        btnAddMoney.setOnClickListener(v -> {
            mAlertDialog.dismiss();
            Intent intent = new Intent(getContext(), AddWalletRazorpay.class);
            intent.putExtra("Amount", etPrice.getText().toString());
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        callGetWalletData();
    }

    private void callGetWalletData() {
        binding.pd.setVisibility(View.VISIBLE);
        Call<WalletDataResponse> call = service.getWalletData(Const.DEV_KEY, token);
        call.enqueue(new Callback<WalletDataResponse>() {
            @Override
            public void onResponse(Call<WalletDataResponse> call, Response<WalletDataResponse> response) {
                binding.pd.setVisibility(View.GONE);
                if (response.code() == 200 && response.body().getStatus() == 200 && response.body().getData() != null) {
                    binding.tvWallet.setText(response.body().getData().getAmount());
                }
            }

            @Override
            public void onFailure(Call<WalletDataResponse> call, Throwable t) {
                binding.pd.setVisibility(View.GONE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void getUserFromLocal() {

        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            User user = sessionManager.getUser();
            showUserDetails(user.getData());
            getDefultAddress();
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getNotificationToken();
//            googleLogin();
            binding.scrollView.setVisibility(View.GONE);
        }
    }

    private void getDefultAddress() {
        Call<Address> call = service.getDefaultAddress(Const.DEV_KEY, token);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(@NonNull Call<Address> call, @NonNull Response<Address> response) {
                if(response.code() == 200 && Objects.requireNonNull(response.body()).getStatus() == 200 && !Objects.requireNonNull(response.body()).getData().isEmpty()) {
                    Address.Datum address = Objects.requireNonNull(response.body()).getData().get(0);
                    binding.tvAdress.setText(address.getHomeNo().concat(" " + address.getSociety() + " " + address.getStreet() + " " +
                            address.getArea() + " " + address.getCity() + " " + address.getPincode()));
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }

    private void initListnear() {
        binding.imgIconedit.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        binding.lytLogOut.setOnClickListener(v -> openConfirmDialog());
        binding.lytDeliveryAddress.setOnClickListener(v -> startActivity(new Intent(getActivity(), DeliveryAddressActivity.class)));
        binding.lytMyorders.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyOrdersActivity.class)));
        binding.lytabout.setOnClickListener(v -> startActivity(new Intent(getActivity(), WebActivity.class).putExtra("URL", Const.ABOUTUS).putExtra(TITLESSTR, "About Us")));
        binding.lytpolicy.setOnClickListener(v -> startActivity(new Intent(getActivity(), WebActivity.class).putExtra("URL", Const.PRIVACYPOLICY).putExtra(TITLESSTR, "Privacy Policy")));
        binding.lytterms.setOnClickListener(v -> startActivity(new Intent(getActivity(), WebActivity.class).putExtra("URL", Const.TERMS).putExtra(TITLESSTR, "Terms & Conditions")));
        binding.lytrefer.setOnClickListener(v -> {
            final String appPackageName = getActivity().getPackageName();

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch(android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });
    }

    private void openConfirmDialog() {
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        BottomSheetconfirmationBinding sheetconfirmationBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheetconfirmation, null, false);
        bottomSheetDialog.setContentView(sheetconfirmationBinding.getRoot());
        bottomSheetDialog.show();
        sheetconfirmationBinding.tvCencel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        sheetconfirmationBinding.tvYes.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        service = RetrofitBuilder.create(getActivity());
        Call<RestResponse2> call = service.logout(Const.DEV_KEY, token);
        call.enqueue(new Callback<RestResponse2>() {
            @Override
            public void onResponse(Call<RestResponse2> call, Response<RestResponse2> response) {
                bottomSheetDialog.dismiss();
                if(response.code() == 200) {
                    if(response.body().getSuccessCode() == 200) {
                        sessionManager.saveUser(new User());
                        sessionManager.saveStringValue(Const.USER_TOKEN, "");
                        sessionManager.saveBooleanValue(Const.IS_LOGIN, false);
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finishAffinity();
                    } else {
                        Toast.makeText(getContext(), response.body().getResponseMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResponse2> call, Throwable t) {
                Log.d(TAG, "onFailure: error 138 " + t);
            }
        });
    }



    private void showUserDetails(User.Data user) {
        Log.d(TAG, "showUserDetails:img  " + user.getProfileImage());
        binding.tvUserName.setText(user.getFullname());
        binding.tvUserEmail.setText(user.getEmail());

        Bitmap placeholder = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.placeholder);
        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), placeholder);
        circularBitmapDrawable.setCircular(true);
        Glide.with(binding.getRoot().getContext())
                .load(Const.BASE_IMG_URL + user.getProfileImage())
                .placeholder(R.drawable.ic_launcher_foreground)
                .circleCrop()
                //  .placeholder(circularBitmapDrawable)
                .into(binding.imgprofile);
        binding.swipe.setRefreshing(false);

    }


    private void getNotificationToken() {

        notificationToken = sessionManager.getStringValue(Const.NOTIFICATION_TOKEN);
    }

    private void googleLogin() {
        service = RetrofitBuilder.create(getActivity());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END config_signin]
        if(getActivity() != null) {
//            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
            showGoogleSheet();
        }
        binding.swipe.setRefreshing(false);
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
            itemLoginBinding.btnclose.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();

                if(getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).pos.set(1);
                    ((MainActivity) getActivity()).closeDrawer();
                    ((MainActivity) getActivity()).loadFragment(new HomeFragment());
                    ((MainActivity) getActivity()).setTitleText("Home");
                }

            });

        }
    }

    private void gotoGoogleLogin() {
        binding.pd.setVisibility(View.VISIBLE);
//        startActivity(new Intent(getActivity(), LoginActivity.class));
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                    if(response.body() != null) {
                        saveUserToken(response.body().getData().getToken());
                    }
                    bottomSheetDialog.dismiss();
                    getUserFromLocal();

                    binding.scrollView.setVisibility(View.VISIBLE);


                }

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


    @Override
    public void onRefresh() {
        User user = sessionManager.getUser();
        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            showUserDetails(user.getData());
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getNotificationToken();
            binding.scrollView.setVisibility(View.GONE);
        }

    }
}