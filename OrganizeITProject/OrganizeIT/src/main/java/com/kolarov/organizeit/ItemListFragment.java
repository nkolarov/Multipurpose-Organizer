package com.kolarov.organizeit;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kolarov.organizeit.Models.ItemModel;
import com.kolarov.organizeit.dummy.DummyContent;

import java.util.ArrayList;

/**
 * A list fragment representing a list of Items. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ItemDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ItemListFragment extends ListFragment {

    private ListView listView = null;

    private ItemListAdapter mAdapter;

    final private int ITEM_TYPE_ID = 1;

    final private int ITEM_ELEMENT_ID = 2;

    final private int NO_PARENT_ID = -1;

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
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
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

        // TODO: replace with a real list adapter.
/*
        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                DummyContent.ITEMS));
*/
        this.mAdapter = new ItemListAdapter(this.mActivity);
        setListAdapter(this.mAdapter);

        LoadItems loadItemsTask = new LoadItems(this.mActivity, this.mAdapter, NO_PARENT_ID);
        loadItemsTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = (ListView) inflater.inflate(R.layout.item_fragment_layout, null);

        return listView;
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

        if (item.itemtype == ITEM_TYPE_ID){
            /*
                // TODO: Find a way to add current state to backstack.
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack("List" + position);
                ft.commit();
            */
            LoadItems loadItemsTask = new LoadItems(this.mActivity, this.mAdapter, (int)id);
            loadItemsTask.execute();
        } else{
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    public class LoadItems extends AsyncTask<Void, Void, Iterable<ItemModel>> {
        private ItemListAdapter adapter;
        private Context context;
        private String sessionKey;
        private int parentID;
        final private String NO_PARRENT_URL = "?parentID=null";
        final private String HAS_PARRENT_URL = "?parentID=";
        private ProgressDialog dialog;

        public LoadItems(Context context ,ItemListAdapter adapter, int parentID){
            this.adapter = adapter;
            this.context = context;
            this.parentID = parentID;
            this.sessionKey = new UserStatusManager(context).getSessionKey();
            this.dialog = new ProgressDialog(this.context);
        }

        @Override
        protected void onPreExecute() {
            // TODO i18n
            this.dialog.setMessage("Please wait.. Fetching data..");
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
            if (this.parentID == NO_PARENT_ID){
                serviceURL += NO_PARRENT_URL;
            }
            else {
                serviceURL += HAS_PARRENT_URL + parentID;
            }
            return serviceURL;
        }

        private void checkUserLogin() {
            if (this.sessionKey == null || this.sessionKey == ""){
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
