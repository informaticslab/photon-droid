package gov.cdc.mmwrexpress;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ArticleListActivity extends FragmentActivity implements ArticleListFragment.OnArticleSelectedListener {

    TextView mRssFeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (savedInstanceState == null) {
            addArticleListFragment();
        }

    }

    private void testPersistence() {
        //IssuesManager issueMgr = new IssuesManager();
        //issueMgr.storeTest();

        Log.d(Constants.ARTICLE_LIST_ACTIVITY,"Done with persistence tests.");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onArticleSelected(String known, String added, String implications) {

        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra(Constants.ARTICLE_KNOWN_MSG, known);
        intent.putExtra(Constants.ARTICLE_ADDED_MSG, added);
        intent.putExtra(Constants.ARTICLE_IMPLICATIONS_MSG, implications);

        startActivity(intent);

    }

}

