package com.kolarov.organizeit.Tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.kolarov.organizeit.HttpRequester;
import com.kolarov.organizeit.Models.ItemShortModel;
import com.kolarov.organizeit.Models.LocationModel;
import com.kolarov.organizeit.UserStatusManager;

/**
 * Created by N.Kolarov on 13-11-19.
 */
public class DeleteItemTask extends AsyncTask<Void, Void, ItemShortModel> {
    private Activity mActivity;
    private ProgressDialog dialog;
    private Context context;
    private int mItemId;
    private String mSessionKey;

    public DeleteItemTask(Activity activity,  int itemId) {
        this.mActivity = activity;
        this.context = this.mActivity;
        this.mItemId = itemId;
        this.mSessionKey = new UserStatusManager(context).getSessionKey();
        this.dialog = new ProgressDialog(this.context);
    }

    @Override
    protected void onPreExecute() {
        // TODO i18n
        this.dialog.setMessage("Please wait.. Deleting item..");
        this.dialog.show();
    }

    @Override
    protected ItemShortModel doInBackground(Void... params) {
        try {
            String serviceURL = "items/delete?itemId=" + this.mItemId;

            HttpRequester requester = new HttpRequester(this.context);
            ItemShortModel model = requester.Delete(serviceURL, ItemShortModel.class, this.mSessionKey);

            int a = 5;
            return model;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ItemShortModel itemShortModel) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }
}
