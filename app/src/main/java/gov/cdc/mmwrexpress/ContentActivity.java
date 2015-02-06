package gov.cdc.mmwrexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Using a ViewPager to display "blue box" content from MMWR Weekly articles.
 *
 * Activity shows a "next" and "previous" button that can advance the user to the next page of
 * content, as well as swiping.
 */
public class ContentActivity extends FragmentActivity {

     // number of content pages
    private static final int NUM_PAGES = 3;

    //pager widget handles animation and swiping to other pages of content
    private ViewPager mPager;
    private String known;
    private String added;
    private String implications;

    // pager adapter provides pages view pager widget
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // Get the message from the intent
        Intent intent = getIntent();
        known = intent.getStringExtra(Constants.ARTICLE_KNOWN_MSG);
        added = intent.getStringExtra(Constants.ARTICLE_ADDED_MSG);
        implications = intent.getStringExtra(Constants.ARTICLE_IMPLICATIONS_MSG);


        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_content, menu);

        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" to the article list activity
                NavUtils.navigateUpTo(this, new Intent(this, ArticleListActivity.class));
                return true;

            case R.id.action_previous:
                // go to the previous content page, if no previous step, setCurrentItem does nothing
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // go to  next content page, if no content then setCurrentItem will do nothing
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A pager adapter that represents the blue boxes in MMWR Weekly
     */
    private class ContentPagerAdapter extends FragmentStatePagerAdapter {
        public ContentPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if (position == 0 )
                return ContentPageFragment.create(position, "What is already known?", known);
            if (position == 1 )
                return ContentPageFragment.create(position, "What is added by this report?", added);
            if (position == 2 )
                return ContentPageFragment.create(position, "What are the implications for public health practice?", implications);

            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}

