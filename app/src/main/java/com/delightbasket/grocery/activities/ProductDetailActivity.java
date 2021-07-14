package com.delightbasket.grocery.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.ProductImageViewPager;
import com.delightbasket.grocery.adapters.ProductPriceAdapter;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.dao.CartOffline;
import com.delightbasket.grocery.databinding.ActivityProductDetailBinding;
import com.delightbasket.grocery.databinding.ItemLoginBinding;
import com.delightbasket.grocery.databinding.ItemProductPriceBinding;
import com.delightbasket.grocery.fragments.ImageFragment;
import com.delightbasket.grocery.model.ProductMain;
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
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity implements ProductPriceAdapter.OnPriceAdapterClickListnear, SwipeRefreshLayout.OnRefreshListener {
    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "productdetailact";
    ActivityProductDetailBinding binding;


    SessionManager sessionManager;
    TabLayout tableLayout;
    BottomSheetDialog bottomSheetDialog;
    GoogleSignInClient mGoogleSignInClient;
    RetrofitService service;
    String pid;
    String userId = null;
    ProductMain.Datum product;
    private boolean isStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);
        service = RetrofitBuilder.create(this);
        sessionManager = new SessionManager(this);
        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userId = sessionManager.getUser().getData().getUserId();
        }
        getIntentData();
        binding.pd.setVisibility(View.GONE);

        binding.swipe.setOnRefreshListener(this);

    }

    private void getIntentData() {
        binding.shimmer.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        binding.shimmer.startShimmer();
        binding.lytmain.setVisibility(View.GONE);
        pid = intent.getStringExtra(Const.ProductID);
        Log.d(TAG, "getIntentData: " + pid);
        Log.d(TAG, "getIntentData:u " + userId);
        getData(pid);


    }

    private void getData(String pid) {
        Call<ProductMain> call = service.getOneProduct(Const.DEV_KEY, userId, pid);
        call.enqueue(new Callback<ProductMain>() {
            @Override
            public void onResponse(@NonNull Call<ProductMain> call, @NonNull Response<ProductMain> response) {
                ProductMain data = response.body();
                if(data != null && data.getStatus() == 200) {
                    product = data.getData().get(0);

                    initview();
                    setData();
                    if(product.getStockQuantity().equals("Out of Stock")) {
                        isStock = false;
                    } else {
                        isStock = true;
                    }
                    setPriceAdapter();
                    initListnear();
                    binding.pd.setVisibility(View.GONE);
                }
                binding.shimmer.setVisibility(View.GONE);
                binding.lytmain.setVisibility(View.VISIBLE);
                binding.swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ProductMain> call, Throwable t) {
                Log.d(TAG, TAG + t.toString());
                binding.pd.setVisibility(View.GONE);
                binding.swipe.setRefreshing(false);
                binding.shimmer.setVisibility(View.GONE);
            }
        });
    }

    private void initListnear() {


        binding.lytAddwishlist.setOnClickListener(v -> {
            boolean b = sessionManager.getBooleanValue(Const.IS_LOGIN);
            if(b) {
                if(product.getIsWishlist() == 1) {
                    removeFromWishlist();
                } else {
                    addToWishlist();
                }
            } else {
                startActivity(new Intent(this, LoginActivity.class));
//                showGoogleSheet();
            }
        });
        binding.lytGotoCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("work", "gotocart");
            startActivity(intent);
            finish();
        });
    }

    private void addToWishlist() {
        String token = sessionManager.getUser().getData().getToken();

        Call<RestResponse> call = service.addProductToWishlist(Const.DEV_KEY, token, product.getProductId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(@NonNull Call<RestResponse> call, @NonNull Response<RestResponse> response) {
                if(response.body() != null && response.body().getStatus() == 200) {
                    binding.tvAddremoveToWishlist.setText(R.string.move_from_wish_list);
                    binding.imgwishlist.setImageDrawable(ContextCompat.getDrawable(ProductDetailActivity.this, R.drawable.heartfill));
                    product.setIsWishlist(1L);
                    binding.pd.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RestResponse> call, @NonNull Throwable t) {
                binding.pd.setVisibility(View.GONE);
            }
        });
    }

    private void removeFromWishlist() {
        String token = sessionManager.getUser().getData().getToken();
        Call<RestResponse> call = service.removeFromWishlist(Const.DEV_KEY, token, product.getProductId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(@NonNull Call<RestResponse> call, @NonNull Response<RestResponse> response) {
                if(response.body() != null && response.body().getStatus() == 200) {
                    binding.tvAddremoveToWishlist.setText("Add to Wishlist");
                    binding.imgwishlist.setImageDrawable(ContextCompat.getDrawable(ProductDetailActivity.this, R.drawable.heart));

                    product.setIsWishlist(0L);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RestResponse> call, @NonNull Throwable t) {
                Log.d("productdetail 68", TAG + t);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private void setData() {

        if (!product.getProductName().isEmpty() && !product.getProductName().equals("")) {
            binding.tvProductName.setText(product.getProductName());
        }

        if (!product.getDescription().isEmpty() && !product.getDescription().equals("")) {
            binding.tvProductBenefits.setText(Html.fromHtml(product.getDescription()));
        } else {
            binding.tvReadmore.setVisibility(View.GONE);
        }
        if (!product.getPriceUnit().get(0).getPrice().isEmpty() && !product.getPriceUnit().get(0).getPrice().equals("")) {
            binding.tvProductprice.setText(getString(R.string.currency).concat(product.getPriceUnit().get(0).getPrice()));
        }
        if (product.getIsWishlist() == 1) {
            binding.tvAddremoveToWishlist.setText("Remove From \n Wishlist");
            binding.imgwishlist.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.heartfill));
        } else {
            binding.tvAddremoveToWishlist.setText("Add to Wishlist");
            binding.imgwishlist.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.heart));
        }
        int amount = 0;
        float discountPrice = 0;
        long finalPrice = 0;
        for (int i = 0; i < product.getPriceUnit().size(); i++) {
            amount = amount + Integer.parseInt(product.getPriceUnit().get(i).getPrice());
            discountPrice = (discountPrice + product.getPriceUnit().get(i).getDiscountPrice());
        }
        int new_Price = 0;
        String price_str = "";
        finalPrice = (long) (100 - ((discountPrice * 100) / amount));
        Math.toIntExact(finalPrice);
        price_str = String.valueOf(finalPrice);
        new_Price = Integer.parseInt(price_str);

        binding.tvPer.setText(new_Price + "%");

    }

    private void setPriceAdapter() {
        ProductPriceAdapter productPriceAdapter = new ProductPriceAdapter(product.getPriceUnit(), binding, this, isStock);
        binding.rvProductOption.setAdapter(productPriceAdapter);
    }

    private void initview() {
        ProductImageViewPager productImageadapter = new ProductImageViewPager(product.getProductImage(), () -> getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ImageFragment(product)).addToBackStack("fragimg").commit());
        binding.imageSlider.setSliderAdapter(productImageadapter);


    }

    private void showGoogleSheet() {
        bottomSheetDialog = new BottomSheetDialog(this);
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

        ItemLoginBinding itemLoginBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.item_login, null, false);
        bottomSheetDialog.setContentView(itemLoginBinding.getRoot());
        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);
        itemLoginBinding.googlelogin.setOnClickListener(view -> {


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            binding.pd.setVisibility(View.VISIBLE);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
        itemLoginBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());

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
                service = RetrofitBuilder.create(ProductDetailActivity.this);
                sessionManager = new SessionManager(ProductDetailActivity.this);
                if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                    userId = sessionManager.getUser().getData().getUserId();
                }
                getIntentData();
                binding.pd.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(@Nullable Call<User> call, @Nullable Throwable t) {
                Log.d(TAG, TAG + t);
            }
        });
    }


    @Override
    public void onPriceAdapterClick(ProductMain.PriceUnit unit, ItemProductPriceBinding binding, @NonNull String work) {

        if(work.equals("addOne")) {

            binding.lytPlusMinus.setVisibility(View.VISIBLE);
            binding.tvAdd.setVisibility(View.GONE);
            addtoCart(unit, binding, "plus");
        } else if(work.equals("lessOne")) {

            addtoCart(unit, binding, "minus");

        }
    }

    private void addtoCart(ProductMain.PriceUnit unit, ItemProductPriceBinding binding1, String work) {
        long oldquantity = getCartdata(unit.getPriceUnitId());
        //  unit.getIsCart()
        binding.pd.setVisibility(View.VISIBLE);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();

        String productId = product.getProductId();
        String name = product.getProductName();
        String imageurl = product.getProductImage().get(0);
        String priceunitid = unit.getPriceUnitId();
        String price = unit.getPrice();
        String munit = unit.getUnit();

        if(work.equals("plus")) {
            if(oldquantity == 0) {
                long quantity = oldquantity + 1;
                CartOffline cartOffline = new CartOffline(productId, name, imageurl, priceunitid, quantity, price, munit);
                db.cartDao().insertNew(cartOffline);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                binding1.lytPlusMinus.setVisibility(View.VISIBLE);
                binding1.tvAdd.setVisibility(View.GONE);
                Log.d(TAG, "addToCart: added " + oldquantity);
            } else {
                Log.d(TAG, "addToCart: update");
                long quantity = oldquantity + 1;
                db.cartDao().updateObj(quantity, priceunitid);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                Log.d(TAG, "addToCart: updated" + oldquantity);
            }
        } else if(work.equals("minus")) {
            Log.d(TAG, "addToCart: update");
            if(oldquantity >= 0) {
                long quantity = oldquantity - 1;
                db.cartDao().updateObj(quantity, priceunitid);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                if(quantity == 0) {
                    db.cartDao().deleteObjbyPid(priceunitid);
                    binding1.lytPlusMinus.setVisibility(View.GONE);
                    binding1.tvAdd.setVisibility(View.VISIBLE);
                }
            }
            Log.d(TAG, "addToCart: updated" + oldquantity);
        }
        binding.pd.setVisibility(View.GONE);

    }

    private long getCartdata(String id) {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();
        Log.d("qqqq1", "getCartdata: " + id);
        if(!db.cartDao().getCartProduct(id).isEmpty()) {
            return db.cartDao().getCartProduct(id).get(0).getQuantity();
        } else {
            return 0;
        }


    }

    @Override
    public void onRefresh() {
        getData(pid);

    }

    public void onClickBack(View view) {
        super.onBackPressed();
    }

    public void onClickReadMore(View view) {
        if(binding.tvReadmore.getText().toString().equals("Read more...")) {
            binding.tvReadmore.setText("Read less...");
            binding.tvProductBenefits.setMaxLines(20);
        } else {
            binding.tvReadmore.setText("Read more...");
            binding.tvProductBenefits.setMaxLines(3);
        }
    }


}