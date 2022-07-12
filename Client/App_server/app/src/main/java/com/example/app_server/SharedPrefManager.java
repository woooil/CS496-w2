package com.example.app_server;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SharedPrefManager {
  
  private static final String SHARED_PREF_NAME = "userinfosharedpref";
  private static final String KEY_USERID = "keyuserid";
  private static final String KEY_EMAIL = "keyemail";
  private static final String KEY_NICKNAME = "keynickname";
  
  private static SharedPrefManager mInstance;
  private static Context mCtx;
  
  private SharedPrefManager(Context context) {
    mCtx = context;
  }
  
  public static synchronized SharedPrefManager getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new SharedPrefManager(context);
    }
    return mInstance;
  }
  
  public void userLogin(UsersModal user) {
    SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(KEY_USERID, user.getUserId());
    editor.putString(KEY_EMAIL, user.getEmail());
    editor.putString(KEY_NICKNAME, user.getNickname());
    editor.apply();
  }
  
  public boolean isLoggedIn() {
    SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    return sharedPreferences.getString(KEY_NICKNAME, null) != null;
  }
  
  public UsersModal getUser() {
    SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    return new UsersModal(
        sharedPreferences.getString(KEY_USERID, null),
        sharedPreferences.getString(KEY_EMAIL, null),
        sharedPreferences.getString(KEY_NICKNAME, null)
    );
  }
  
  public void logout() {
    SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.clear();
    editor.apply();
    mCtx.startActivity(new Intent(mCtx, MainActivity.class));
  }
}

