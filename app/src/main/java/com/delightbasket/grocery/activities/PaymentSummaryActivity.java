package com.delightbasket.grocery.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.room.Room;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.CouponAdapter;
import com.delightbasket.grocery.adapters.OrderItemsTextAdapter;
import com.delightbasket.grocery.dao.AppDatabase;
import com.delightbasket.grocery.dao.CartOffline;
import com.delightbasket.grocery.databinding.ActivityPaymentSummaryBinding;
import com.delightbasket.grocery.databinding.BottomsheetProductweightBinding;
import com.delightbasket.grocery.model.Address;
import com.delightbasket.grocery.model.ApplyCoupon;
import com.delightbasket.grocery.model.Coupon;
import com.delightbasket.grocery.model.RazorpaySuccessResponse;
import com.delightbasket.grocery.model.RestResponse;
import com.delightbasket.grocery.model.ShippingChargeRoot;
import com.delightbasket.grocery.model.WalletDataResponse;
import com.delightbasket.grocery.model.schedulelistmodel.DataItem;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.stripe.android.Stripe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.stripe.Stripe.apiKey;

public class PaymentSummaryActivity extends AppCompatActivity {
    private static final String TAG = "paymentactivity";
    ActivityPaymentSummaryBinding binding;
    boolean isShow = false;
    List<String> listSchedule = new ArrayList<>();
    SessionManager sessionManager;
    List<DataItem> dataItem = new ArrayList<>();
    private String token, user_id;
    private RetrofitService service;
    private List<Coupon.Datum> coupons;
    BottomSheetDialog bottomSheetDialog;
    private int paymentType = 1;
    List<String> pids = new ArrayList<>();
    private long totalamount = 0;
    long discount = 0;
    private long shippingCharge = 0;
    String deliveryAddress = "";
    private String paymentIntentClientSecret;
    private Stripe stripe;

    String userAddress = "";
    private String lang = "";
    private String lat = "";
    private String addressString = "";
    List<String> unitids = new ArrayList<>();
    List<String> quantities = new ArrayList<>();
    private long subTotal;
    private Address.Datum address;
    private List<CartOffline> list = new ArrayList<>();
    private String couponCode = "";
    private boolean couponSelected = false;
    double walletPrice = 0.0;

    private void radioButtonListnear() {
        binding.radioCash.setChecked(true);
        binding.radioCash.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                binding.radioOnline.setChecked(false);
                binding.radioCash.setChecked(true);
                binding.radioWallet.setChecked(false);
                paymentType = 1;
                toggleCardUI();
            }
        });
        binding.radioOnline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.radioCash.setChecked(false);
                binding.radioOnline.setChecked(true);
                binding.radioWallet.setChecked(false);
                paymentType = 2;
                toggleCardUI();
            }
        });
        binding.radioWallet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.radioOnline.setChecked(false);
                binding.radioWallet.setChecked(true);
                binding.radioCash.setChecked(false);
                paymentType = 3;
                toggleCardUI();
            }
        });
    }

    private void toggleCardUI() {
        if(paymentType == 1) {
            binding.lytCard.setVisibility(View.GONE);
            binding.btnPlaceOrder.setText("Place Order");
        } else {
            binding.lytCard.setVisibility(View.GONE);
            binding.btnPlaceOrder.setText("Check Out");
        }
    }



    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void deleteCartData(long totalamount) {
        AppDatabase db = Room.databaseBuilder(this,
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();


        List<CartOffline> offlineList = db.cartDao().getall();

        for (CartOffline cart : offlineList) {
            db.cartDao().deleteObjbyPid(cart.getPriceUnitId());
        }
        showCustomDialog(totalamount);

    }

    private void getCouponListData() {
        binding.pBar.setVisibility(View.VISIBLE);
        Call<Coupon> call = service.getCouponData(Const.DEV_KEY, token, user_id);
        call.enqueue(new Callback<Coupon>() {
            @Override
            public void onResponse(Call<Coupon> call, Response<Coupon> response) {
                if (response.code() == 200 && response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                    coupons = response.body().getData();

                    openCouponBottomSheet();
                    binding.pBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Coupon> call, Throwable t) {
//ll
            }
        });
    }

    public void check() {
        if (binding.checkBox.isChecked()) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_summary);
        sessionManager = new SessionManager(this);
        service = RetrofitBuilder.create(this);
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            user_id = sessionManager.getUser().getData().getUserId();
            Log.d("TAG", "onCreate: " + token);

            apiKey = Const.STRIPE_SECRET_KEY;
            initView();
            getShippingCharge();
            radioButtonListnear();
            initListnear();
            callGetWalletData();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void callGetWalletData() {
        binding.pBar.setVisibility(View.VISIBLE);
        Call<WalletDataResponse> call = service.getWalletData(Const.DEV_KEY, token);
        call.enqueue(new Callback<WalletDataResponse>() {
            @Override
            public void onResponse(Call<WalletDataResponse> call, Response<WalletDataResponse> response) {
                binding.pBar.setVisibility(View.GONE);
                if (response.code() == 200 && response.body().getStatus() == 200 && response.body().getData() != null) {
                    walletPrice = Double.parseDouble(response.body().getData().getAmount());
                }
            }

            @Override
            public void onFailure(Call<WalletDataResponse> call, Throwable t) {
                binding.pBar.setVisibility(View.GONE);
                Toast.makeText(PaymentSummaryActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void applyCoupon() {
        binding.pBar.setVisibility(View.VISIBLE);
        Call<ApplyCoupon> call = service.applyCoupon(Const.DEV_KEY, token, user_id, couponCode, String.valueOf(subTotal));
        call.enqueue(new Callback<ApplyCoupon>() {
            @Override
            public void onResponse(Call<ApplyCoupon> call, Response<ApplyCoupon> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus() == 200 && response.body().getData() != null) {

                        binding.etCoupon.setTextColor(ContextCompat.getColor(PaymentSummaryActivity.this, android.R.color.holo_green_light));
                        binding.etCoupon.setText(couponCode);
                        subTotal = response.body().getData().getSubtotal();
                        setTotalPrice();
                        binding.tvDiscountPrice.setText(getString(R.string.currency) + response.body().getData().getCouponDiscount());
                        discount = response.body().getData().getCouponDiscount();

                        placeOrder();
                    } else if (response.body().getStatus() == 401) {
                        binding.etCoupon.setText(response.body().getMessage());
                        binding.etCoupon.setTextColor(ContextCompat.getColor(PaymentSummaryActivity.this, R.color.color_red));

                    }
                    binding.pBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ApplyCoupon> call, Throwable t) {
//ll
            }
        });
    }


    private void getShippingCharge() {
        Call<ShippingChargeRoot> call = service.getShippingCharge("English",Const.DEV_KEY, user_id, token);
        call.enqueue(new Callback<ShippingChargeRoot>() {
            @Override
            public void onResponse(Call<ShippingChargeRoot> call, Response<ShippingChargeRoot> response) {
                if(response.code() == 200 && response.body().getStatus() == 200 && response.body().getData() != null) {
                    shippingCharge = response.body().getData().getShippingCharge();
                    Log.d(TAG, "onResponse: shipping " + shippingCharge);
                    setPrice();
                }
            }

            @Override
            public void onFailure(Call<ShippingChargeRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void openCouponBottomSheet() {

        bottomSheetDialog = new BottomSheetDialog(this);
        BottomsheetProductweightBinding couopnSheet = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheet_productweight, null, false);
        bottomSheetDialog.setContentView(couopnSheet.getRoot());
        couopnSheet.tvCoupon.setVisibility(View.VISIBLE);
        CouponAdapter couponAdapter = new CouponAdapter(coupons, subTotal, coupon -> {
            couponSelected = true;
            couponCode = coupon.getCouponCode();
            binding.etCoupon.setText(couponCode);
            bottomSheetDialog.dismiss();

        });
        couopnSheet.listProductWeight.setAdapter(couponAdapter);
        bottomSheetDialog.show();
        couopnSheet.tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

    }

    private void initView() {
        Intent intent = getIntent();
        lat = intent.getStringExtra("lat");
        lang = intent.getStringExtra("lang");

        addressString = intent.getStringExtra("address");
        if(addressString != null && !addressString.equals("")) {
            Address.Datum tempAddress = new Gson().fromJson(addressString, Address.Datum.class);
            if(tempAddress != null) {
                address = tempAddress;
                setAddress();
            }
        }
        AppDatabase db = Room.databaseBuilder(this,
                AppDatabase.class, Const.DB_NAME).allowMainThreadQueries().build();
        list.clear();
        list = db.cartDao().getall();

        OrderItemsTextAdapter orderItemsTextAdapter = new OrderItemsTextAdapter(list);
        binding.rvOrdersItems.setAdapter(orderItemsTextAdapter);
        binding.tvOrderItems.setText("Order Items (".concat(String.valueOf(list.size())).concat(")"));

        setPrice();


        binding.lytCard.setVisibility(View.GONE);
        paymentType = 1;

        binding.cardInputWidget.setPostalCodeEnabled(false);
        binding.cardInputWidget.setPostalCodeRequired(false);

        binding.pBar.setVisibility(View.GONE);
    }


    private void initListnear() {

        binding.tvOrderItems.setOnClickListener(v -> {
            if(isShow) {
                binding.imgArrow.setRotation(0);
                binding.rvOrdersItems.setVisibility(View.GONE);
                isShow = false;
            } else {
                binding.imgArrow.setRotation(180);
                binding.rvOrdersItems.setVisibility(View.VISIBLE);
                isShow = true;
            }
        });
        binding.imgArrow.setOnClickListener(v -> {
            if(isShow) {
                binding.imgArrow.setRotation(0);

                isShow = false;
                binding.rvOrdersItems.setVisibility(View.GONE);
            } else {
                binding.imgArrow.setRotation(180);
                binding.rvOrdersItems.setVisibility(View.VISIBLE);
                isShow = true;
                expand(binding.rvOrdersItems);
            }
        });
        binding.btnCoupon.setOnClickListener(v -> getCouponListData());

        binding.btnPlaceOrder.setOnClickListener(v -> {
            if (paymentType == 1) {
                if (couponSelected) {
                    applyCoupon();
                } else {
                    placeOrder();
                }

            } else if (paymentType == 3) {
                if (walletPrice > totalamount) {
                    placeOrderUsingWallet();
                } else {
                    Toast.makeText(PaymentSummaryActivity.this, "You have not enough money to checkout", Toast.LENGTH_LONG).show();
                }
            } else {
                if (couponSelected) {
                    applyCoupon();
                } else {
                    Intent intent = new Intent(PaymentSummaryActivity.this, MakePaymentActivity.class);
                    intent.putExtra("Amount", String.valueOf(totalamount));
                    startActivityForResult(intent, 2231);
                }
            }
        });
    }

    private void placeOrderUsingWallet() {
        binding.pBar.setVisibility(View.VISIBLE);
        Call<RazorpaySuccessResponse> call = service.walletAmountDeductAmount(Const.DEV_KEY, token, String.valueOf(totalamount));
        call.enqueue(new Callback<RazorpaySuccessResponse>() {
            @Override
            public void onResponse(Call<RazorpaySuccessResponse> call, Response<RazorpaySuccessResponse> response) {
                binding.pBar.setVisibility(View.GONE);
                if (response.code() == 200 && response.body().getStatus() == 200) {
                    deleteCartData(totalamount);
                    showCustomDialog(100);
                }
            }

            @Override
            public void onFailure(Call<RazorpaySuccessResponse> call, Throwable t) {
                binding.pBar.setVisibility(View.GONE);
                Toast.makeText(PaymentSummaryActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void setPrice() {
        totalamount = 0;
        subTotal = 0;
        binding.tvShippnigCharge.setText(getString(R.string.currency) + String.valueOf(shippingCharge));
        setSubTotal();
        setTotalPrice();
    }


    public void onClickBack(View view) {
        super.onBackPressed();
    }


    private void setTotalPrice() {
        totalamount = 0;
        totalamount = subTotal + shippingCharge;
        binding.tvSubtotalPrice.setText(getString(R.string.currency) + subTotal);
        binding.tvtotalPrice.setText(getString(R.string.currency) + totalamount);
        binding.tvTotalPrice.setText(getString(R.string.currency) + totalamount);
    }

    private void setSubTotal() {
        subTotal = 0;
        for(int i = 0; i <= list.size() - 1; i++) {
            CartOffline product = list.get(i);
            long price = Long.parseLong(product.getPrice()) * product.getQuantity();
            Log.d(TAG, "getTotalAmount: " + product.getPrice() + " * " + product.getQuantity());
            subTotal = subTotal + price;
        }
        binding.tvSubtotalPrice.setText(getString(R.string.currency) + String.valueOf(subTotal));
    }

    private void setAddress() {
        binding.tvName.setText(address.getFirstName().concat(" " + address.getLastName()));
        userAddress = address.getHomeNo().concat(" " + address.getSociety() + " " + address.getStreet() + " " +
                address.getArea() + " " + address.getCity() + " " + address.getPincode());
        binding.tvAddress.setText(userAddress);

    }

    private void placeOrder() {
        if(list.isEmpty()) {
            Toast.makeText(this, "No Order Found", Toast.LENGTH_SHORT).show();
            return;
        }
        for(int i = 0; i <= list.size() - 1; i++) {
            CartOffline product = list.get(i);
            Log.d(TAG, "uploadCart:forpids  " + product.getPid() + " qq " + product.getQuantity());
            pids.add(product.getPid());
            unitids.add(product.getPriceUnitId());
            quantities.add(String.valueOf(product.getQuantity()));
        }

        binding.pBar.setVisibility(View.VISIBLE);
        if(addressString.equals("")) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        Call<RestResponse> call = service.placeOrder(
                Const.DEV_KEY,
                token,
                user_id,
                String.valueOf(totalamount),
                String.valueOf(paymentType),
                String.valueOf(shippingCharge),
                address.getDeliveryAddressId(),
                String.valueOf(discount),
                addressString,
                lat,
                lang,
                pids,
                unitids,
                quantities);

        Log.e("Place order data: ", "Key : " + Const.DEV_KEY);
        Log.e("Place order data: ", "token : " + token);
        Log.e("Place order data: ", "user_id : " + user_id);
        Log.e("Place order data: ", "totalamount : " + totalamount);
        Log.e("Place order data: ", "paymentType : " + paymentType);
        Log.e("Place order data: ", "shippingCharge :" + shippingCharge);
        Log.e("Place order data: ", "Addressid : " + address.getDeliveryAddressId());
        Log.e("Place order data: ", "discount : " + discount);
        Log.e("Place order data: ", "address : " + addressString);
        Log.e("Place order data: ", "latitude : " + lat);
        Log.e("Place order data: ", "longitude : " + lang);
        Log.e("Place order data: ", "product ids array : " + pids);
        Log.e("Place order data: ", "unitids array : " + unitids);
        Log.e("Place order data: ", "quantities : " + quantities);

        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        Toast.makeText(PaymentSummaryActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        deleteCartData(totalamount);

                    } else {
                        Toast.makeText(PaymentSummaryActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    binding.pBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                binding.pBar.setVisibility(View.GONE);
            }
        });
    }

    private void showCustomDialog(long totalamount) {
        View mDialogView = LayoutInflater.from(this).inflate(R.layout.placebid_custom_dialog, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog).setView(mDialogView);
        mBuilder.setCancelable(false);
        AlertDialog mAlertDialog = mBuilder.show();
        TextView btnViewOrder;
        btnViewOrder = mDialogView.findViewById(R.id.btnViewOrder);
        btnViewOrder.setOnClickListener(v -> {
            mAlertDialog.dismiss();
            onBackPressed();
            onBackPressed();
            onBackPressed();
            Intent intent = new Intent(PaymentSummaryActivity.this, MyOrdersActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2231) {
            boolean message = data.getBooleanExtra("MESSAGE", false);
            if (message) {
                deleteCartData(totalamount);
                showCustomDialog(100);
            }
        }
    }

}