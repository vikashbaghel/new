package com.app.rupyz.generic.utils;

import static com.app.rupyz.generic.utils.SharePrefConstant.CART_MODEL;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.rupyz.MyApplication;
import com.google.gson.Gson;

public class SharedPref {
    private static SharedPreferences.Editor prefsEditor;
    private static volatile SharedPref mAppSharedPreferenceInstance;
    private final SharedPreferences sharedPref;

    private SharedPref(Context context) {
        sharedPref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        prefsEditor = sharedPref.edit();
    }

    public static SharedPref getInstance() {
        if (mAppSharedPreferenceInstance == null) {
            synchronized (SharedPref.class) {
                if (mAppSharedPreferenceInstance == null) {
                    mAppSharedPreferenceInstance = new SharedPref(MyApplication.Companion.getInstance());
                }
            }
        }
        return mAppSharedPreferenceInstance;
    }


    public void putString(String key, String value) {
        prefsEditor.putString(key, value).commit();
    }

    public String getString(String key) {
        return sharedPref.getString(key, "");
    }

    public void putInt(String key, int value) {
        prefsEditor.putInt(key, value).commit();
    }

    public int getInt(String key) {
        return sharedPref.getInt(key, 0);
    }

    public boolean getBoolean(String key, Boolean defaultValue) {
        return sharedPref.getBoolean(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        prefsEditor.putBoolean(key, value).commit();
    }

    public void putLong(String key, long value) {
        prefsEditor.putLong(key, value).commit();
    }

    public long getLong(String key) {
        return sharedPref.getLong(key, 0L);
    }

    public void putModelClass(String key, Object model) {
        prefsEditor.putString(key, new Gson().toJson(model)).commit();
    }

    public void clearCart() {
        prefsEditor.putString(CART_MODEL, "").commit();
    }

    public void clearSharePref() {
        prefsEditor.clear().apply();
    }
}
