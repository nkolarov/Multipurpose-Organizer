package com.kolarov.organizeit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kolarov.organizeit.Models.ItemCreateModel;
import com.kolarov.organizeit.Models.LoggedUserModel;
import com.kolarov.organizeit.Models.UserModel;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
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
                labelTextView.setText("Enter new Type Details:");
            }

            if (this.mTypeID == ITEM_ELEMENT_ID) {
                labelTextView.setText("Enter new Element Details:");
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

        private String getEnteredTitle(){
            EditText editText = (EditText) getActivity().findViewById(R.id.editTitle);
            Editable editable = editText.getText();

            return editable != null ? editable.toString() : "";
        }

        private void handleSaveButton(Button view) {
            SaveItemTask saveItemTask = new SaveItemTask(getActivity(), getEnteredTitle(), this.mParentID, this.mTypeID);
            saveItemTask.execute();
        }

        public class SaveItemTask extends AsyncTask<Void, Void, ItemCreateModel> {
            private Activity mActivity;
            private ProgressDialog dialog;
            private Context context;
            private int mParentId;
            private String mTitle;
            private int mItemType;
            private String mSessionKey;

            public SaveItemTask(Activity activity, String title, int parentId, int itemType) {
                this.mActivity = activity;
                this.context = this.mActivity;
                this.mParentId = parentId;
                this.mItemType = itemType;
                this.mTitle = title;
                this.mSessionKey = new UserStatusManager(context).getSessionKey();
                this.dialog = new ProgressDialog(this.context);
            }

            @Override
            protected void onPreExecute() {
                // TODO i18n
                this.dialog.setMessage("Please wait.. Saving item..");
                this.dialog.show();
            }

            @Override
            protected ItemCreateModel doInBackground(Void... params) {
                try {
                    ItemCreateModel itemModel = new ItemCreateModel();
                    itemModel.parentid = this.mParentId;
                    itemModel.itemtype = this.mItemType;
                    itemModel.title = this.mTitle;

                    HttpRequester requester = new HttpRequester(this.context);
                    ItemCreateModel model = requester.Post("items/add", ItemCreateModel.class, this.mSessionKey, itemModel);

                    return model;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ItemCreateModel itemModel) {
                this.mActivity.setResult(Activity.RESULT_OK);
                this.mActivity.finish();
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
            }
        }
    }
}
