package com.kolarov.organizeit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.kolarov.organizeit.Models.ItemModel;

import java.util.ArrayList;

/**
 * A list fragment representing a list of Items.
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ItemListFragment extends ListFragment {

    private ItemListAdapter mAdapter;

    private int currentParent;

    final private int ITEM_TYPE_ID = 1;

    final private int ITEM_ELEMENT_ID = 2;

    final private int NO_PARENT_ID = 0;

    final private int REQUEST_CODE_SAVE_ITEM = 1000;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
        }
    };

    private Activity mActivity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int parentId = NO_PARENT_ID;

        if (savedInstanceState != null) {
            parentId = savedInstanceState.getInt(getString(R.string.item_id));
            this.currentParent = parentId;
        } else {
            this.currentParent = NO_PARENT_ID;
        }

        this.mAdapter = new ItemListAdapter(this.mActivity);
        setListAdapter(this.mAdapter);

        LoadItems loadItemsTask = new LoadItems(this.mActivity, this.mAdapter, this.currentParent);
        loadItemsTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_fragment_layout, null);
        setupButtonHandlers(rootView);

        return rootView;
    }

    private void setupButtonHandlers(View rootView) {
        Button addType = (Button) rootView.findViewById(R.id.buttonAddNewType);
        addType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAddTypeButton((Button) view);
            }
        });

        Button addElement = (Button) rootView.findViewById(R.id.buttonAddElement);
        addElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAddElementButton((Button) view);
            }
        });
    }

    private void handleAddElementButton(Button view) {
        Intent intent = new Intent(this.mActivity, AddItemActivity.class);
        intent.putExtra(mActivity.getString(R.string.ItemTypeExtraLabel), ITEM_ELEMENT_ID);
        intent.putExtra(mActivity.getString(R.string.ParentIdExtraLabel), this.currentParent);

        startActivityForResult(intent, REQUEST_CODE_SAVE_ITEM);
    }


    private void handleAddTypeButton(Button view) {
        Intent intent = new Intent(this.mActivity, AddItemActivity.class);
        intent.putExtra(mActivity.getString(R.string.ItemTypeExtraLabel), ITEM_TYPE_ID);
        intent.putExtra(mActivity.getString(R.string.ParentIdExtraLabel), this.currentParent);

        startActivityForResult(intent, REQUEST_CODE_SAVE_ITEM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int successResultCode = Activity.RESULT_OK;

        if (requestCode == REQUEST_CODE_SAVE_ITEM && resultCode == successResultCode) {
            refreshList();
        }
    }

    private void refreshList() {
        LoadItems loadItemsTask = new LoadItems(this.mActivity, this.mAdapter, this.currentParent);
        loadItemsTask.execute();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mActivity = activity;
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        ItemModel item = (ItemModel) this.mAdapter.getItem(position);

        if (item.itemtype == ITEM_TYPE_ID) {
            LoadItems loadItemsTask;

            if (item.paerntid == NO_PARENT_ID) {
                this.currentParent = NO_PARENT_ID;
                loadItemsTask = new LoadItems(this.mActivity, this.mAdapter, NO_PARENT_ID);
            } else {
                this.currentParent = (int) id;
                loadItemsTask = new LoadItems(this.mActivity, this.mAdapter, (int) id);
            }

            loadItemsTask.execute();
        } else {
            mCallbacks.onItemSelected((int) id);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(getString(R.string.item_id), this.currentParent);

        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    /**
     * Created by N.Kolarov on 13-11-18. *
     * A task that loads the list items from a web service.
     */
    public class LoadItems extends AsyncTask<Void, Void, Iterable<ItemModel>> {
        private ItemListAdapter adapter;
        private Context context;
        private String sessionKey;
        private int parentID;
        final private String NO_PARENT_URL = "?parentID=null";
        final private String HAS_PARENT_URL = "?parentID=";
        private ProgressDialog dialog;

        public LoadItems(Context context, ItemListAdapter adapter, int parentID) {
            this.adapter = adapter;
            this.context = context;
            this.parentID = parentID;
            this.sessionKey = new UserStatusManager(context).getSessionKey();
            this.dialog = new ProgressDialog(this.context);
        }

        @Override
        protected void onPreExecute() {
            // TODO i18n
            this.dialog.setMessage(context.getString(com.kolarov.organizeit.R.string.fetch_data_message));
            this.dialog.show();
        }

        @Override
        protected Iterable<ItemModel> doInBackground(Void... params) {
            checkUserLogin();

            try {
                HttpRequester requester = new HttpRequester(this.context);
                String serviceURL = constructServiceURL();

                Iterable<ItemModel> items = requester.GetMany(serviceURL, ItemModel.class, this.sessionKey);

                return items;
            } catch (Exception e) {
                return null;
            }
        }

        private String constructServiceURL() {
            String serviceURL = context.getString(R.string.items_for_parent_url);
            if (this.parentID == NO_PARENT_ID) {
                serviceURL += NO_PARENT_URL;
            } else {
                serviceURL += HAS_PARENT_URL + parentID;
            }
            return serviceURL;
        }

        private void checkUserLogin() {
            if (this.sessionKey == null || this.sessionKey == "") {
                Intent intent = new Intent(this.context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        @Override
        protected void onPostExecute(Iterable<ItemModel> itemModels) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

            adapter.upDateEntries((ArrayList) itemModels);
        }
    }
}
