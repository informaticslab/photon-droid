package gov.cdc.mmwrexpress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by greg on 9/14/15.
 */
public class KeywordSearchActivity extends BaseActivity {

    private static final String TAG = "KeywordSearchActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_search);

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



}
