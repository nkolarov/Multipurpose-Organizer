package com.kolarov.organizeit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kolarov.organizeit.Models.ItemCreateModel;

public class AddItemActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        if (savedInstanceState == null) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                fragment.setArguments(extras);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a the view for adding an item.
     */
    public static class PlaceholderFragment extends Fragment {
        private int mParentID;
        private int mTypeID;

        final private int ITEM_TYPE_ID = 1;
        final private int ITEM_ELEMENT_ID = 2;
        final private int NO_PARENT_ID = 0;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.mTypeID = getArguments().getInt(this.getString(R.string.ItemTypeExtraLabel));
            this.mParentID = getArguments().getInt(this.getString(R.string.ParentIdExtraLabel));

            View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
            TextView labelTextView = (TextView) rootView.findViewById(R.id.textViewAddItem);
            if (this.mTypeID == ITEM_TYPE_ID) {
                labelTextView.setText(getActivity().getString(R.string.new_type_label));
            }

            if (this.mTypeID == ITEM_ELEMENT_ID) {
                labelTextView.setText(getActivity().getString(R.string.new_element_label));
            }

            setupButtonHandlers(rootView);

            return rootView;
        }

        private void setupButtonHandlers(View rootView) {
            Button saveItem = (Button) rootView.findViewById(R.id.buttonSaveItem);
            saveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleSaveButton((Button) view);
                }
            });

            Button cancelSave = (Button) rootView.findViewById(R.id.buttonCancelSave);
            cancelSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleCancelSaveButton((Button) view);
                }
            });
        }

        private void handleCancelSaveButton(Button view) {
            this.getActivity().setResult(Activity.RESULT_CANCELED);
            this.getActivity().finish();
        }

        private String getEnteredTitle() {
            EditText editText = (EditText) getActivity().findViewById(R.id.editTitle);
            Editable editable = editText.getText();

            return editable != null ? editable.toString() : getActivity().getString(R.string.empty_string);
        }

        private void handleSaveButton(Button view) {
            SaveItemTask saveItemTask = new SaveItemTask(getActivity(), getEnteredTitle(), this.mParentID, this.mTypeID);
            saveItemTask.execute();
        }

        /**
         * A task that sends the data to the web service and saves the item.
         */
        public class SaveItemTask extends AsyncTask<Void, Void, ItemCreateModel> {
            private Activity mActivity;
            private ProgressDialog dialog;
            private Context mContext;
            private int mParentId;
            private String mTitle;
            private int mItemType;
            private String mSessionKey;

            public SaveItemTask(Activity activity, String title, int parentId, int itemType) {
                this.mActivity = activity;
                this.mContext = this.mActivity;
                this.mParentId = parentId;
                this.mItemType = itemType;
                this.mTitle = title;
                this.mSessionKey = new UserStatusManager(mContext).getSessionKey();
                this.dialog = new ProgressDialog(this.mContext);
            }

            @Override
            protected void onPreExecute() {
                // TODO i18n
                this.dialog.setMessage(mActivity.getString(R.string.save_item_message));
                this.dialog.show();
            }

            @Override
            protected ItemCreateModel doInBackground(Void... params) {
                try {
                    ItemCreateModel itemModel = new ItemCreateModel();
                    itemModel.parentid = this.mParentId;
                    itemModel.itemtype = this.mItemType;
                    itemModel.title = this.mTitle;

                    HttpRequester requester = new HttpRequester(this.mContext);
                    ItemCreateModel model = requester.Post(mActivity.getString(R.string.add_items_service_url), ItemCreateModel.class, this.mSessionKey, itemModel);

                    return model;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ItemCreateModel itemModel) {
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }

                this.mActivity.setResult(Activity.RESULT_OK);
                this.mActivity.finish();
            }
        }
    }
}
