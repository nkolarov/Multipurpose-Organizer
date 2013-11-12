package com.kolarov.organizeit;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.kolarov.organizeit.Models.LoggedUserModel;
import com.kolarov.organizeit.Models.UserModel;

/**
 * Created by N.Kolarov on 13-11-12.
 */
public class LoginTask extends AsyncTask<Void, Void, LoggedUserModel> {
    private MainActivity myMainActivity;
    private ProgressDialog dialog;
    private Context context;

    public LoginTask( MainActivity activity ) {
        this.myMainActivity = activity;
        this.context = this.myMainActivity;
        this.dialog = new ProgressDialog(this.context);
    }

    @Override
    protected void onPreExecute() {
        // TODO i18n
        this.dialog.setMessage("Please wait.. Logging in..");
        this.dialog.show();
    }

    @Override
    protected LoggedUserModel doInBackground(Void... params) {
       try {
            UserModel loginModel = new UserModel();
            loginModel.username = "kokokoko";
            loginModel.authCode = "bfff2dd4f1b310eb0dbf593bd83f94dd8d34077e";

            HttpRequester requester = new HttpRequester(this.context);
            LoggedUserModel model = requester.Post("users/login/", LoggedUserModel.class, null, loginModel);

            return model;
        } catch (Exception e) {
            return null;
        }
    }


    protected void onPostExecute(LoggedUserModel userModel) {
        if (userModel!=null) {
            SaveSessionKey(userModel);

            TextView et = (TextView) myMainActivity.findViewById(R.id.textView);
            String text = "Display name: " + userModel.displayName + " Session Key: " + userModel.sessionKey;
            et.setText(text);
        }

        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }

    private void SaveSessionKey(LoggedUserModel userModel) {
        UserStatusManager usm = new UserStatusManager(this.myMainActivity);
        usm.setSessionKey(userModel.sessionKey);
    }

}
