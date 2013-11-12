package com.kolarov.organizeit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if (!isNetworkAvailable()){ // debug
        if (isNetworkAvailable()){
            String sessionKey = getSessionKey();
            if (sessionKey != null){ // debug enter in login activity
            //if (sessionKey == null || sessionKey == ""){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }

            setContentView(R.layout.activity_main);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new PlaceholderFragment())
                        .commit();
            }

            new LoginTask(MainActivity.this).execute();
        }else{
            showErrorDialogAndQuit();
        }
    }

    private String getSessionKey() {
        UserStatusManager usm = new UserStatusManager(this);
        return usm.getSessionKey();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    public void showErrorDialogAndQuit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog aDialog = new AlertDialog.Builder(MainActivity.this).setMessage(getString(R.string.no_internet_error_message)).setTitle(getString(R.string.error_dialog_title))
                        .setNeutralButton(getString(R.string.error_dialog_cancel_btn_title), new AlertDialog.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int which) {
                                // Exit the application.
                                finish();
                            }
                        }).create();
                aDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        // Disables the back button.
                        return true;
                    }

                });
                aDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }
    }

}
