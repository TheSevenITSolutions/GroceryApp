package com.delightbasket.grocery.retrofit;

import com.delightbasket.grocery.model.Address;
import com.delightbasket.grocery.model.ApplyCoupon;
import com.delightbasket.grocery.model.Area;
import com.delightbasket.grocery.model.BannerRoot;
import com.delightbasket.grocery.model.Cart;
import com.delightbasket.grocery.model.Categories;
import com.delightbasket.grocery.model.CategoryProduct;
import com.delightbasket.grocery.model.CityRoot;
import com.delightbasket.grocery.model.ComplainRoot;
import com.delightbasket.grocery.model.Coupon;
import com.delightbasket.grocery.model.FaqRoot;
import com.delightbasket.grocery.model.MainCategory;
import com.delightbasket.grocery.model.NotificationRoot;
import com.delightbasket.grocery.model.OrderDetailRoot;
import com.delightbasket.grocery.model.OrderRoot;
import com.delightbasket.grocery.model.Otp;
import com.delightbasket.grocery.model.Pincode;
import com.delightbasket.grocery.model.ProductMain;
import com.delightbasket.grocery.model.RatingRoot;
import com.delightbasket.grocery.model.RazorpayOrderIdResponse;
import com.delightbasket.grocery.model.RazorpaySuccessResponse;
import com.delightbasket.grocery.model.RestResponse;
import com.delightbasket.grocery.model.RestResponse2;
import com.delightbasket.grocery.model.Search;
import com.delightbasket.grocery.model.SearchCatProduct;
import com.delightbasket.grocery.model.ShippingChargeRoot;
import com.delightbasket.grocery.model.SortRoot;
import com.delightbasket.grocery.model.Summary;
import com.delightbasket.grocery.model.User;
import com.delightbasket.grocery.model.WalletDataResponse;
import com.delightbasket.grocery.model.Wishlist;
import com.delightbasket.grocery.model.postsubscription.PostSubscriptionResponse;
import com.delightbasket.grocery.model.schedulelistmodel.ScheduleListResponse;
import com.delightbasket.grocery.model.subscriptionmodel.SubscriptionListByCategoryIdResponse;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface RetrofitService {

    @FormUrlEncoded
    @POST("User/registration")
    Call<User> registerUser(@Header("api-key") String key,
                            @Field("first_name") String firstName,
                            @Field("last_name") String lastName,
                            @Field("email") String email,
                            @Field("login_type") String loginType,
                            @Field("identity") String identity,
                            @Field("device_type") String deviceType,
                            @Field("device_token") String deviceToken,
                            @Field("profile_image") String profileImage);

    @FormUrlEncoded
    @POST("User/registration")
    Call<User> registerUser(@Header("api-key") String key,
                            @Field("fullname") String fullname,
                            @Field("mobile_no") String mobile_no,
                            @Field("email") String email,
                            @Field("device_token") String device_token,
                            @Field("device_type") String device_type);

    @FormUrlEncoded
    @POST("User/login")
    Call<User> loginUser(@Header("api-key") String key,
                         @Field("mobile_no") String mobile_no,
                         @Field("device_token") String device_token,
                         @Field("device_type") String device_type);


    @POST("User/verify")
    Call<Otp> checkOTP(@Header("api-key") String key,
                       @Body HashMap<String, String> body);

    @FormUrlEncoded
    @POST("Product/getProductList")
    Call<Categories> getAllData(@Header("api-key") String key,
                                @Field("user_id") String uid,
                                @Field("start") int start,
                                @Field("limit") int limit);

    @FormUrlEncoded
    @POST("Product/getProductList")
    Call<Categories.Datum> getProductList(@Header("api-key") String key,
                                          @Field("user_id") String uid,
                                          @Field("start") int start,
                                          @Field("limit") int limit);


    @GET("Product/getCategoryList")
    Call<MainCategory> getCategoryList(@Header("api-key") String key,
                                       @Query("page") int page,
                                       @Query("perpage") int perpage);

    @FormUrlEncoded
    @POST("Product/getProductByCategoryId")
    Call<CategoryProduct> getProductByCatogery(@Header("api-key") String key,
                                               @Field("category_id") String cid,
                                               @Field("start") int start,
                                               @Field("limit") int limit);

    @FormUrlEncoded
    @POST("Product/addupdateToCart")
    Call<RestResponse> addProductToCart(@Header("api-key") String key,
                                        @Header("Authorization") String userToken,
                                        @Field("product_id[]") List<String> pids,
                                        @Field("price_unit_id[]") List<String> priceid,
                                        @Field("quantity[]") List<String> quantity);

    @GET("Product/getCartList")
    Call<Cart> getCardData(@Header("api-key") String key,
                           @Header("Authorization") String userToken);

    @FormUrlEncoded
    @POST("Product/addupdateToWishlist")
    Call<RestResponse> addProductToWishlist(@Header("api-key") String key,
                                            @Header("Authorization") String userToken,
                                            @Field("product_id") String pid);

    @FormUrlEncoded
    @POST("Product/removeProductFromCart")
    Call<RestResponse> removeFromCart(@Header("api-key") String key,
                                      @Header("Authorization") String userToken,
                                      @Field("product_id") String pId,
                                      @Field("price_unit_id") String priceId);

    @Multipart
    @POST("User/updateProfile")
    Call<User> updateUser(@Header("api-key") String key,
                          @Header("Authorization") String token,
                          @PartMap Map<String, RequestBody> partMap,
                          @Part MultipartBody.Part requestBody);


    @GET("Product/getWishlistList")
    Call<Wishlist> getWishlist(@Header("api-key") String key,
                               @Header("Authorization") String userToken);

    @FormUrlEncoded
    @POST("Product/removeProductFromWishlist")
    Call<RestResponse> removeFromWishlist(@Header("api-key") String key,
                                          @Header("Authorization") String userToken,
                                          @Field("product_id") String pId);

    @FormUrlEncoded
    @POST("Product/searchProduct")
    Call<Search> getSearch(@Header("api-key") String key,
                           @Field("search_keyword") String keyword,
                           @Field("start") int start,
                           @Field("limit") int limit);

    @FormUrlEncoded
    @POST("User/addDeliveryAddress")
    Call<Address> addAddress(@Header("api-key") String key,
                             @Header("Authorization") String token,
                             @FieldMap HashMap<String, String> data);

    @FormUrlEncoded
    @POST("User/addDeliveryAddress")
    Call<Address> addAddress(@Header("api-key") String key,
                             @Header("Authorization") String token,
                             @FieldMap HashMap<String, String> data,
                             @Header("user_id") String userToken);

    @FormUrlEncoded
    @POST("User/updateDeliveryAddress")
    Call<RestResponse> updateAddress(@Header("api-key") String key,
                                     @Header("Authorization") String token,
                                     @FieldMap HashMap<String, String> data);

    @FormUrlEncoded
    @POST("User/updateDeliveryAddress")
    Call<RestResponse> updateAddress(@Header("api-key") String key,
                                     @Header("Authorization") String token,
                                     @FieldMap HashMap<String, String> data,
                                     @Header("user_id") String userToken);

    @GET("User/getAllDeliveryAddress")
    Call<Address> getAllAddress(@Header("api-key") String key,
                                @Header("user_id") String userToken);

    @GET("User/getAllDeliveryAddress")
    Call<Address> getAllAddress(@Header("api-key") String key,
                                @Header("Authorization") String token,
                                @Header("user_id") String user_id);

    @FormUrlEncoded
    @POST("Product/getProductById")
    Call<ProductMain> getOneProduct(@Header("api-key") String key,
                                    @Field("user_id") String uid,
                                    @Field("product_id") String pid);


    @FormUrlEncoded
    @POST("Product/searchProductByCategory")
    Call<SearchCatProduct> getSearchCat(@Header("api-key") String key,
                                        @Field("search_keyword") String keyword,
                                        @Field("category_id") String catId,
                                        @Field("limit") String limit);

    @FormUrlEncoded
    @POST("Order/getPaymentSummary")
    Call<Summary> getSummary(@Header("api-key") String key,
                             @Header("Authorization") String userToken,
                             @Field("delivery_address_id") String addressId);

    @GET("Settings/getCityList")
    Call<CityRoot> getCity(@Header("api-key") String key);

    @FormUrlEncoded
    @POST("Settings/getAreaByPincode")
    Call<Area> getArea(@Header("api-key") String key, @Field("pincode_id") String pincode_id);

    @FormUrlEncoded
    @POST("Settings/getPincodeByCity")
    Call<Pincode> getPincode(@Header("api-key") String key, @Field("city_id") String citiyid);


    @GET("Order/getCouponList")
    Call<Coupon> getCouponData(@Header("api-key") String key,
                               @Header("Authorization") String userToken);

    @GET("Order/getCouponList")
    Call<Coupon> getCouponData(@Header("api-key") String key,
                               @Header("Authorization") String userToken,
                               @Header("user_id") String user_id);

    @FormUrlEncoded
    @POST("Order/applyCoupon")
    Call<ApplyCoupon> applyCoupon(@Header("api-key") String key,
                                  @Header("Authorization") String userToken,
                                  @Field("coupon_code") String couponCode,
                                  @Field("subtotal") String subtotal);

    @FormUrlEncoded
    @POST("Order/applyCoupon")
    Call<ApplyCoupon> applyCoupon(@Header("api-key") String key,
                                  @Header("Authorization") String userToken,
                                  @Field("user_id") String user_id,
                                  @Field("coupon_code") String couponCode,
                                  @Field("subtotal") String subtotal);

    @FormUrlEncoded
    @POST("Order/getMyOrderList")
    Call<OrderRoot> getMyOrders(@Header("api-key") String key,
                                @Header("Authorization") String userToken,
                                @Field("start") int start,
                                @Field("limit") int limit);

    @FormUrlEncoded
    @POST("Order/placeOrder")
    Call<RestResponse> placeOrder(@Header("api-key") String key,
                                  @Header("Authorization") String userToken,
                                  @Field("user_id") String user_id,
                                  @Field("total_amount") String totalAmount,
                                  @Field("payment_type") String patmentType,
                                  @Field("shipping_charge") String shipping,
                                  @Field("delivery_address_id") String address,
                                  @Field("coupon_discount") String coupon_discount,
                                  @Field("address") String useraddress,
                                  @Field("latitude") String lat,
                                  @Field("longitude") String lang,
                                  @Field("product_id") List<String> pids,
                                  @Field("price_unit_id") List<String> priceid,
                                  @Field("quantity") List<String> quantity);

//    @FormUrlEncoded
//    @POST("Order/placeOrder")
//    Call<RestResponse> placeOrder(@Header("api-key") String key,
//                                  @Header("Authorization") String userToken,
//                                  @Field HashMap<Object, Object> body,
//                                  @Field("product_id[]") List<String> pids,
//                                  @Field("price_unit_id[]") List<String> puds,
//                                  @Field("quantity[]") List<String> qts
//    );

    @FormUrlEncoded
    @POST("Order/getOrderDetailsById")
    Call<OrderDetailRoot> getOrdersDetail(@Header("api-key") String key,
                                          @Header("Authorization") String userToken,
                                          @Field("order_id") String orderId);

    @FormUrlEncoded
    @POST("Order/raiseComplaint")
    Call<RestResponse> raiseComplain(@Header("api-key") String key,
                                     @Header("Authorization") String userToken,
                                     @Field("order_id") String orderId,
                                     @Field("title") String title,
                                     @Field("mobile_no") String mobileNo,
                                     @Field("description") String des);

    @GET("Settings/getBannerList")
    Call<BannerRoot> getBanner(@Header("api-key") String key);


    @POST("User/Logout")
    Call<RestResponse2> logout(@Header("api-key") String key,
                               @Header("Authorization") String userToken);

    @FormUrlEncoded
    @POST("Product/sortByProduct")
    Call<SortRoot> getSortAll(@Header("api-key") String key,
                              @Field("sort_by") String sortBy,
                              @Field("limit") int limit,
                              @Field("start") int start,
                              @Field("user_id") String uid, @Field("search_keyword") String keyword);

    @FormUrlEncoded
    @POST("Product/sortByProduct")
    Call<SortRoot> getSortCategory(@Header("api-key") String key,
                                   @Field("sort_by") String sortBy,
                                   @Field("limit") int limit,
                                   @Field("start") int start,
                                   @Field("user_id") String uid,
                                   @Field("search_keyword") String keyword,
                                   @Field("category_id") String catId);

    @FormUrlEncoded
    @POST("Order/getAllComplaint")
    Call<ComplainRoot> getComplainData(@Header("api-key") String key,
                                       @Header("Authorization") String userToken,
                                       @Field("limit") int limit,
                                       @Field("start") int start);


    @FormUrlEncoded
    @POST("User/deleteDeliveryAddress")
    Call<RestResponse> deleteDeliveryAddress(@Header("api-key") String key,
                                             @Header("Authorization") String userToken,
                                             @Header("user_id") String user_id,
                                             @Field("delivery_address_id") String dId);


    @FormUrlEncoded
    @POST("Order/cancelledOrder")
    Call<RestResponse> cancelOrder(@Header("api-key") String key,
                                   @Header("Authorization") String userToken,
                                   @Field("order_id") String oId);

    @GET("User/getDefaultDeliveryDetails")
    Call<Address> getDefaultAddress(@Header("api-key") String key,
                                    @Header("Authorization") String userToken);

    @FormUrlEncoded
    @POST("getAllNotification")
    Call<NotificationRoot> getNotifications(@Header("api-key") String key,
                                            @Header("Authorization") String userToken,
                                            @Field("start") int start,
                                            @Field("limit") int limit);

    @FormUrlEncoded
    @POST("Order/getAllOrderRating")
    Call<RatingRoot> getRatings(@Header("api-key") String key,
                                @Header("Authorization") String userToken,
                                @Field("start") int start,
                                @Field("limit") int limit);

    @FormUrlEncoded
    @POST("Order/orderReviewRating")
    Call<RestResponse> setRating(@Header("api-key") String key,
                                 @Header("Authorization") String userToken,
                                 @Field("rating") int start,
                                 @Field("order_id") String oId,
                                 @Field("review") String review);

    @GET("Order/getShippingCharge")
    Call<ShippingChargeRoot> getShippingCharge(@Header("api-key") String key,
                                               @Header("Authorization") String userToken);

    @GET("Order/getShippingCharge")
    Call<ShippingChargeRoot> getShippingCharge(@Header("Content-Language") String content_language,
                                               @Header("api-key") String key,
                                               @Header("user_id") String user_id,
                                               @Header("Authorization") String userToken);


    @FormUrlEncoded
    @POST("v1/customers")
    Call<JsonObject> getCustomer(@Header("Authorization") String userToken,
                                 @Field("address[line1]") String adress1, @Field("address[line2]") String adress2,
                                 @Field("address[city]") String city,
                                 @Field("address[state]") String state,
                                 @Field("address[postal_code]") String postalcode,
                                 @Field("address[country]") String country,
                                 @Field("email") String email,
                                 @Field("phone") String mobile,
                                 @Field("name") String name,
                                 @Field("description") String description);

    @FormUrlEncoded
    @POST("v1/payment_intents")
    Call<JsonObject> getPaymentIntent(@Header("Authorization") String userToken,
                                      @Field("currency") String currency,
                                      @Field("description") String description,
                                      @Field("customer") String customer,
                                      @Field("amount") String amount);


    @GET("Settings/getFAQList")
    Call<FaqRoot> getFaqs(@Header("api-key") String key);

    @FormUrlEncoded
    @POST("Razorpay/razorpayOrderCreate")
    Call<RazorpayOrderIdResponse> getRazorPayId(@Header("api-key") String key, @Header("Authorization") String userToken, @Field("amount") String amount);

    @FormUrlEncoded
    @POST("Razorpay/razorpayPaymentSuccess")
    Call<RazorpaySuccessResponse> getRazorPaymentSuccess(@Header("api-key") String key,
                                                         @Header("Authorization") String userToken,
                                                         @Field("transaction_id") String transaction_id,
                                                         @Field("order_id") String order_id,
                                                         @Field("amount") String amount);

    @FormUrlEncoded
    @POST("Razorpay/walletAdd")
    Call<RazorpaySuccessResponse> addWallet(@Header("api-key") String key,
                                            @Header("Authorization") String userToken,
                                            @Field("user_id") String user_id,
                                            @Field("phone_number") String order_id,
                                            @Field("amount") String amount,
                                            @Field("transaction_id") String transaction_id,
                                            @Field("transaction_status") String transaction_status);

    @GET("Razorpay/walletAmountGet")
    Call<WalletDataResponse> getWalletData(@Header("api-key") String key,
                                           @Header("Authorization") String userToken);

    @FormUrlEncoded
    @POST("Razorpay/walletAmountDeductAmount")
    Call<RazorpaySuccessResponse> walletAmountDeductAmount(@Header("api-key") String key,
                                                           @Header("Authorization") String userToken,
                                                           @Field("amount") String amount);

    @FormUrlEncoded
    @POST("subscription/getSubscriptionList")
    Call<SubscriptionListByCategoryIdResponse> getSubscriptionList(@Header("Authorization") String userToken,
                                                                   @Field("start") String start,
                                                                   @Field("limit") String limit,
                                                                   @Field("category_id") String category_id);

    @GET("Product/getScheduleList")
    Call<ScheduleListResponse> getScheduleList(@Header("Authorization") String userToken,
                                               @Header("api-key") String key);

    @FormUrlEncoded
    @POST("subscription/postSubscription")
    Call<PostSubscriptionResponse> getPostSubscription(@Header("Authorization") String userToken,
                                                       @Header("api-key") String key,
                                                       @Field("subscription_id") String SubscriptionId,
                                                       @Field("product_id") String Product_id,
                                                       @Field("schedule_id") String ScheduleId,
                                                       @Field("quantity") String Quantity,
                                                       @Field("price_unit_id") String Price_unit_id,
                                                       @Field("delivery_address_id") String delivery_address_id,
                                                       @Field("promo_code") String promo_code);

}
