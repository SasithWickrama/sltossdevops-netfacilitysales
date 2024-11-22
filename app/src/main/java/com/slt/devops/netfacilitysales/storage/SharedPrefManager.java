package com.slt.devops.netfacilitysales.storage;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefManager {

    private Context context;
    SharedPreferences sharedPreferences;

    private static SharedPrefManager mInstance;
    private Context mCtx;
    private static final String SHARED_PREF_NAME = "net_facility_shared";

    public SharedPrefManager(Context mCtx) {
        this.mCtx = mCtx;
    }


   /* public SharedPrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
    }*/


    public static synchronized SharedPrefManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }



    public void saveLoginDetails(String sid, String name, String contact, String regdate, String rtom) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sid", sid);
        editor.putString("name", name);
        editor.putString("contact", contact);
        editor.putString("regdate", regdate);
        editor.putString("rtom", rtom);

        editor.commit();
    }

    public void saveMobile(String mobile) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mobile", mobile);
        editor.commit();
    }

    public void saveIsLogged(Boolean val) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogged", val);
        editor.commit();
    }

    public void saveIsRegistered(Boolean val) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isRegistered", val);
        editor.commit();
    }


    public String getSid() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("sid", "");
    }
    public String getName() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }
    public String getContact() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("contact", "");
    }
    public String getRegdate() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("regdate", "");
    }
    public String getRtom() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("rtom", "");
    }


    public Boolean getIsLogged() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLogged", false);
    }

    public Boolean getIsRegistered() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isRegistered", false);
    }

    public String getMobile() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("mobile", "");
    }

    public void clear() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
