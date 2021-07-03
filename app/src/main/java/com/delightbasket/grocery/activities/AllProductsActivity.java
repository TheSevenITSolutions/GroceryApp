package com.delightbasket.grocery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.CategoryProductAdapter;
import com.delightbasket.grocery.adapters.SortAdapter;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.dao.CartOffline;
import com.delightbasket.grocery.databinding.ActivityAllProductsBinding;
import com.delightbasket.grocery.databinding.BottomsheetSortingBinding;
import com.delightbasket.grocery.databinding.ItemCategoryProductBinding;
import com.delightbasket.grocery.databinding.ItemSearchProductBinding;
import com.delightbasket.grocery.model.CategoryProduct;
import com.delightbasket.grocery.model.SearchCatProduct;
import com.delightbasket.grocery.model.SortRoot;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllProductsActivity extends AppCompatActivity implements CategoryProductAdapter.OnCatAllProductClickListnear, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String TAG = "allproductact";
    ActivityAllProductsBinding binding;
    RetrofitService service;
    private String cid;
    CategoryProductAdapter categoryProductAdapter;
    String userId = null;
    private BottomsheetSortingBinding sortingBinding;
    int start = 0;
    private SortAdapter sortAdapter;
    private int sortType = 3;
    private boolean isLoding = false;
    private String keywords = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_products);
        service = RetrofitBuilder.create(this);
        binding.shimmer.startShimmer();
//        checkUser();
        getIntentData();
        initview();

        initSearchListnear();
        initListnear();
        binding.swipe.setOnRefreshListener(this);
    }

    private void initListnear() {
        binding.rvProductlist.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!binding.rvProductlist.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvProductlist.getLayoutManager();

                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();


                    if(!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {
                        Log.d(TAG, "onScrollStateChanged: " + start);
                        start = start + Const.LIMIT;
                        binding.pd2.setVisibility(View.VISIBLE);
                        getsortData(false);
                    }
                }
            }
        });
    }

    private void checkUser() {
        SessionManager sessionManager = new SessionManager(this);
        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userId = sessionManager.getUser().getData().getUserId();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        cid = intent.getStringExtra("cid");
        String cname = intent.getStringExtra("cname");
        binding.tvCategoryname.setText(cname);
    }

    private void initSearchListnear() {

        binding.etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //  ll
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sortAdapter = new SortAdapter();
                binding.rvProductlist.setAdapter(sortAdapter);
                AllProductsActivity.this.start = 0;
                keywords = s.toString();

                if(!binding.swipe.isRefreshing()) {
                    binding.pd.setVisibility(View.VISIBLE);
                }
                binding.lyt404.setVisibility(View.GONE);
                getsortData(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });
        sortAdapter.setOnSortItemClick((datum, binding1, work, defultPriceUnit) -> {
            Log.d(TAG, "initListnear: " + defultPriceUnit.getPriceUnitId());
            if(work.equals("add")) {

                binding1.lytPlusMinus.setVisibility(View.VISIBLE);
                binding1.tvAdd.setVisibility(View.GONE);
                Log.d(TAG, "initListnear: prr " + defultPriceUnit.getPriceUnitId());
                addtoCartSort(datum, "add", defultPriceUnit, binding1);
            } else if(work.equals("less")) {

                addtoCartSort(datum, "less", defultPriceUnit, binding1);

            }
        });

    }


    private void initview() {
        categoryProductAdapter = new CategoryProductAdapter(this);

        sortAdapter = new SortAdapter();
        binding.rvProductlist.setAdapter(sortAdapter);
        start = 0;

        if(!binding.swipe.isRefreshing()) {
            binding.pd.setVisibility(View.GONE);
        }
        binding.lyt404.setVisibility(View.GONE);
        getsortData(false);
    }

    public void onClickSort(View view) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        sortingBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheet_sorting, null, false);
        bottomSheetDialog.setContentView(sortingBinding.getRoot());
        bottomSheetDialog.show();
        sortingBinding.imgClose.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

        sortingBinding.rb1.setOnClickListener(this);
        sortingBinding.rb2.setOnClickListener(this);
        sortingBinding.rb3.setOnClickListener(this);

        setOldSortType();
        sortingBinding.imgClose.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
        sortingBinding.tvApply.setOnClickListener(v -> {
            sortAdapter = new SortAdapter();
            binding.rvProductlist.setAdapter(sortAdapter);

            AllProductsActivity.this.start = 0;

            if(!binding.swipe.isRefreshing()) {
                binding.pd.setVisibility(View.VISIBLE);
            }
            binding.lyt404.setVisibility(View.GONE);
            getsortData(false);
            bottomSheetDialog.dismiss();
        });

    }

    private void setOldSortType() {
        switch(sortType) {
            case 1:
                sortingBinding.rb1.setChecked(true);
                break;
            case 2:
                sortingBinding.rb2.setChecked(true);
                break;
            case 3:
                sortingBinding.rb3.setChecked(true);
                break;


            default:

        }
    }

    private void getsortData(boolean search) {
        
//        checkUser();

        isLoding = true;
        Log.d(TAG, "getsortData: sort  type=" + sortType + "  start=" + start + "  keyword=" + keywords);


        Call<SortRoot> call = service.getSortCategory(Const.DEV_KEY, String.valueOf(sortType), Const.LIMIT, start, userId, keywords, cid);
        call.enqueue(new Callback<SortRoot>() {
            @Override
            public void onResponse(Call<SortRoot> call, Response<SortRoot> response) {

                if(response.code() == 200) {
                    if(response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {

                        if(search) {
                            sortAdapter = new SortAdapter();
                            binding.rvProductlist.setAdapter(sortAdapter);
                        }
                        sortAdapter.addData(response.body().getData());

                    } else if(start == 0 && response.body().getStatus() == 401) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }
                initSearchListnear();
                binding.pd2.setVisibility(View.GONE);
                binding.pd.setVisibility(View.GONE);
                binding.swipe.setRefreshing(false);
                isLoding = false;
                binding.shimmer.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<SortRoot> call, Throwable t) {
//ll
            }
        });


    }

    @Override
    public void onCatProductClick(CategoryProduct.Product product1, SearchCatProduct.Datum product2, int i, String work, ItemCategoryProductBinding binding1) {
        if(i == 1) {

            if(work.equals("Add")) {
                CategoryProduct.PriceUnit priceUnit = getPriceUnit(binding1.tvProductweight.getText().toString(), product1);
                addToCart1(product1, priceUnit, binding1, "add");
            }
        } else if(i == 2) {
            SearchCatProduct.PriceUnit priceUnit = getPriceUnit(binding1.tvProductweight.getText().toString(), product2);
            addToCart1(product2, priceUnit, binding1, "add");
            // minus nu baki 6
        }
    }

    private void addToCart1(CategoryProduct.Product unit, CategoryProduct.PriceUnit priceUnit, ItemCategoryProductBinding binding1, String work) {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();

        binding.pd.setVisibility(View.VISIBLE);
        String pid = unit.getProductId();
        String name = unit.getProductName();
        String imageurl = unit.getProductImage().get(0);
        String priceunitid = priceUnit.getPriceUnitId();
        String price = priceUnit.getPrice();
        String munit = priceUnit.getUnit();
        long quantity = getCartdata(priceunitid);
        if(work.equals("add")) {
            if(quantity == 0) {
                quantity++;
                CartOffline cartOffline = new CartOffline(pid, name, imageurl, priceunitid, quantity, price, munit);
                db.cartDao().insertNew(cartOffline);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                binding1.lytPlusMinus.setVisibility(View.VISIBLE);
                binding1.tvAdd.setVisibility(View.GONE);
                Log.d(TAG, TAG + quantity);
            } else {
                Log.d(TAG, TAG);
                quantity++;
                db.cartDao().updateObj(quantity, priceunitid);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                Log.d(TAG, TAG + quantity);
            }
        }
        binding.pd.setVisibility(View.GONE);
    }

    private void addToCart1(SearchCatProduct.Datum unit, SearchCatProduct.PriceUnit priceUnit, ItemCategoryProductBinding binding1, String work) {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();

        binding.pd.setVisibility(View.VISIBLE);
        String pid = unit.getProductId();
        String name = unit.getProductName();
        String imageurl = unit.getProductImage().get(0);
        String priceunitid = priceUnit.getPriceUnitId();
        String price = priceUnit.getPrice();
        String munit = priceUnit.getUnit();
        long quantity = getCartdata(priceunitid);
        if(work.equals("add")) {
            if(quantity == 0) {
                quantity++;
                CartOffline cartOffline = new CartOffline(pid, name, imageurl, priceunitid, quantity, price, munit);
                db.cartDao().insertNew(cartOffline);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                binding1.lytPlusMinus.setVisibility(View.VISIBLE);
                binding1.tvAdd.setVisibility(View.GONE);
                Log.d(TAG, TAG + quantity);
            } else {
                Log.d(TAG, TAG);
                quantity++;
                db.cartDao().updateObj(quantity, priceunitid);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                Log.d(TAG, TAG + quantity);
            }
        }
        binding.pd.setVisibility(View.GONE);
    }

    private CategoryProduct.PriceUnit getPriceUnit(String toString, CategoryProduct.Product model) {
        for(int i = 0; i <= model.getPriceUnit().size() - 1; i++) {

            if(model.getPriceUnit().get(i).getUnit().equals(toString)) {

                return model.getPriceUnit().get(i);
            }
        }
        return null;
    }

    private SearchCatProduct.PriceUnit getPriceUnit(String toString, SearchCatProduct.Datum model) {
        for(int i = 0; i <= model.getPriceUnit().size() - 1; i++) {

            if(model.getPriceUnit().get(i).getUnit().equals(toString)) {


                return model.getPriceUnit().get(i);
            }
        }
        return null;
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
        initview();

        binding.lyt404.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        removeAllrbCheck();
        switch(v.getId()) {
            case R.id.rb1:
                sortingBinding.rb1.setChecked(true);
                sortType = 1;
                break;
            case R.id.rb2:
                sortingBinding.rb2.setChecked(true);
                sortType = 2;
                break;
            case R.id.rb3:
                sortingBinding.rb3.setChecked(true);
                sortType = 3;
                break;


            default:

        }
    }

    private void removeAllrbCheck() {
        sortingBinding.rb1.setChecked(false);
        sortingBinding.rb2.setChecked(false);
        sortingBinding.rb3.setChecked(false);

    }

    private void addtoCartSort(SortRoot.DataItem unit, String work, SortRoot.PriceUnitItem priceUnit, ItemSearchProductBinding binding1) {
        //  unit.getIsCart()
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();

        binding.pd.setVisibility(View.VISIBLE);
        String pid = unit.getProductId();
        String name = unit.getProductName();
        String imageurl = unit.getProductImage().get(0);
        String priceunitid = priceUnit.getPriceUnitId();
        String price = priceUnit.getPrice();
        String munit = priceUnit.getUnit();
        long quantity = getCartdata(priceunitid);
        Log.d(TAG, "addtoCartSort: qu " + priceunitid);
        if(work.equals("add")) {
            if(quantity == 0) {
                quantity++;
                CartOffline cartOffline = new CartOffline(pid, name, imageurl, priceunitid, quantity, price, munit);
                db.cartDao().insertNew(cartOffline);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                binding1.lytPlusMinus.setVisibility(View.VISIBLE);
                binding1.tvAdd.setVisibility(View.GONE);
                Log.d(TAG, TAG + quantity);
            } else {
                Log.d(TAG, TAG);
                quantity++;
                db.cartDao().updateObj(quantity, priceunitid);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                Log.d(TAG, TAG + quantity);
            }
        } else if(work.equals("less")) {
            Log.d(TAG, TAG);
            if(quantity >= 0) {
                quantity--;
                db.cartDao().updateObj(quantity, priceunitid);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                if(quantity == 0) {
                    db.cartDao().deleteObjbyPid(priceunitid);
                    binding1.lytPlusMinus.setVisibility(View.GONE);
                    binding1.tvAdd.setVisibility(View.VISIBLE);
                }
            }
            Log.d(TAG, TAG + quantity);
        }
        binding.pd.setVisibility(View.GONE);


    }

    public void onClickBack(View view) {
        super.onBackPressed();
    }
}