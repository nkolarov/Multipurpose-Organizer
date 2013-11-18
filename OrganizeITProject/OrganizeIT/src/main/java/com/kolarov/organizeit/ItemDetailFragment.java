package com.kolarov.organizeit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kolarov.organizeit.Models.ItemCreateModel;
import com.kolarov.organizeit.Models.ItemDetailsModel;
import com.kolarov.organizeit.dummy.DummyContent;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    private int mItemId;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(getString(R.string.item_id))) {
            this.mItemId = getArguments().getInt(getString(R.string.item_id));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        if (this.mItemId != 0) {
            GetItemDetailsTask saveItemTask = new GetItemDetailsTask(getActivity(), this.mItemId);
            saveItemTask.execute();
            //((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.content);
        }

        return rootView;
    }

    public class GetItemDetailsTask extends AsyncTask<Void, Void, ItemDetailsModel> {
        private Activity mActivity;
        private ProgressDialog dialog;
        private Context context;
        private int mElementId;
        private String mSessionKey;

        public GetItemDetailsTask(Activity activity, int elementId) {
            this.mActivity = activity;
            this.context = this.mActivity;
            this.mElementId = elementId;
            this.mSessionKey = new UserStatusManager(context).getSessionKey();
            this.dialog = new ProgressDialog(this.context);
        }

        @Override
        protected void onPreExecute() {
            // TODO i18n
            this.dialog.setMessage("Please wait.. Loading details..");
            this.dialog.show();
        }

        @Override
        protected ItemDetailsModel doInBackground(Void... params) {
            try {
                HttpRequester requester = new HttpRequester(this.context);
                String serviceURL = "items/details?elementId=" + this.mElementId;
                ItemDetailsModel model = requester.Get(serviceURL, ItemDetailsModel.class, this.mSessionKey);

                return model;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ItemDetailsModel itemModel) {
            // TODO: setup view data.
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            //this.mActivity.setResult(Activity.RESULT_OK);
            //this.mActivity.finish();
        }
    }
}
