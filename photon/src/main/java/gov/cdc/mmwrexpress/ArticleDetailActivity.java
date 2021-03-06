package gov.cdc.mmwrexpress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**ArticleDetailActivity.java
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 *
 * Using a ViewPager to display "blue box" content from MMWR Weekly articles.
 *
 * Activity shows a "next" and "previous" button that can advance the user to the next page of
 * content, as well as swiping.
 */

public class ArticleDetailActivity extends AppCompatActivity {

    // number of content pages
    private static final int NUM_PAGES = 2;

    //pager widget handles animation and swiping to other pages of content
    private ViewPager mPager;
    private String known;
    private String added;
    private String implications;
    private String title;
    private String pubDate;
    private int volume;
    private int number;
    private String link;
    private TabLayout tabLayout;
    private int defaultTab;

    // pager adapter provides pages view pager widget
    private PagerAdapter mPagerAdapter;
    private FragmentTabHost mTabHost;

    public static Intent newIntent(Context packageContext, String known, String added, String implications,
                                   String title, String date, int volume, int number, String link) {

        Intent intent = new Intent(packageContext, ArticleDetailActivity.class);
        intent.putExtra(Constants.ARTICLE_KNOWN_MSG, known);
        intent.putExtra(Constants.ARTICLE_ADDED_MSG, added);
        intent.putExtra(Constants.ARTICLE_IMPLICATIONS_MSG, implications);
        intent.putExtra("title", title);
        intent.putExtra("pubDate", date);
        intent.putExtra("volume", volume);
        intent.putExtra("number", number);
        intent.putExtra("link", link);
        return intent;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_details_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Get the message from the intent
        Intent intent = getIntent();
        known = intent.getStringExtra(Constants.ARTICLE_KNOWN_MSG);
        added = intent.getStringExtra(Constants.ARTICLE_ADDED_MSG);
        implications = intent.getStringExtra(Constants.ARTICLE_IMPLICATIONS_MSG);
        title = intent.getStringExtra("title");
        pubDate = intent.getStringExtra("pubDate");
        volume = intent.getIntExtra("volume", -1);
        number = intent.getIntExtra("number", -1);
        link = intent.getStringExtra("link");

        // Set toolbar title to Article title
        ab.setTitle(title);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tab_host);
        tabLayout.setupWithViewPager(mPager);

        defaultTab = AppManager.pref.getInt(MmwrPreferences.DEFAULT_TAB, Constants.FULL_ARTICLE_TAB);

        mPager.setCurrentItem(defaultTab);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
                //showSwipeHelpSnackbar(position);

                if (mPagerAdapter.getPageTitle(position) == "Full Article") {
                    AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_FULL, Constants.SC_SECTION_SUMMARY);
                } else {
                    AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_SUMMARY, Constants.SC_SECTION_SUMMARY);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPagerAdapter.getPageTitle(mPager.getCurrentItem()) == "Full Article") {
            AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_FULL, Constants.SC_SECTION_SUMMARY);
        } else {
            AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_SUMMARY, Constants.SC_SECTION_SUMMARY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.article_detail, menu);


        //menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "previous" or "finish" button to the action bar, depending on which page
        // is currently selected.
       /* MenuItem previous_item = menu.add(Menu.NONE, R.id.action_previous, Menu.NONE,
                (mPager.getCurrentItem() == 0)
                        ? R.string.action_finish
                        : R.string.action_previous);
        previous_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem next_item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        next_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);*/


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A pager adapter that represents the blue boxes in MMWR Weekly
     */
    private class ContentPagerAdapter extends FragmentPagerAdapter {
        private String [] tabTitles = new String [] {"Summary", "Full Article"};
        public ContentPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if (position == 0 ) {
                return ArticleSummaryFragment.create(known, added, implications);
            }
            if (position == 1 ) {
                return ArticleWebViewFragment.create(position, link);
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public void o(int pageNumber) {
            // Just define a callback method in your fragment and call it like this!

        }
    }

    private void share(){
        AppManager.sc.trackEvent(Constants.SC_EVENT_SHARE_BUTTON, Constants.SC_PAGE_TITLE_SUMMARY, Constants.SC_SECTION_SUMMARY);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "MMWR Weekly article via CDC's MMWR Express "
                + "mobile app.\n" +link);
        startActivity(shareIntent);
    }
}

