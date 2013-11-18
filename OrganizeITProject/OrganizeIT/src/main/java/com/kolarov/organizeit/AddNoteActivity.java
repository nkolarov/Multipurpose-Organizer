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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.kolarov.organizeit.Models.NoteModel;

public class AddNoteActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

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
        getMenuInflater().inflate(R.menu.add_note, menu);
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.mParentID = getArguments().getInt(this.getString(R.string.ParentIdExtraLabel));
            View rootView = inflater.inflate(R.layout.fragment_add_note, container, false);
            setupButtonHandlers(rootView);

            return rootView;
        }

        private void setupButtonHandlers(View rootView) {
            Button saveItem = (Button) rootView.findViewById(R.id.buttonSaveNote);
            saveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleSaveButton((Button) view);
                }
            });

            Button cancelSave = (Button) rootView.findViewById(R.id.buttonCancelSaveNote);
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

        private String getEnteredNoteContent(){
            EditText editText = (EditText) getActivity().findViewById(R.id.editNoteText);
            Editable editable = editText.getText();

            return editable != null ? editable.toString() : "";
        }

        private void handleSaveButton(Button view) {
            SaveItemTask saveItemTask = new SaveItemTask(getActivity(), getEnteredNoteContent(), this.mParentID);
            saveItemTask.execute();
        }

        public class SaveItemTask extends AsyncTask<Void, Void, NoteModel> {
            private Activity mActivity;
            private ProgressDialog dialog;
            private Context context;
            private int mItemId;
            private String mNoteContent;
            private String mSessionKey;

            public SaveItemTask(Activity activity, String content, int itemId) {
                this.mActivity = activity;
                this.context = this.mActivity;
                this.mItemId = itemId;
                this.mNoteContent = content;
                this.mSessionKey = new UserStatusManager(context).getSessionKey();
                this.dialog = new ProgressDialog(this.context);
            }

            @Override
            protected void onPreExecute() {
                // TODO i18n
                this.dialog.setMessage("Please wait.. Saving note..");
                this.dialog.show();
            }

            @Override
            protected NoteModel doInBackground(Void... params) {
                try {
                    NoteModel noteModel = new NoteModel();
                    noteModel.text = this.mNoteContent;
                    String serviceURL = "notes/add?itemId=" + this.mItemId;

                    HttpRequester requester = new HttpRequester(this.context);
                    NoteModel model = requester.Post(serviceURL, NoteModel.class, this.mSessionKey, noteModel);

                    return model;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(NoteModel noteModel) {
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }

                this.mActivity.setResult(Activity.RESULT_OK);
                this.mActivity.finish();
            }
        }
    }

}
