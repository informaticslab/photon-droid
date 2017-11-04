package gov.cdc.mmwrexpress;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

/**BaseActivity.java
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected ActionBarDrawerToggle mDrawerToggle;
    protected DrawerLayout mDrawerLayout;
    protected NavigationView mNavigationView;



    protected void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setHomeActionContentDescription(R.string.menu_button_description);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    protected void initNavigationDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        setupActionBarDrawerToogle();
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }

    /**
     * In case if you require to handle drawer open and close states
     */
    protected void setupActionBarDrawerToogle() {

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                //Snackbar.make(view, R.string.drawer_close, Snackbar.LENGTH_SHORT).show();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                //Snackbar.make(drawerView, R.string.drawer_open, Snackbar.LENGTH_SHORT).show();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);

    }

    protected void setupDrawerContent(final NavigationView navigationView) {
        //setting up selected item listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.isChecked()) mDrawerLayout.closeDrawers();
                        else {
                            if (menuItem.getItemId() == R.id.nav_articles_list_fragment) {
                                Intent intent = new Intent(getApplicationContext(), ArticleListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
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
                            if (menuItem.getItemId() == R.id.nav_settings_fragment) {
                                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            if (menuItem.getItemId() == R.id.nav_support_fragment) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:informaticslab@cdc.gov"));
                                intent.putExtra(Intent.EXTRA_SUBJECT, "App Support Request: MMWR Express for Android");
                                intent.putExtra(Intent.EXTRA_TEXT, "Application Name: MMWR Express\n"
                                        + "Application Version: " + getApplicationVersionName() + "\n"
                                        + "OS: Android " + Build.VERSION.RELEASE + "\n"
                                        + "Device Info: " + (Build.MODEL.toLowerCase().startsWith(Build.MANUFACTURER.toLowerCase()) ? Build.MODEL : Build.MANUFACTURER + "-" + Build.MODEL) + "\n"
                                        + "\nPlease provide as much detail as possible for your request:"
                                        + "\n");
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Snackbar.make(getCurrentFocus(), "No Email Application detected.", Snackbar.LENGTH_LONG).show();
                                }

                            }
                            mDrawerLayout.closeDrawers();
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void showSnackbar(int resId) {
        Snackbar.make(mNavigationView, resId, Snackbar.LENGTH_LONG).show();

    }
    public int getApplicationVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {} catch(Exception e){}
        return 0;
    }

    public String getApplicationVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {} catch(Exception e){}
        return "";
    }

    protected void setActionBarTitle(String title)
    {
        getSupportActionBar().setTitle(title);
    }

}