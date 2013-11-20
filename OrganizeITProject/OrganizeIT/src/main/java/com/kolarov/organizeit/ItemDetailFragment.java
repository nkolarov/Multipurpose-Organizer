package com.kolarov.organizeit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kolarov.organizeit.Models.ItemDetailsModel;
import com.kolarov.organizeit.Models.LocationModel;
import com.kolarov.organizeit.Tasks.SaveItemLocationTask;

/**
 * A fragment representing a single Item detail screen.
 */
public class ItemDetailFragment extends Fragment {

    private int mItemId;

    private View mRootView;

    private MyLocation myLocation;

    private double mLatitude;

    private double mLongitude;

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

        Button setLocation = (Button) rootView.findViewById(R.id.buttonSetLocation);
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSetLocationButton((Button) view);
            }
        });

        Button viewLocationOnMap = (Button) rootView.findViewById(R.id.buttonViewOnMap);
        viewLocationOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleViewLocationOnMapButton((Button) view);
            }
        });
    }

    private void handleViewLocationOnMapButton(Button view) {
        if (this.mLongitude != 0 && this.mLatitude != 0) {
            showMap(this.mLatitude, this.mLongitude);
        }
    }

    private void showMap(double latitude, double longitude) {
        String uriString = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude;
        if (isUriAvailable(getActivity(), uriString)) {
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.maps_not_found_message), 10).show();
        }
    }

    public static boolean isUriAvailable(Context context, String uri) {
        Intent test = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        return context.getPackageManager().resolveActivity(test, 0) != null;
    }

    private void handleSetLocationButton(Button view) {
        myLocation = new MyLocation(this.getActivity());

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(final Location location) {
                if (location != null) {
                    setupLocationRelatedViews(location);
                    saveItemLocation(location);
                }
            }

            ;
        };

        myLocation.getLocation(this.getActivity(), locationResult);
    }

    private void saveItemLocation(Location location) {
        LocationModel locationModel = new LocationModel();
        locationModel.latitude = location.getLatitude();
        locationModel.longitude = location.getLongitude();
        SaveItemLocationTask saveTask = new SaveItemLocationTask(getActivity(), locationModel, this.mItemId);
        saveTask.execute();
    }

    private void setupLocationRelatedViews(Location location) {
        this.mLatitude = location.getLatitude();
        TextView textViewLatitude = (TextView) getActivity().findViewById(R.id.textViewLatitudeValue);
        textViewLatitude.setText(String.valueOf(location.getLatitude()));

        this.mLongitude = location.getLongitude();
        TextView textViewLongitude = (TextView) getActivity().findViewById(R.id.textViewLongitudeValue);
        textViewLongitude.setText(String.valueOf(location.getLongitude()));

        Button showOnMapButton = (Button) getActivity().findViewById(R.id.buttonViewOnMap);
        showOnMapButton.setVisibility(View.VISIBLE);
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

    /**
     * A task that gets the item details data from a web service.
     */
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
            this.dialog.setMessage(mActivity.getString(R.string.loading_details_message));
            this.dialog.show();
        }

        @Override
        protected ItemDetailsModel doInBackground(Void... params) {
            try {
                HttpRequester requester = new HttpRequester(this.context);
                String serviceURL = mActivity.getString(R.string.load_tem_details_Service_url) + this.mElementId;
                ItemDetailsModel model = requester.Get(serviceURL, ItemDetailsModel.class, this.mSessionKey);

                return model;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ItemDetailsModel itemModel) {
            if (itemModel != null) {
                setupViewData(itemModel);
            }

            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }

        private void setupViewData(ItemDetailsModel itemModel) {
            TextView textViewTitle = (TextView) this.mRootView.findViewById(R.id.textViewItemTitle);
            textViewTitle.setText(itemModel.title);

            TextView textViewNotes = (TextView) this.mRootView.findViewById(R.id.textViewItemNotes);
            textViewNotes.setText(itemModel.notes);

            TextView textViewLatitude = (TextView) this.mRootView.findViewById(R.id.textViewLatitudeValue);
            textViewLatitude.setText(itemModel.location != null ? String.valueOf(itemModel.location.latitude) : "Not set");
            if (itemModel.location != null) {
                mLongitude = itemModel.location.longitude;
                mLatitude = itemModel.location.latitude;
            }

            TextView textViewLongitude = (TextView) this.mRootView.findViewById(R.id.textViewLongitudeValue);
            textViewLongitude.setText(itemModel.location != null ? String.valueOf(itemModel.location.longitude) : "Not set");

            if (itemModel.location != null) {
                Button showOnMapButton = (Button) this.mRootView.findViewById(R.id.buttonViewOnMap);
                showOnMapButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
