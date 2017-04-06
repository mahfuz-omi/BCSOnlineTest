package com.example.omi.navigationdrawercommondemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by omi on 5/13/2016.
 */
public class BCSApplication extends Application {
    private boolean isUserLoggedIn = false;
    private String access_token = "";
    private String userName = "";
    private boolean isLoginWithFB = false;


    private SharedPreferences sharedPreferences;
    private String KEY_ACCESS_TOKEN = "access_token";
    private String KEY_USER_NAME = "user_name";
    private String KEY_IsLoginWithFB = "isLoginWithFB";

    @Override
    public void onCreate() {
        super.onCreate();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.isUserLoggedIn = this.sharedPreferences.getBoolean("isUserLoggedIn",false);
        this.access_token = this.sharedPreferences.getString(KEY_ACCESS_TOKEN,"");
        this.userName = this.sharedPreferences.getString(KEY_USER_NAME,"");
        this.isLoginWithFB = this.sharedPreferences.getBoolean(KEY_IsLoginWithFB,false);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }


    public boolean isUserLoggedIn()
    {
        return this.isUserLoggedIn;
    }


    public void setLoginWithFB()
    {
        this.isLoginWithFB = true;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IsLoginWithFB, isLoginWithFB);
        editor.commit();

    }

    public void unsetLoginWithFB()
    {
        this.isLoginWithFB = false;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IsLoginWithFB, isLoginWithFB);
        editor.commit();

    }

    public boolean isUserLogidWithFB()
    {
        return this.isLoginWithFB;
    }


    public void loggedInUser(String userName,String access_token)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, access_token);
        editor.putString(KEY_USER_NAME, userName);
        editor.putBoolean("isUserLoggedIn",true);
        editor.commit();
        this.isUserLoggedIn = true;
        this.userName = userName;
        this.access_token = access_token;
    }


    public String getUserName()
    {
        return this.userName;
    }

    public String getAccessToken()
    {
        return this.access_token;
    }
    public void setAccess_token(String access_token)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, access_token);
        editor.putBoolean("isUserLoggedIn",true);
        editor.commit();
        this.access_token = access_token;
        this.isUserLoggedIn = true;
    }



    public void setUserName(String user_name)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, user_name);
        editor.putBoolean("isUserLoggedIn",true);
        editor.commit();

        this.userName = user_name;
        this.isUserLoggedIn = true;

    }

    public void logoutUser()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, "");
        editor.putString(KEY_USER_NAME, "");
        editor.putBoolean("isUserLoggedIn",false);
        editor.commit();

        this.isUserLoggedIn = false;
        this.userName = "";
        this.access_token = "";
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
