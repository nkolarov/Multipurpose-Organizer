package com.kolarov.organizeit;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNetworkAvailable()) {

            checkUserSession();
            setContentView(R.layout.activity_main);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new PlaceholderFragment())
                        .commit();
            }

        } else {
            showErrorDialogAndQuit();
        }
    }

    private void checkUserSession() {
        String sessionKey = getSessionKey();
        if (sessionKey == null || sessionKey == "") {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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

    /**
     * A placeholder fragment containing a the main view.
     */
    public static class PlaceholderFragment extends Fragment {
        private Activity mActivity;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            setupButtonHandlers(rootView);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;
        }

        private void setupButtonHandlers(View rootView) {
            Button logoutButton = (Button) rootView.findViewById(R.id.buttonLogout);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleLogoutButton((Button) view);
                }
            });

            Button browseItems = (Button) rootView.findViewById(R.id.buttonBrowseItems);
            browseItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleBrowseButton((Button) view);
                }
            });

            Button viewAbout = (Button) rootView.findViewById(R.id.buttonViewAbout);
            viewAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleAboutButton((Button) view);
                }
            });
        }

        private void handleAboutButton(Button view) {
            Intent intent = new Intent(mActivity, AboutActivity.class);
            startActivity(intent);
        }

        private void handleBrowseButton(Button view) {
            Intent intent = new Intent(mActivity, ItemListActivity.class);
            startActivity(intent);
        }

        private void handleLogoutButton(Button view) {
            UserStatusManager usm = new UserStatusManager(this.mActivity);
            usm.clearSessionKey();
            Intent intent = new Intent(mActivity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}
