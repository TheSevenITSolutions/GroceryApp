package com.delightbasket.grocery.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.SearchAdapter;
import com.delightbasket.grocery.adapters.SortAdapter;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.dao.CartOffline;
import com.delightbasket.grocery.databinding.ActivitySearchBinding;
import com.delightbasket.grocery.databinding.BottomsheetSortingBinding;
import com.delightbasket.grocery.databinding.ItemSearchProductBinding;
import com.delightbasket.grocery.model.SortRoot;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String TAG = "sssss";
    RetrofitService service;
    ActivitySearchBinding binding;
    SearchAdapter searchAdapter = new SearchAdapter();
    SessionManager sessionManager;
    private String userId = "";
    private BottomsheetSortingBinding sortingBinding;
    private int sortType = 1;
    private SortAdapter sortAdapter;
    int start = 0;

    private boolean isLoding = false;
    private String keyword = "";
    private boolean isSort = true;
    private int searchstart = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        service = RetrofitBuilder.create(this);
        sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            userId = sessionManager.getUser().getData().getUserId();
            Log.d(TAG, "onCreate: " + sessionManager.getUser().getData().getDeviceToken());


        } else {
//            startActivity(new Intent(this, LoginActivity.class));
        }
        isSort = true;
        binding.shimmer.startShimmer();
        initView();
        initListnear();
        binding.swipe.setOnRefreshListener(this);

    }

    private void initListnear() {
        binding.etSearchProduct.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//ll
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyword = String.valueOf(s);

                SearchActivity.this.start = 0;

                if(binding.swipe.isRefreshing()) {
                    binding.swipe.setRefreshing(true);
                }

                sortAdapter = new SortAdapter();
                binding.rvProductlist.setAdapter(sortAdapter);
                getsortData(0, true);


            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });

        sortAdapter.setOnSortItemClick((datum, binding1, work, defultPriceUnit) -> {
            Log.d(TAG, "initListnear: " + defultPriceUnit.getQuantity());
            long quantity = getCartdata(defultPriceUnit.getPriceUnitId());
            if(work.equals("add")) {

                binding1.lytPlusMinus.setVisibility(View.VISIBLE);
                binding1.tvAdd.setVisibility(View.GONE);
                Log.d(TAG, "initListnear: prr " + defultPriceUnit.getPriceUnitId());
                addtoCartSort(datum, "add", defultPriceUnit, binding1);
            } else if(work.equals("less")) {
                if(quantity == 0) {
                    Toast.makeText(this, "You will reach minimum limit", Toast.LENGTH_SHORT).show();
                } else {
                    addtoCartSort(datum, "minus", defultPriceUnit, binding1);
                }
            }
        });

        binding.rvProductlist.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!binding.rvProductlist.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvProductlist.getLayoutManager();
                    Log.d(TAG, "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d(TAG, "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d(TAG, "onScrollStateChanged:188 " + totalitem);
                    if(!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {
                        Log.d(TAG, "onScrollStateChanged: " + start);

                        binding.pd2.setVisibility(View.VISIBLE);
                        if(isSort) {
                            start = start + Const.LIMIT;
                            getsortData(start, false);
                        } else {
                            searchstart = searchstart + 2;
                        }

                    }
                }

                Log.d(TAG, "onScrolled: 202");


            }
        });

    }


    private void addtoCartSort(SortRoot.DataItem unit, String work, SortRoot.PriceUnitItem priceUnit, ItemSearchProductBinding binding1) {
        //  unit.getIsCart()
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();


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
                Log.d(TAG, "addToCart: added " + quantity);
            } else {
                Log.d(TAG, TAG);
                quantity++;
                db.cartDao().updateObj(quantity, priceunitid);
                db.close();
                binding1.tvQuantity.setText(String.valueOf(quantity));
                Log.d(TAG, TAG + quantity);
            }
        } else if(work.equals("minus")) {
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


    }

    private void initView() {

        sortAdapter = new SortAdapter();
        binding.rvProductlist.setAdapter(sortAdapter);

        if(binding.swipe.isRefreshing()) {
            binding.swipe.setRefreshing(true);
        }
        isSort = true;
        getsortData(start, false);


    }


    public void onClickSort(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        sortingBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheet_sorting, null, false);
        bottomSheetDialog.setContentView(sortingBinding.getRoot());
        bottomSheetDialog.show();
        sortingBinding.rb1.setOnClickListener(this);
        sortingBinding.rb2.setOnClickListener(this);
        sortingBinding.rb3.setOnClickListener(this);
        sortingBinding.rb4.setOnClickListener(this);


        setOldSortType();
        sortingBinding.imgClose.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
        sortingBinding.tvApply.setOnClickListener(v -> {

            sortAdapter = new SortAdapter();
            binding.rvProductlist.setAdapter(sortAdapter);
            start = 0;
            if(binding.swipe.isRefreshing()) {
                binding.swipe.setRefreshing(true);
            }
            getsortData(start, false);
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
            case 4:
                sortingBinding.rb4.setChecked(true);
                break;

            default:

        }
    }

    private void getsortData(int start, boolean search) {
        isSort = true;
        isLoding = true;
        binding.lyt404.setVisibility(View.GONE);


        Log.d(TAG, "getData: ok======sort  " + keyword + " " + start);
        Call<SortRoot> call = service.getSortAll(Const.DEV_KEY, String.valueOf(sortType), Const.LIMIT, start, keyword);
        call.enqueue(new Callback<SortRoot>() {
            @Override
            public void onResponse(Call<SortRoot> call, Response<SortRoot> response) {
                isLoding = false;
                if(response.code() == 200) {
                    if(response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {


                        if(search) {
                            sortAdapter = new SortAdapter();
                            binding.rvProductlist.setAdapter(sortAdapter);
                            initListnear();
                        }
                        sortAdapter.addData(response.body().getData());
                        if(binding.swipe.isRefreshing()) {
                            binding.swipe.setRefreshing(false);
                        }

                    } else if(sortAdapter.getItemCount() == 0) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }

                binding.pd2.setVisibility(View.GONE);
                binding.swipe.setRefreshing(false);

                binding.shimmer.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<SortRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });

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
        start = 0;
        searchstart = 0;
        if(!isLoding) {
            if(isSort) {
                sortAdapter = new SortAdapter();
                binding.rvProductlist.setAdapter(sortAdapter);
                getsortData(start, false);
            } else {
                searchAdapter = new SearchAdapter();
                binding.rvProductlist.setAdapter(searchAdapter);
            }
        }
        initListnear();
        binding.lyt404.setVisibility(View.GONE);
    }


    private void removeAllrbCheck() {
        sortingBinding.rb1.setChecked(false);
        sortingBinding.rb2.setChecked(false);
        sortingBinding.rb3.setChecked(false);
        sortingBinding.rb4.setChecked(false);

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
            case R.id.rb4:
                sortingBinding.rb4.setChecked(true);
                sortType = 4;
                break;
            default:
        }
    }

    public void onClickBack(View view) {
        super.onBackPressed();
    }
}