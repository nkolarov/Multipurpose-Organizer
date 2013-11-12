package com.kolarov.organizeit;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by N.Kolarov on 13-11-12.
 */
public class UserStatusManager {
    Activity activity;
    String sessionKeyLabel;

    public UserStatusManager(Activity activity){
        this.activity = activity;
        this.sessionKeyLabel = this.activity.getResources().getString(R.string.sessionKey);
    }

    public String getSessionKey(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.activity);

        return prefs.getString(sessionKeyLabel, "");
    }

    public Boolean setSessionKey(String sessionKey){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(sessionKeyLabel, sessionKey);
        editor.commit();

        return true;
    }
}
