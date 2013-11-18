package com.kolarov.organizeit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private View mRootView;

    final private int REQUEST_CODE_SAVE_NOTE = 1001;

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
            GetItemDetailsTask saveItemTask = new GetItemDetailsTask(getActivity(), this.mItemId, rootView);
            saveItemTask.execute();
            //((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.content);
        }

        this.mRootView = rootView;
        setupButtonHandlers(rootView);

        return rootView;
    }

    private void setupButtonHandlers(View rootView) {
        Button addNote = (Button) rootView.findViewById(R.id.buttonAddNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAddNoteButton((Button) view);
            }
        });
    }

    private void handleAddNoteButton(Button view) {
        Activity mActivity = this.getActivity();
        Intent intent = new Intent(mActivity, AddNoteActivity.class);
        intent.putExtra(mActivity.getString(R.string.ParentIdExtraLabel), this.mItemId);

        startActivityForResult(intent, REQUEST_CODE_SAVE_NOTE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int successResultCode = Activity.RESULT_OK;

        if (requestCode == REQUEST_CODE_SAVE_NOTE && resultCode == successResultCode) {
            refreshData();
        }
    }

    private void refreshData() {
        GetItemDetailsTask loadItemsTask = new GetItemDetailsTask(this.getActivity(), this.mItemId, this.mRootView);
        loadItemsTask.execute();
    }

    public class GetItemDetailsTask extends AsyncTask<Void, Void, ItemDetailsModel> {
        private Activity mActivity;
        private ProgressDialog dialog;
        private Context context;
        private int mElementId;
        private String mSessionKey;
        private View mRootView;

        public GetItemDetailsTask(Activity activity, int elementId, View rootView) {
            this.mActivity = activity;
            this.context = this.mActivity;
            this.mRootView = rootView;
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
            if (itemModel != null){
                TextView textViewTitle = (TextView) this.mRootView.findViewById(R.id.textViewItemTitle);
                textViewTitle.setText(itemModel.title);

                TextView textViewNotes = (TextView) this.mRootView.findViewById(R.id.textViewItemNotes);
                textViewNotes.setText(itemModel.notes);
            }

            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
    }
}
