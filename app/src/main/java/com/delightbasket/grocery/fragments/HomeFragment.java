package com.delightbasket.grocery.fragments;

import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.activities.AllCategoriesActivity;
import com.delightbasket.grocery.activities.LoginActivity;
import com.delightbasket.grocery.adapters.AdapterWeight;
import com.delightbasket.grocery.adapters.CategoryAdapter;
import com.delightbasket.grocery.adapters.MainCategoryAdapter;
import com.delightbasket.grocery.adapters.SliderAdapterExample;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.dao.CartOffline;
import com.delightbasket.grocery.databinding.BottomsheetProductweightBinding;
import com.delightbasket.grocery.databinding.FragmentHome2Binding;
import com.delightbasket.grocery.databinding.ItemLoginBinding;
import com.delightbasket.grocery.databinding.ItemProductBinding;
import com.delightbasket.grocery.model.BannerRoot;
import com.delightbasket.grocery.model.Categories;
import com.delightbasket.grocery.model.MainCategory;
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
import com.smarteist.autoimageslider.SliderAnimations;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RetrofitService service;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1000;
    private static final String TAG = "homefrag";
    FragmentHome2Binding binding;
    CategoryAdapter categoryAdapter = new CategoryAdapter();
    MainCategoryAdapter mainCategoryAdapter = new MainCategoryAdapter();
    private SessionManager sessionManager;
    BottomSheetDialog bottomSheetDialog;
    private String userid = null;
    private int start = 0;
    private Categories dataList;
    private MainCategory mainCategoryList;
    private boolean isLoding = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home2, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getContext());
        service = RetrofitBuilder.create(getActivity());
        binding.shimmer.startShimmer();
        initView();
        getBannerData();
        initListener();
        viewAllCategory();
        binding.swipe.setOnRefreshListener(this);
    }

    private void viewAllCategory() {
        binding.tvViewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AllCategoriesActivity.class));
            }
        });
    }

    private void getBannerData() {
        Call<BannerRoot> call = service.getBanner(Const.DEV_KEY);
        call.enqueue(new Callback<BannerRoot>() {
            @Override
            public void onResponse(Call<BannerRoot> call, Response<BannerRoot> response) {
                if (response.code() == 200 && response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                    List<BannerRoot.Datum> banners = response.body().getData();
                    SliderAdapterExample sliderAdapterExample = new SliderAdapterExample(banners);
                    binding.imageSlider.setSliderAdapter(sliderAdapterExample);
                    binding.imageSlider.startAutoCycle();

                    binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                }
            }

            @Override
            public void onFailure(Call<BannerRoot> call, Throwable t) {
                Log.e("TAG", t.toString());
//ll
            }
        });
    }

    private void initListener() {
        categoryAdapter.setOnProductClickListener(new CategoryAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Categories.Product product, ItemProductBinding binding, String work, Categories.PriceUnit priceUnit, long quantity) {
                if (work.equals("WEIGHT")) {
                    openBottomSheetWeight(product, binding);
                } else if (work.equals("Google")) {
//                    showGoogleSheet();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else if (work.equals("addMore")) {

                    addToCart(product, priceUnit, binding, "plus", quantity);
                } else if (work.equals("lessOne")) {


                    addToCart(product, priceUnit, binding, "minus", quantity);

                }
            }

            @Override
            public void o() {
//ll
            }
        });
        binding.recyclearCatogery.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.recyclearCatogery.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.recyclearCatogery.getLayoutManager();
                    Log.d(TAG, "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d(TAG, "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d(TAG, "onScrollStateChanged:188 " + totalitem);
                    if (!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {
                        Log.d(TAG, "onScrollStateChanged: " + start);
                        start = start + Const.LIMIT;
                        binding.pd2.setVisibility(View.VISIBLE);
                        getData();
                    }
                }
            }
        });

    }

    private void openBottomSheetWeight(Categories.Product product, ItemProductBinding binding) {
        if (getActivity() != null) {
            bottomSheetDialog = new BottomSheetDialog(getActivity());
            BottomsheetProductweightBinding productweightBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottomsheet_productweight, null, false);
            bottomSheetDialog.setContentView(productweightBinding.getRoot());

            AdapterWeight adapterWeight = new AdapterWeight(product.getPriceUnit(), binding, bottomSheetDialog);
            productweightBinding.listProductWeight.setAdapter(adapterWeight);
            bottomSheetDialog.show();
            productweightBinding.tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());


        }
    }

    private void getData() {
        isLoding = true;

        Call<Categories> categoriesCall = service.getAllData(Const.DEV_KEY, userid, start, Const.LIMIT);
        categoriesCall.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {

                        dataList = response.body();
                        categoryAdapter.updateData(dataList.getData());
                        Log.d(TAG, "onScrolled: fetched " + dataList.getData().size());


                    }
                    isLoding = false;
                    binding.pd2.setVisibility(View.GONE);
                    binding.pBar.setVisibility(View.GONE);
                    binding.swipe.setRefreshing(false);
                    binding.shimmer.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@Nullable Call<Categories> call, @Nullable Throwable t) {
                //ll
                binding.pd2.setVisibility(View.GONE);
                binding.pBar.setVisibility(View.GONE);
                binding.swipe.setRefreshing(false);
                binding.shimmer.setVisibility(View.GONE);
            }
        });


    }

    private void getMainCategoryData() {
        isLoding = true;

        Call<MainCategory> categoriesCall = service.getCategoryList(Const.DEV_KEY, 0, 0);
        categoriesCall.enqueue(new Callback<MainCategory>() {
            @Override
            public void onResponse(Call<MainCategory> call, Response<MainCategory> response) {
                if (response.code() == 200) {
                    if (response.body().status == 200 && !response.body().data.data.isEmpty()) {
                        binding.layutCategory.setVisibility(View.VISIBLE);
                        mainCategoryList = response.body();
                        mainCategoryAdapter.updateData(mainCategoryList.data.data);
//                        Log.d(TAG, "onScrolled: fetched " + mainCategoryList.data.size());


                    }
                    isLoding = false;
                    binding.pd2.setVisibility(View.GONE);
                    binding.pBar.setVisibility(View.GONE);
                    binding.swipe.setRefreshing(false);
                    binding.shimmer.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@Nullable Call<MainCategory> call, @Nullable Throwable t) {
                //ll
                binding.layutCategory.setVisibility(View.GONE);
                binding.pd2.setVisibility(View.GONE);
                binding.pBar.setVisibility(View.GONE);
                binding.swipe.setRefreshing(false);
                binding.shimmer.setVisibility(View.GONE);
            }
        });


    }


    private void initView() {
        service = RetrofitBuilder.create(getActivity());
        categoryAdapter = new CategoryAdapter();
        mainCategoryAdapter = new MainCategoryAdapter();
        binding.recyclearCatogery.setAdapter(categoryAdapter);
        binding.rvMainCategory.setAdapter(mainCategoryAdapter);
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userid = sessionManager.getUser().getData().getUserId();
        }
        binding.pBar.setVisibility(View.GONE);
        getData();
        getMainCategoryData();
    }

    private void addToCart(Categories.Product model, Categories.PriceUnit priceUnit, ItemProductBinding binding1, String work, long oldQuantity) {
        AppDatabase db = Room.databaseBuilder(getContext(),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();

        String pid = model.getProductId();
        String name = model.getProductName();
        String imageurl = model.getProductImage().get(0);
        String priceunitid = priceUnit.getPriceUnitId();
        String price = priceUnit.getPrice();
        String unit = priceUnit.getUnit();
        Log.d(TAG, "addToCart:old " + oldQuantity);
        if (work.equals("plus")) {
            if (oldQuantity == 0) {
                long quantity = oldQuantity + 1;
                CartOffline cartOffline = new CartOffline(pid, name, imageurl, priceunitid, quantity, price, unit);
                db.cartDao().insertNew(cartOffline);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                binding1.lytAddMore.setVisibility(View.VISIBLE);
                binding1.tvProductadd.setVisibility(View.GONE);
                Log.d(TAG, "addToCart: added " + oldQuantity);
            } else {
                Log.d(TAG, "addToCart: update");
                long quantity = oldQuantity + 1;
                db.cartDao().updateObj(quantity, priceunitid);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                Log.d(TAG, "addToCart: updated" + oldQuantity);
            }
        } else if (work.equals("minus")) {
            Log.d(TAG, "addToCart: update");
            if (oldQuantity >= 0) {
                long quantity = oldQuantity - 1;
                db.cartDao().updateObj(quantity, priceunitid);
                db.close();
                getActivity().runOnUiThread(() -> {
                    binding1.tvQuantity.setText(String.valueOf(quantity));
                    if (quantity == 0) {
                        db.cartDao().deleteObjbyPid(priceunitid);
                        Log.d(TAG, "addToCart:deleted  ");
                        binding1.lytAddMore.setVisibility(View.GONE);
                        binding1.tvProductadd.setVisibility(View.VISIBLE);
                    }
                });
            }
            Log.d(TAG, "addToCart: updated" + oldQuantity);
        }

    }

    private void showGoogleSheet() {
        if (getActivity() != null) {
            bottomSheetDialog = new BottomSheetDialog(getActivity());
            bottomSheetDialog.setOnShowListener((DialogInterface.OnShowListener) dialog -> {

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
                if (getActivity() != null) {
                    mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

                }
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            binding.pBar.setVisibility(View.GONE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
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
        String notificationToken = sessionManager.getStringValue(Const.NOTIFICATION_TOKEN);
        Call<User> call = service.registerUser(Const.DEV_KEY,
                account.getDisplayName(), account.getDisplayName(), account.getEmail(),
                "gmail", account.getEmail(), "1", notificationToken, String.valueOf(account.getPhotoUrl())
        );
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@Nullable Call<User> call, @Nullable Response<User> response) {
                if (response != null) {
                    sessionManager.saveUser(response.body());
                    sessionManager.saveBooleanValue(Const.IS_LOGIN, true);
                    if (response.body() != null) {
                        sessionManager.saveStringValue(Const.USER_TOKEN, response.body().getData().getToken());
                    }
                    Log.d(TAG, "onResponse: uuuuu" + response.body().getData().getId());
                    bottomSheetDialog.dismiss();
                }
                service = RetrofitBuilder.create(getActivity());
                binding.shimmer.startShimmer();
                initView();
                getBannerData();
                initListener();
            }

            @Override
            public void onFailure(@Nullable Call<User> call, @Nullable Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }


    @Override
    public void onRefresh() {
        start = 0;
        initView();
        initListener();
        getBannerData();

        binding.pBar.setVisibility(View.GONE);
    }
}