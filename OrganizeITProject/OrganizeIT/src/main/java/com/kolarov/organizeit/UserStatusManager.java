package com.kolarov.organizeit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by N.Kolarov on 13-11-12.
 * Helper class that saves and retrieves user data from shared preferences.
 */
public class UserStatusManager {
    Context mContext;
    String sessionKeyLabel;

    public UserStatusManager(Context context) {
        this.mContext = context;
        this.sessionKeyLabel = this.mContext.getResources().getString(R.string.sessionKey);
    }

    public String getSessionKey() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        String sessionKey = prefs.getString(sessionKeyLabel, "");
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(sessionKeyLabel, sessionKey);
        editor.commit();
    }

    public void clearSessionKey() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        prefs.edit().remove(sessionKeyLabel).commit();
    }
}
