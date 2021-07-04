package com.delightbasket.grocery.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.delightbasket.grocery.R
import com.delightbasket.grocery.SessionManager
import com.delightbasket.grocery.model.FaqRoot
import com.delightbasket.grocery.model.RazorpayOrderIdResponse
import com.delightbasket.grocery.model.RazorpaySuccessResponse
import com.delightbasket.grocery.retrofit.Const
import com.delightbasket.grocery.retrofit.RetrofitBuilder
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MakePaymentActivity : AppCompatActivity(), PaymentResultListener {

    var amount = "0"
    var orderId = ""
    private var token: String? = null
    private var userId: String? = null
    private var mobileNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_payment)

        amount = (100 * 100).toString()
        sessionManager = SessionManager(this)
        service = RetrofitBuilder.create(this)
        if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            token = sessionManager.getUser().getData().getToken()
            userId = sessionManager.getUser().getData().getUserId()
            mobileNo = sessionManager.getUser().getData().getMobileNo()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        Checkout.preload(applicationContext)
        createOrderOnRazorPay()
    }

    private fun createOrderOnRazorPay() {
        val call = RetrofitBuilder.create(getActivity()).getRazorPayId(Const.DEV_KEY, amount)
        call.enqueue(object : Callback<RazorpayOrderIdResponse> {
            override fun onResponse(
                call: Call<RazorpayOrderIdResponse>,
                response: Response<RazorpayOrderIdResponse>
            ) {
                if (response.isSuccessful && response.body()!!.status == 200 && !response.body()!!.data.isEmpty()) {
                    orderId = response.body()!!.data.orderId
                    startPayment(response.orderId)
                }
            }

            override fun onFailure(call: Call<FaqRoot>, t: Throwable) {
                Toast.makeText(this@MakePaymentActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun startPayment(orderId: String) {
        val checkout = Checkout()
        checkout.setKeyID(Const.RAZORPAY_API_KEY_ID)
        checkout.setImage(R.drawable.logo_app_png)
        val activity: Activity = this
        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("description", "Item")
            options.put("image", ContextCompat.getDrawable(R.drawable.logo_app_png))
            options.put("order_id", orderId) //from response of step 3.
            options.put("theme.color", "#2ca7bb")
            options.put("currency", Const.CURRENCY_INR)
            options.put("amount", amount) //pass amount in currency subunits
            options.put("prefill.email", "ibrahim.malada1234@gmail.com")
            options.put("prefill.contact", mobileNo)
            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e("ERROR", e.message!!)
        }
    }

    override fun onPaymentSuccess(razorPayPaymentID: String?) {
        updateRazorPayPayment(razorPayPaymentID)
    }

    override fun onPaymentError(p0: Int, e: String?) {
        Log.e("error", e!!)
        onBackPressed()
    }

    private fun updateRazorPayPayment(razorPayPaymentID: String?) {
        val call = RetrofitBuilder.create(getActivity())
            .getRazorPaymentSuccess(Const.DEV_KEY, razorPayPaymentID, orderId, amount)
        call.enqueue(object : Callback<RazorpaySuccessResponse> {
            override fun onResponse(
                call: Call<RazorpaySuccessResponse>,
                response: Response<RazorpaySuccessResponse>
            ) {
                if (response.isSuccessful && response.body()!!.status == 200 && !response.body()!!.data.isEmpty()) {
                    Toast.makeText(
                        this@MakePaymentActivity,
                        response.body()!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<FaqRoot>, t: Throwable) {
                Toast.makeText(this@MakePaymentActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }


}