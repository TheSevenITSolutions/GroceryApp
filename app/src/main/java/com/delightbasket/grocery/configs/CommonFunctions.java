package com.delightbasket.grocery.configs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;
import android.widget.Toast;

import com.delightbasket.grocery.R;

public class CommonFunctions {

    static ProgressDialog pd;

    public static ProgressDialog createProgressBar(Context context,
                                                   String strMsg) {
        if (pd == null) {
            pd = new ProgressDialog(context, R.style.ProThemeOrange);
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pd.setMessage(strMsg);
            pd.setCancelable(false);
            pd.show();
        } else {
            pd.dismiss();
            pd = null;
            pd = new ProgressDialog(context, R.style.ProThemeOrange);
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pd.setMessage(strMsg);
            pd.setCancelable(false);
            pd.show();

        }
        return pd;
    }

    /**
     * Destroy progress bar
     */
    public static void destroyProgressBar() {
        if (pd != null)
            pd.dismiss();
    }

    public static boolean checkConnection(Activity activity) {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            Toast.makeText(activity, R.string.msg_NO_INTERNET_RESPOND, Toast.LENGTH_SHORT).show();
        }
        return isConnected;

    }
}
