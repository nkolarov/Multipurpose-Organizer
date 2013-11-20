package com.kolarov.organizeit;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by N.Kolarov on 13-11-18.
 * A class helping to get the current device location.
 */
public class MyLocation {
    LocationManager mLocationManager;
    LocationResult mLocationResult;
    boolean mGps_enabled = false;
    boolean mNetwork_enabled = false;

    AsyncTask<Context, Void, Void> mTask;

    public boolean getLocation(Context context, LocationResult result) {
        this.mLocationResult = result;

        if (this.mLocationManager == null) {
            this.mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        // Check if providers are permitted
        try {
            this.mGps_enabled = this.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            this.mNetwork_enabled = this.mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!this.mGps_enabled && !this.mNetwork_enabled) {
            return false;
        }

        if (this.mGps_enabled) {
            this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListenerGps);
        }

        if (this.mNetwork_enabled) {
            this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.locationListenerNetwork);
        }

        this.mTask = new GetLastLocation();
        this.mTask.execute();

        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            mTask.cancel(true);
            mLocationResult.gotLocation(location);
            mLocationManager.removeUpdates(this);
            mLocationManager.removeUpdates(locationListenerNetwork);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            mTask.cancel(true);
            mLocationResult.gotLocation(location);
            mLocationManager.removeUpdates(this);
            mLocationManager.removeUpdates(locationListenerGps);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private Context mContext;

    public MyLocation(Context c) {
        this.mContext = c;
    }

    /**
     *  A task that gets the the current device location.
     */
    public class GetLastLocation extends AsyncTask<Context, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(mContext);

        protected void onPreExecute() {
            dialog.setMessage("Searching...");
            dialog.show();
        }

        protected Void doInBackground(Context... params) {
            Handler mHandler = new Handler(Looper.getMainLooper());

            mHandler.post(new Runnable() {
                public void run() {
                    mLocationManager.removeUpdates(locationListenerGps);
                    mLocationManager.removeUpdates(locationListenerNetwork);

                    Location net_loc = null;
                    Location gps_loc = null;
                    if (mGps_enabled) {
                        gps_loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }

                    if (mNetwork_enabled) {
                        net_loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }

                    //if there are both values use the latest one
                    if (gps_loc != null && net_loc != null) {
                        if (gps_loc.getTime() > net_loc.getTime()) {
                            mLocationResult.gotLocation(gps_loc);
                        } else {
                            mLocationResult.gotLocation(net_loc);
                        }
                        return;
                    }

                    if (gps_loc != null) {
                        mLocationResult.gotLocation(gps_loc);
                        return;
                    }

                    if (net_loc != null) {
                        mLocationResult.gotLocation(net_loc);
                        return;
                    }

                    mLocationResult.gotLocation(null);
                }
            });

            return null;
        }

        protected void onPostExecute(final Void unused) {
            dialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }
}
