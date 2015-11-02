package gov.cdc.mmwrexpress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.PushManager;

public class ArticleListActivity extends BaseActivity {

    private static final String TAG = "ArticleListActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // navigation view setup
        setupToolbar();
        initNavigationDrawer();

        //Register receivers for push notifications
        registerReceivers();

        //Create and start push manager
        PushManager pushManager = PushManager.getInstance(this);

        //Start push manager, this will count app open for Pushwoosh stats as well
        try {
            pushManager.onStartup(this);
        }
        catch(Exception e)
        {
            //push notifications are not available or AndroidManifest.xml is not configured properly
        }

        //Register for push!
        pushManager.registerForPushNotifications();

        checkMessage(getIntent());


        if (savedInstanceState == null) {
            addArticleListFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(AppManager.pref.getBoolean(MmwrPreferences.FIRST_LAUNCH, true)){
            mDrawerLayout.openDrawer(Gravity.LEFT);
            AppManager.editor.putBoolean(MmwrPreferences.FIRST_LAUNCH, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
        mNavigationView.setCheckedItem(R.id.nav_articles_list_fragment);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceivers();
    }


    private void testPersistence() {
        //IssuesManager issueMgr = new IssuesManager();
        //issueMgr.storeTest();

        Log.d(TAG, "Done with persistence tests.");
    }

    private void share(){
        AppManager.sc.trackEvent(Constants.SC_EVENT_SHARE_BUTTON, Constants.SC_PAGE_TITLE_LIST, Constants.SC_SECTION_ARTICLES);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm using CDC's MMWR Express mobile app. "
                + "Learn more about it here: \nhttp://www.cdc.gov/mmwr/mmwr_expresspage.html");
        startActivity(shareIntent);
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

    //Registration receiver
    BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver()
    {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent)
        {
            checkMessage(intent);
        }
    };

    //Push message receiver
    private BroadcastReceiver mReceiver = new BasePushMessageReceiver()
    {
        @Override
        protected void onMessageReceive(Intent intent)
        {
            //JSON_DATA_KEY contains JSON payload of push notification.
            showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
        }
    };

    //Registration of the receivers
    public void registerReceivers()
    {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");

        registerReceiver(mReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE", null);

        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers()
    {
        //Unregister receivers on pause
        try
        {
            unregisterReceiver(mReceiver);
        }
        catch (Exception e)
        {
            // pass.
        }

        try
        {
            unregisterReceiver(mBroadcastReceiver);
        }
        catch (Exception e)
        {
            //pass through
        }
    }
    private void checkMessage(Intent intent)
    {
        if (null != intent)
        {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
            {
                showMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
            }
            else if (intent.hasExtra(PushManager.REGISTER_EVENT))
            {
                showMessage("register");
            }
            else if (intent.hasExtra(PushManager.UNREGISTER_EVENT))
            {
                showMessage("unregister");
            }
            else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
            {
                showMessage("register error");
            }
            else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
            {
                showMessage("unregister error");
            }

            resetIntentValues();
        }
    }

    /**
     * Will check main Activity intent and if it contains any PushWoosh data, will clear it
     */
    private void resetIntentValues()
    {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    private void showMessage(String message)
    {
        Snackbar.make(findViewById(R.id.main_content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);

        checkMessage(intent);
    }
}

