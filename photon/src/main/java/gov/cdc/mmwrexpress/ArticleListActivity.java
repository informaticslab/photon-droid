package gov.cdc.mmwrexpress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class ArticleListActivity extends BaseActivity {

    private static final String TAG = "ArticleListActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // navigation view setup
        setupToolbar();
        initNavigationDrawer();

        startGcmRegistration();

        loadJsonArticlesFromAsset();

        if (savedInstanceState == null) {
            addArticleListFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MmwrPreferences.REGISTRATION_COMPLETE));
        mNavigationView.setCheckedItem(R.id.nav_articles_list_fragment);

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private void testPersistence() {
        //IssuesManager issueMgr = new IssuesManager();
        //issueMgr.storeTest();

        Log.d(TAG, "Done with persistence tests.");
    }

    private void addArticleListFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ArticleListFragment fragment = new ArticleListFragment();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fragment_added", true);
    }

    public void loadJsonArticlesFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("PreloadIssues.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Realm realm = Realm.getInstance(this);
            JsonArticleParser jsonArticleParser = new JsonArticleParser(realm);
            jsonArticleParser.parseEmbeddedArticles(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        return;
    }


    @Override
    protected void setupDrawerContent(final NavigationView navigationView) {
        //setting up selected item listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        if (menuItem.getItemId() == R.id.nav_articles_list_fragment) {
                            mDrawerLayout.closeDrawers();
                        }

                        if (menuItem.getItemId() == R.id.nav_search_fragment) {
                            Intent intent = new Intent(getApplicationContext(), KeywordSearchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if (menuItem.getItemId() == R.id.nav_help_fragment) {
                            Intent intent = WebViewActivity.newIntent(getApplicationContext(), "help.html");
                            intent.putExtra("toolbarTitle", "Help");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if (menuItem.getItemId() == R.id.nav_eula_fragment) {
                            Intent intent = WebViewActivity.newIntent(getApplicationContext(), "eula.html");
                            intent.putExtra("toolbarTitle", "User License Agreement");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if (menuItem.getItemId() == R.id.nav_about_fragment) {
                            Intent intent = WebViewActivity.newIntent(getApplicationContext(), "about.html");
                            intent.putExtra("toolbarTitle", "About");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    protected void onStop() {
        mDrawerLayout.closeDrawers();
        super.onStop();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("GCM Registration", "This device is not supported.");
//                finish();
            }
            return false;
        }
        return true;
    }


    protected void startGcmRegistration() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences;
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(MmwrPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    //mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        //mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        if (checkPlayServices()) {
            /* Start IntentService to register this application with GCM. */
            Intent intent = new Intent(this, gov.cdc.mmwrexpress.RegistrationIntentService.class);
            startService(intent);
        }

    }
}

