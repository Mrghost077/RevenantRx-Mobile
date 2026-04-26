package com.mrghost077.revenantrx.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public TokenManager(Context context){
        sharedPreferences = context.getSharedPreferences("REVENANT_PREFS", context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveToken(String token){
        editor.putString("auth_token", token);
        editor.apply();
    }

    public String getToken(){
        return sharedPreferences.getString("auth_token", null);
    }

    public void clearToken(){
        editor.remove("auth_token");
        editor.apply();
    }
}
