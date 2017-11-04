package gov.cdc.mmwrexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

/**ArticleListActivity.java
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class ArticleListActivity extends BaseActivity {

    private static final String TAG = "ArticleListActivity";
    private ArticleListFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.article_list_activity);

        // navigation view setup
        setupToolbar();
        initNavigationDrawer();

        if (savedInstanceState == null) {
            addArticleListFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!AppManager.pref.getBoolean(MmwrPreferences.DISPLAY_NAV_DRAWER_ON_FIRST_LAUNCH, false)) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            AppManager.editor.putBoolean(MmwrPreferences.DISPLAY_NAV_DRAWER_ON_FIRST_LAUNCH, true).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                share();
                return true;
            case R.id.action_search:
                Intent i = new Intent(this, KeywordSearchActivity.class);
                startActivity(i);
                return true;
            case R.id.action_refresh:
                fragment.forceRefresh();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationView.setCheckedItem(R.id.nav_articles_list_fragment);
    }

    private void share() {
        AppManager.sc.trackEvent(Constants.SC_EVENT_SHARE_BUTTON, Constants.SC_PAGE_TITLE_LIST, Constants.SC_SECTION_ARTICLES);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm using CDC's MMWR Express mobile app. "
                + "Learn more about it here: \nhttps://www.cdc.gov/mmwr/mmwr_expresspage.html");
        startActivity(shareIntent);
    }

    private void addArticleListFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = new ArticleListFragment();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fragment_added", true);
    }

    @Override
    protected void onStop() {
        mDrawerLayout.closeDrawers();
        super.onStop();
    }
}


