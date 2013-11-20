package com.kolarov.organizeit.Tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.kolarov.organizeit.HttpRequester;
import com.kolarov.organizeit.Models.LocationModel;
import com.kolarov.organizeit.R;
import com.kolarov.organizeit.UserStatusManager;

/**
 * Created by N.Kolarov on 13-11-18. *
 *  A task that sends the data to the web service and saves the item location.
 */

public class SaveItemLocationTask extends AsyncTask<Void, Void, LocationModel> {
    private Activity mActivity;
    private ProgressDialog dialog;
    private Context context;
    private int mItemId;
    private LocationModel mLocationModel;
    private String mSessionKey;

    public SaveItemLocationTask(Activity activity, LocationModel locationModel, int itemId) {
        this.mActivity = activity;
        this.context = this.mActivity;
        this.mItemId = itemId;
        this.mLocationModel = locationModel;
        this.mSessionKey = new UserStatusManager(context).getSessionKey();
        this.dialog = new ProgressDialog(this.context);
    }

    @Override
    protected void onPreExecute() {
        // TODO i18n
        this.dialog.setMessage(mActivity.getString(R.string.save_item_location_message));
        this.dialog.show();
    }

    @Override
    protected LocationModel doInBackground(Void... params) {
        try {
            String serviceURL = mActivity.getString(R.string.save_item_location_serice_url) + this.mItemId;

            HttpRequester requester = new HttpRequester(this.context);
            LocationModel model = requester.Post(serviceURL, LocationModel.class, this.mSessionKey, this.mLocationModel);

            int a = 5;
            return model;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(LocationModel locationModel) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }
}


