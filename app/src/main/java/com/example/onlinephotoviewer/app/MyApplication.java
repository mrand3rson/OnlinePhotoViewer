package com.example.onlinephotoviewer.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.onlinephotoviewer.mvp.models.SignUserOut;

import io.realm.Realm;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Andrei on 29.03.2018.
 */

public class MyApplication extends android.app.Application {

    private static final String API_BASE_URL = "http://junior.balinasoft.com/";

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    private static Retrofit retrofit;

    public static SignUserOut getUserInfo() {
        return userInfo;
    }

    public static void setUserInfo(SignUserOut newInfo) {
        userInfo = newInfo;
    }

    private static SignUserOut userInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
