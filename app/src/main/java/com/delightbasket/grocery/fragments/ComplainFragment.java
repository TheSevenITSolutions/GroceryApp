package com.delightbasket.grocery.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.activities.LoginActivity;
import com.delightbasket.grocery.adapters.ComplaintAdapter;
import com.delightbasket.grocery.databinding.BottomsheetHaveAnIssueBinding;
import com.delightbasket.grocery.databinding.FragmentComplainBinding;
import com.delightbasket.grocery.databinding.ItemLoginBinding;
import com.delightbasket.grocery.model.ComplainRoot;
import com.delightbasket.grocery.model.RestResponse;
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


public class ComplainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "complainfrag";
    FragmentComplainBinding binding;
    SessionManager sessionManager;
    private String token;
    private ComplaintAdapter complaintAdapter;
    private int start = 0;
    private boolean isLoding = false;
    private static final int RC_SIGN_IN = 100;
    String notificationToken;
    BottomSheetDialog bottomSheetDialog;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_complain, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getContext());
        binding.shimmer.startShimmer();
        initView();
        initListnear();
        binding.swipe.setOnRefreshListener(this);
    }

    private void getData() {
        isLoding = true;

        binding.lyt404.setVisibility(View.GONE);

        RetrofitService service = RetrofitBuilder.create(getActivity());
        Call<ComplainRoot> call = service.getComplainData(Const.DEV_KEY, token, Const.LIMIT, start);
        call.enqueue(new Callback<ComplainRoot>() {
            @Override
            public void onResponse(Call<ComplainRoot> call, Response<ComplainRoot> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                        complaintAdapter.addData(response.body().getData());
                        binding.rvComplain.setAdapter(complaintAdapter);
                        isLoding = false;
                    } else if(start == 0 && response.body().getStatus() == 400) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                    binding.pBar.setVisibility(View.GONE);
                    binding.swipe.setRefreshing(false);
                    binding.pd2.setVisibility(View.GONE);
                    binding.shimmer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ComplainRoot> call, Throwable t) {
                binding.shimmer.setVisibility(View.GONE);
            }
        });
    }

    private void initView() {
        start = 0;
        complaintAdapter = new ComplaintAdapter();

        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            binding.pBar.setVisibility(View.GONE);
            getData();
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    private void googleLogin() {
        notificationToken = sessionManager.getStringValue(Const.NOTIFICATION_TOKEN);

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
        binding.pBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            binding.pBar.setVisibility(View.GONE);
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
        Call<User> call = RetrofitBuilder.create(getActivity()).registerUser(Const.DEV_KEY,
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
                }
                sessionManager = new SessionManager(getContext());
                binding.shimmer.startShimmer();
                initView();
                initListnear();
                binding.swipe.setOnRefreshListener(ComplainFragment.this);
            }

            @Override
            public void onFailure(@Nullable Call<User> call, @Nullable Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }


    private void openBottomSheet() {

        bottomSheetDialog = new BottomSheetDialog(getActivity());
        BottomsheetHaveAnIssueBinding bottomsheetHaveAnIssueBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottomsheet_have_an_issue, null, false);
        bottomSheetDialog.setContentView(bottomsheetHaveAnIssueBinding.getRoot());

        bottomSheetDialog.show();

        bottomsheetHaveAnIssueBinding.btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomsheetHaveAnIssueBinding.lytBottom.setOnClickListener(view -> sendIssue(bottomsheetHaveAnIssueBinding));
    }

    private void initListnear() {
        binding.lytBottom.setOnClickListener(v -> openBottomSheet());


        binding.rvComplain.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!binding.rvComplain.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvComplain.getLayoutManager();
                    Log.d(TAG, "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d(TAG, "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d(TAG, "onScrollStateChanged:188 " + totalitem);
                    if(!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {
                        Log.d(TAG, "onScrollStateChanged: " + start);
                        start = start + Const.LIMIT;
                        binding.pd2.setVisibility(View.VISIBLE);
                        getData();
                    }
                }

            }
        });

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
        Call<RestResponse> call = RetrofitBuilder.create(getActivity()).raiseComplain(Const.DEV_KEY, token, "", title, mobile, des);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if(response.code() == 200 && response.body() != null) {
                    if(response.body().getStatus() == 200) {
                        getData();
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
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


    @Override
    public void onRefresh() {
        initView();

        binding.lyt404.setVisibility(View.GONE);
    }
}