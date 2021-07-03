package com.delightbasket.grocery.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.activities.DeliveryOptionsActivity;
import com.delightbasket.grocery.activities.LoginActivity;
import com.delightbasket.grocery.adapters.CartAdapter;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.dao.CartOffline;
import com.delightbasket.grocery.databinding.FragmentCartBinding;
import com.delightbasket.grocery.databinding.ItemCartBinding;
import com.delightbasket.grocery.databinding.ItemLoginBinding;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "cartfrag";
    private static final int RC_SIGN_IN = 100;
    FragmentCartBinding binding;
    RetrofitService service;
    SessionManager sessionManager;
    CartAdapter cartAdapter = new CartAdapter();
    private BottomSheetDialog bottomSheetDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private String token;
    private long totalPrice = 0;
    List<String> pids = new ArrayList<>();
    int i = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);

        return binding.getRoot();
    }

    List<String> unitids = new ArrayList<>();
    List<String> quantities = new ArrayList<>();
    private List<CartOffline> list = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        service = RetrofitBuilder.create(getActivity());

        sessionManager = new SessionManager(getContext());
        Log.d(TAG, "onActivityCreated: " + sessionManager.getBooleanValue(Const.IS_LOGIN));


        binding.rvCart.setAdapter(cartAdapter);
        getData();
        binding.swipe.setOnRefreshListener(this);


        initListnear();
    }

    private void initListnear() {
        cartAdapter.setOnCartItemClick((product, work, binding1, position) -> {
            if(work.equals("Delete")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Are you Delete this Product ?");
                builder.setPositiveButton("Yes", (dialog, which) -> deleteProduct(product, position));
                builder.setNegativeButton("No", (dialog, which) -> builder.create().dismiss());
                builder.create().show();

            } else if(work.equals("Addmore")) {


                addMoreToCart(product, "add", binding1);
            } else if(work.equals("moveToWishlist")) {
                if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                    token = sessionManager.getUser().getData().getToken();
                    moveToWishlist(product, position);

                } else {
                   startActivity(new Intent(getActivity(), LoginActivity.class));
                }

            } else if(work.equals("LessOne")) {
                if(getCartdata(product.getPriceUnitId()) == 1) {
                    Toast.makeText(getActivity(), "You reach minimum limit", Toast.LENGTH_SHORT).show();
                    return;
                }
                addMoreToCart(product, "less", binding1);
            }
        });

        binding.tvCheckOut.setOnClickListener(v -> {
            if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                token = sessionManager.getUser().getData().getToken();
                i = 0;
                if(list.isEmpty()) {
                    Toast.makeText(getContext(), "No Cart Data Found", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getActivity(), DeliveryOptionsActivity.class));

                }

            } else {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }


    private void moveToWishlist(CartOffline product, int position) {
        binding.pBar.setVisibility(View.VISIBLE);
        Call<RestResponse> call = service.addProductToWishlist(Const.DEV_KEY, token, product.getPid());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if(response.body().getStatus() == 200) {

                    Toast.makeText(getActivity(), "Add to Wishlist SuccessFully", Toast.LENGTH_SHORT).show();
                    deleteProduct(product, position);

                } else if(response.body().getStatus() == 401) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    deleteProduct(product, position);
                }
                binding.pBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                binding.pBar.setVisibility(View.GONE);
            }
        });
    }

    private void gotoLogin() {
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
            itemLoginBinding.googlelogin.setOnClickListener(view -> {
                binding.pBar.setVisibility(View.VISIBLE);

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                if(getActivity() != null) {
                    mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                }
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });
            itemLoginBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        }
    }

    private long getCartdata(String id) {
        AppDatabase db = Room.databaseBuilder(Objects.requireNonNull(getActivity()),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();
        Log.d("qqqq1", "getCartdata: " + id);
        if(!db.cartDao().getCartProduct(id).isEmpty()) {
            return db.cartDao().getCartProduct(id).get(0).getQuantity();
        } else {
            return 0;
        }


    }

    private void addMoreToCart(CartOffline product, String work, ItemCartBinding binding1) {
        AppDatabase db = Room.databaseBuilder(getContext(),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();

        binding.pBar.setVisibility(View.VISIBLE);
        if(work.equals("add")) {

            long quantity = getCartdata(product.getPriceUnitId()) + 1;
            db.cartDao().updateObj(quantity, product.getPriceUnitId());
            db.close();
            binding1.tvQuantity.setText(String.valueOf(quantity));
            Log.d(TAG, "addToCart: updated in cart" + quantity);
            totalPrice = totalPrice + Long.parseLong(product.getPrice());
            binding.tvTotalPrice.setText(getString(R.string.currency) + String.valueOf(totalPrice));
        } else if(work.equals("less")) {
            long quantity = getCartdata(product.getPriceUnitId()) - 1;
            Log.d(TAG, "addMoreToCart: " + getCartdata(product.getPriceUnitId()));
            if(quantity == 0) {
                Toast.makeText(getActivity(), "You reach minimum limit", Toast.LENGTH_SHORT).show();
                binding.pBar.setVisibility(View.GONE);
                return;
            }
            db.cartDao().updateObj(quantity, product.getPriceUnitId());
            db.close();
            binding1.tvQuantity.setText(String.valueOf(quantity));
            Log.d(TAG, "addToCart: updated in cart" + quantity);
            totalPrice = totalPrice - Long.parseLong(product.getPrice());
            binding.tvTotalPrice.setText(getString(R.string.currency) + String.valueOf(totalPrice));
        }
        binding.pBar.setVisibility(View.GONE);



    }

    private void deleteProduct(CartOffline product, int position) {


        AppDatabase db = Room.databaseBuilder(getContext(),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();
        db.cartDao().deleteObjbyPid(product.getPriceUnitId());
        Log.d(TAG, "deleteProduct: " + product.getName());
        cartAdapter.removeItem(position);
        list.remove(product);
        getTotalAmount(list);
    }

    private void getData() {
        binding.lyt404.setVisibility(View.GONE);
        AppDatabase db = Room.databaseBuilder(getContext(),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();

        list.clear();
        list = db.cartDao().getall();
        cartAdapter.addData(list);

        binding.pBar.setVisibility(View.GONE);
        binding.swipe.setRefreshing(false);
        getTotalAmount(list);
        if(list.isEmpty()) {
            binding.lyt404.setVisibility(View.VISIBLE);
        } else {
            binding.lyt404.setVisibility(View.GONE);

        }




    }

    private void getTotalAmount(List<CartOffline> list) {
        totalPrice = 0;
        for(int j = 0; j <= list.size() - 1; j++) {
            CartOffline product = list.get(j);
            long price = Long.parseLong(product.getPrice()) * product.getQuantity();
            Log.d(TAG, "getTotalAmount: " + product.getPrice() + " * " + product.getQuantity());
            totalPrice = totalPrice + price;
        }
        binding.tvTotalPrice.setText(getString(R.string.currency) + String.valueOf(totalPrice));
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
                    bottomSheetDialog.dismiss();


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
        String notificationToken = sessionManager.getStringValue(Const.NOTIFICATION_TOKEN);
        Call<User> call = service.registerUser(Const.DEV_KEY,
                account.getDisplayName(), account.getDisplayName(), account.getEmail(),
                "gmail", account.getEmail(), "1", notificationToken, String.valueOf(account.getPhotoUrl())
        );
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@Nullable Call<User> call, @Nullable Response<User> response) {

                if(response != null) {
                    sessionManager.saveUser(response.body());
                    sessionManager.saveBooleanValue(Const.IS_LOGIN, true);
                    if(response.body() != null) {
                        sessionManager.saveStringValue(Const.USER_TOKEN, response.body().getData().getToken());
                    }
                    bottomSheetDialog.dismiss();
                }
                Log.d(TAG, "onActivityCreated: " + sessionManager.getBooleanValue(Const.IS_LOGIN));


                binding.swipe.setOnRefreshListener(CartFragment.this);


                initListnear();
            }

            @Override
            public void onFailure(@Nullable Call<User> call, @Nullable Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

    @Override
    public void onRefresh() {
        cartAdapter = new CartAdapter();
        getData();

    }
}