package com.delightbasket.grocery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.delightbasket.grocery.model.User;
import com.delightbasket.grocery.retrofit.Const;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.pref = context.getSharedPreferences(Const.PREF_NAME, MODE_PRIVATE);
        this.editor = this.pref.edit();
    }

    public void saveStringValue(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValue(String key) {
        return pref.getString(key, "");
    }

    public void saveBooleanValue(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key) {
        return pref.getBoolean(key, false);
    }

    public void saveUser(User user) {
        editor.putString(Const.USER, new Gson().toJson(user));
        editor.apply();
    }

    public User getUser() {
        String userString = pref.getString(Const.USER, "");
        if (!userString.isEmpty()) {
            return new Gson().fromJson(userString, User.class);
        }
        return null;
    }




}
