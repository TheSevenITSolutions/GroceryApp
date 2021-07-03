package com.delightbasket.grocery.configs;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import java.util.List;


/**
 * Created by Navneet Boghani on 16,June,2016
 */
public class AppController extends MultiDexApplication {
    public static final String TAG = AppController.class
            .getSimpleName();


    private static AppController mInstance;


    @Override
    public void onCreate() {
        super.onCreate();

       // Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        mInstance = this;
        StrictMode.VmPolicy.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            builder = new StrictMode.VmPolicy.Builder();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setVmPolicy(builder.build());
        }

//        OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
//                .build();
//        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

//        getClient(this);

    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

//    private OkHttpClient getClient(Context context) {
//        return new OkHttpClient.Builder()
//                .addInterceptor(new ChuckerInterceptor(context))
//                .build();
//    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }



}