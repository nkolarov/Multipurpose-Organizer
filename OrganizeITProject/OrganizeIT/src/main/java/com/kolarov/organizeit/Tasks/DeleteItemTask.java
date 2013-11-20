package com.kolarov.organizeit.Tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.kolarov.organizeit.HttpRequester;
import com.kolarov.organizeit.Models.ItemShortModel;
import com.kolarov.organizeit.R;
import com.kolarov.organizeit.UserStatusManager;

/**
 * Created by N.Kolarov on 13-11-19.
 *  A task that sends the data to the web service and deletes the item.
 */
public class DeleteItemTask extends AsyncTask<Void, Void, ItemShortModel> {
    private Activity mActivity;
    private ProgressDialog dialog;
    private Context mContext;
    private int mItemId;
    private String mSessionKey;

    public DeleteItemTask(Activity activity,  int itemId) {
        this.mActivity = activity;
        this.mContext = this.mActivity;
        this.mItemId = itemId;
        this.mSessionKey = new UserStatusManager(mContext).getSessionKey();
        this.dialog = new ProgressDialog(this.mContext);
    }

    @Override
    protected void onPreExecute() {
        // TODO i18n
        this.dialog.setMessage(mActivity.getString(R.string.delete_item_task_message));
        this.dialog.show();
    }

    @Override
    protected ItemShortModel doInBackground(Void... params) {
        try {
            String serviceURL = mActivity.getString(R.string.delete_item_service_url) + this.mItemId;

            HttpRequester requester = new HttpRequester(this.mContext);
            ItemShortModel model = requester.Delete(serviceURL, ItemShortModel.class, this.mSessionKey);

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
