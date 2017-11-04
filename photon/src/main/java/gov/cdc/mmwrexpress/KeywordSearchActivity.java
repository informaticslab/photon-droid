package gov.cdc.mmwrexpress;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**KeywordSearchActivity.java
 * photon-droid
 *
 * Created by greg on 9/14/15.
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class KeywordSearchActivity extends BaseActivity {

    private static final String TAG = "KeywordSearchActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyword_search_activity);

        // navigationview setup
        setupToolbar();
        initNavigationDrawer();


        if (savedInstanceState == null) {
            addKeywordSearchFragment();
        }

    }


    private void addKeywordSearchFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        KeywordSearchFragment fragment = new KeywordSearchFragment();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
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
            //boolean values for tracking drawer state
            boolean drawerOpen = false;
            boolean drawerClosed = true;
            boolean toggleKeyboard = false;

            //Access Input service to get keyboard state
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                //Snackbar.make(view, R.string.drawer_close, Snackbar.LENGTH_SHORT).show();

                drawerClosed = true;
                drawerOpen = false;

                if(imm.isAcceptingText())
                    toggleKeyboard = true;
                else
                    toggleKeyboard = false;

            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                //Snackbar.make(drawerView, R.string.drawer_open, Snackbar.LENGTH_SHORT).show();
                drawerOpen = true;
                drawerClosed = false;

                if(imm.isAcceptingText())
                    toggleKeyboard = true;
                else
                    toggleKeyboard = false;
            }

            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                if(imm.isAcceptingText())
                {
                    toggleKeyboard = true;
                }

                //hide keyboard when drawer slides open
                if(drawerClosed && toggleKeyboard)
                {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    toggleKeyboard = true;
                }

                //if keyboard was previously visible, show keyboard when the drawer slides closed
                else if(drawerOpen && toggleKeyboard) {
                    imm.showSoftInput(getCurrentFocus(), 0);
                    toggleKeyboard = false;
                }
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onResume() {
        mNavigationView.setCheckedItem(R.id.nav_search_fragment);
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDrawerLayout.removeDrawerListener(mDrawerToggle);
    }
}
