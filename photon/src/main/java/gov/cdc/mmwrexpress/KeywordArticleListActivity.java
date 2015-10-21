package gov.cdc.mmwrexpress;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class KeywordArticleListActivity extends BaseActivity {

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

        // navigation view setup
        setupToolbar();
        initNavigationDrawer();

        // get the message from the intent
        Intent intent = getIntent();
        mKeywordText = intent.getStringExtra(KEYWORD_TEXT);
        setActionBarTitle(mKeywordText);


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

    @Override
    protected void onResume() {
        super.onResume();
        for(int i = 0; i < mNavigationView.getMenu().size(); i++){
            if(mNavigationView.getMenu().getItem(i).isChecked()){
                mNavigationView.getMenu().getItem(i).setChecked(false);
            }
        }
    }
}
