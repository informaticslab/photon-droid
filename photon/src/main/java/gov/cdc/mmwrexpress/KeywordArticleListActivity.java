package gov.cdc.mmwrexpress;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**KeywordArticleListActivity.java
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class KeywordArticleListActivity extends AppCompatActivity {

    private static final String TAG = "KeywordArticleListActivity";
    private static final String KEYWORD_TEXT = "KEYWORD_TEXT";

    private String mKeywordText;

    public static Intent newIntent(Context packageContext, String keywordText) {

        Intent intent = new Intent(packageContext, KeywordArticleListActivity.class);
        intent.putExtra(KEYWORD_TEXT, keywordText);
        return intent;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_article_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // get the message from the intent
        Intent intent = getIntent();
        mKeywordText = intent.getStringExtra(KEYWORD_TEXT);
        getSupportActionBar().setTitle(mKeywordText);


        if (savedInstanceState == null) {
            addKeywordArticleListFragment();
        }

    }

    private void addKeywordArticleListFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        KeywordArticleListFragment fragment = KeywordArticleListFragment.create(mKeywordText);
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fragment_added", true);
    }
}
