package com.delightbasket.grocery.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.delightbasket.grocery.model.RestResponse;
import com.delightbasket.grocery.model.ShippingChargeRoot;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent.Status;
import com.stripe.android.view.CardInputWidget;
import com.stripe.param.PaymentIntentCreateParams;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.stripe.Stripe.apiKey;

public class PaymentSummaryActivity extends AppCompatActivity {
    private static final String TAG = "paymentactivity";
    ActivityPaymentSummaryBinding binding;
    boolean isShow = false;
    SessionManager sessionManager;
    private String token, user_id;
    String transId = "XXXXXXX";
    String cardType = "VISA";
    String cardNo = "XXXXYYYYZZZZAAAA";
    String cardExpDate = "2-22";
    String cardHolderName = "ANURAGSSS";
    String cardHolderEmail = "anurag@gmail.com";
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

    private void radioButtonListnear() {
        binding.radioCash.setChecked(true);
        binding.radioCash.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                binding.radioOnline.setChecked(false);
                binding.radioCash.setChecked(true);
                paymentType = 1;
                toggleCardUI();
            }
        });
        binding.radioOnline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                binding.radioCash.setChecked(false);
                binding.radioOnline.setChecked(true);
                paymentType = 2;
                toggleCardUI();
            }
        });
    }

    private void toggleCardUI() {
        if(paymentType == 1) {
            binding.lytCard.setVisibility(View.GONE);
            binding.btnPlaceOrder.setText("Place Order");
        } else {
            binding.lytCard.setVisibility(View.VISIBLE);
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

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
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
                if(response.code() == 200 && response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_summary);
        sessionManager = new SessionManager(this);
        service = RetrofitBuilder.create(this);
        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken();
            user_id = sessionManager.getUser().getData().getUserId();
            Log.d("TAG", "onCreate: " + token);

            apiKey = Const.STRIPE_SECRET_KEY;

            stripe = new Stripe(
                    getApplicationContext(),
                    Objects.requireNonNull(Const.STRIPE_PUBLISHABLE_KEY)
            );


            initView();
            getShippingCharge();
            radioButtonListnear();
            initListnear();

        }else{
            startActivity(new Intent(this, LoginActivity.class));

        }

    }

    private void applyCoupon() {
        binding.pBar.setVisibility(View.VISIBLE);
        Call<ApplyCoupon> call = service.applyCoupon(Const.DEV_KEY, token, user_id,couponCode, String.valueOf(subTotal));
        call.enqueue(new Callback<ApplyCoupon>() {
            @Override
            public void onResponse(Call<ApplyCoupon> call, Response<ApplyCoupon> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus() == 200 && response.body().getData() != null) {

                        binding.etCoupon.setTextColor(ContextCompat.getColor(PaymentSummaryActivity.this, android.R.color.holo_green_light));
                        binding.etCoupon.setText(couponCode);
                        subTotal = response.body().getData().getSubtotal();
                        setTotalPrice();
                        binding.tvDiscountPrice.setText(getString(R.string.currency) + String.valueOf(response.body().getData().getCouponDiscount()));
                        discount = response.body().getData().getCouponDiscount();

                        placeOrder();
                    } else if(response.body().getStatus() == 401) {
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
            if(paymentType == 1) {
                if(couponSelected) {
                    applyCoupon();
                } else {
                    placeOrder();
                }

            } else {
                if(couponSelected) {
                    applyCoupon();
                } else {
                    PaymentMethodCreateParams params = binding.cardInputWidget.getPaymentMethodCreateParams();
                    if(params != null) {
                        confirmPayment();
                    } else {
                        Toast.makeText(this, "Enter Card Details", Toast.LENGTH_SHORT).show();
                    }
                }
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

    private void confirmPayment() {

        binding.pBar.setVisibility(View.VISIBLE);
        new MyTask().execute();
        Log.d(TAG, "confirmPayment: ");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
        binding.pBar.setVisibility(View.GONE);
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message, boolean b) {
        binding.pBar.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            if(b) {
                placeOrder();
            } else {
                builder.create().dismiss();
            }
        });
        builder.create().show();
    }

    private void setTotalPrice() {
        totalamount = 0;
        totalamount = subTotal + shippingCharge;
        binding.tvSubtotalPrice.setText(getString(R.string.currency) + String.valueOf(subTotal));
        binding.tvtotalPrice.setText(getString(R.string.currency) + String.valueOf(totalamount));
        binding.tvTotalPrice.setText(getString(R.string.currency) + String.valueOf(totalamount));
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

        Log.e("Place order data: ","Key : " +Const.DEV_KEY );
        Log.e("Place order data: ", "token : "+token);
        Log.e("Place order data: ", "user_id : " + user_id);
        Log.e("Place order data: ", "totalamount : " + String.valueOf(totalamount));
        Log.e("Place order data: ","paymentType : "+ String.valueOf(paymentType) );
        Log.e("Place order data: ", "shippingCharge :"+String.valueOf(shippingCharge));
        Log.e("Place order data: ", "Addressid : "+ address.getDeliveryAddressId());
        Log.e("Place order data: ","discount : "+String.valueOf(discount) );
        Log.e("Place order data: ","address : "+addressString );
        Log.e("Place order data: ", "latitude : "+  lat );
        Log.e("Place order data: ","longitude : "+lang );
        Log.e("Place order data: ","product ids array : "+ String.valueOf(pids) );
        Log.e("Place order data: ","unitids array : "+String.valueOf(unitids) );
        Log.e("Place order data: ","quantities : "+String.valueOf(quantities) );

//        HashMap<Object, Object> requestBody = new HashMap<Object, Object>();
//        requestBody.put("user_id", user_id);
//        requestBody.put("total_amount",String.valueOf(totalamount) );
//        requestBody.put("payment_type",String.valueOf(paymentType) );
//        requestBody.put("latitude", lat);
//        requestBody.put("longitude", lang);
//        requestBody.put("coupon_discount", discount);
//        requestBody.put("shipping_charge", shippingCharge);
//        requestBody.put("delivery_address_id", address.getDeliveryAddressId());

//        Call<RestResponse> call = service.placeOrder(
//                Const.DEV_KEY,
//                token,
//                requestBody,
//                pids,
//                unitids,
//                quantities);


        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if(response.code() == 200 && response.body() != null) {
                    if(response.body().getStatus() == 200) {
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
//        textSuccess = mDialogView.findViewById(R.id.textSuccess);
//        textSuccess.setText("Congratulation You have save " + totalamount + " on your order");
        btnViewOrder.setOnClickListener(v -> {
            mAlertDialog.dismiss();
            Intent intent = new Intent(PaymentSummaryActivity.this, MyOrdersActivity.class);
            startActivity(intent);
        });
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<PaymentSummaryActivity> activityRef;

        PaymentResultCallback(@NonNull PaymentSummaryActivity activity) {
            activityRef = new WeakReference<>(activity);
            Log.d(TAG, "PaymentResultCallback: ");
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final PaymentSummaryActivity activity = activityRef.get();
            if(activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            Status status = paymentIntent.getStatus();
            if(status == Status.Succeeded) {

                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Log.d(TAG, "onSuccess: payment== " + gson.toString());
                long amount = paymentIntent.getAmount() / 100;
                String message = "Amount: " + amount + "$" + "\n Status: " + paymentIntent.getStatus().toString();

                activity.displayAlert(
                        "Payment completed",
                        message, true
                );
            } else if(status == Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(), false
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final PaymentSummaryActivity activity = activityRef.get();
            if(activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            Log.d(TAG, "onSuccess: error== " + e.toString());
            activity.displayAlert("Error", e.getMessage(), false);
        }
    }

    private class MyTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {
            binding.pBar.setVisibility(View.VISIBLE);
            com.stripe.Stripe.apiKey = Const.STRIPE_SECRET_KEY;

            PaymentIntentCreateParams params1 =
                    PaymentIntentCreateParams.builder()
                            .setAmount(totalamount * 100)
                            .setDescription("User id :" + address.getUserId() + " email: " + sessionManager.getUser().getData().getEmail())
                            .setReceiptEmail(sessionManager.getUser().getData().getEmail())
                            //   .putExtraParam("email",sessionManager.getUser().getData().getEmail())
                            .setShipping(
                                    PaymentIntentCreateParams.Shipping.builder()
                                            .setName(address.getFirstName().concat(" " + address.getLastName()))
                                            .setPhone(address.getMobileNumber())
                                            .setAddress(
                                                    PaymentIntentCreateParams.Shipping.Address.builder()
                                                            .setLine1(address.getHomeNo().concat(" " + address.getSociety().concat(" " + address.getArea())))
                                                            .setPostalCode("91761")
                                                            .setLine2(sessionManager.getUser().getData().getEmail())
                                                            .setCity(address.getCity())
                                                            .setState(address.getLandmark())
                                                            .setCountry("US")
                                                            .build())
                                            .build())
                            .setCurrency("USD")

                            .addPaymentMethodType("card")
                            .build();

            com.stripe.model.PaymentIntent paymentIntent = null;

            try {
                paymentIntent = com.stripe.model.PaymentIntent.create(params1);
            } catch(com.stripe.exception.StripeException e) {
                e.printStackTrace();
                Log.d(TAG, "startCheckout: errr 64 " + e);
            }


            paymentIntentClientSecret = paymentIntent != null ? paymentIntent.getClientSecret() : null;
            Log.d(TAG, "doInBackground:0 " + paymentIntentClientSecret);

            Log.d(TAG, "doInBackground:1 " + paymentIntentClientSecret);
            return paymentIntentClientSecret;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(paymentType == 2) {
                CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                cardInputWidget.setPostalCodeRequired(false);
                cardInputWidget.setPostalCodeEnabled(false);
                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

                if(params != null && paymentIntentClientSecret != null) {
                    Log.d(TAG, "confirmPayment: " + params.toString());
                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                            .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                    stripe.confirmPayment(PaymentSummaryActivity.this, confirmParams);
                    Log.d(TAG, "onResponse: cps == " + confirmParams.getClientSecret());
                }
            }

        }
    }


}