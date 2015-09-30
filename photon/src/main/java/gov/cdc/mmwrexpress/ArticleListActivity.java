package gov.cdc.mmwrexpress;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;

public class ArticleListActivity extends BaseActivity {

    private static final String TAG = "ArticleListActivity";
    public static final String PREFS_NAME = "CdcMmwrExpressPrefsFile";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // navigationview setup
        setupToolbar();
        initNavigationDrawer();

        loadJsonArticlesFromAsset();

        if (savedInstanceState == null) {
            addArticleListFragment();
        }

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean agreedToEula = settings.getBoolean("agreedToEula", false);
        if (agreedToEula == false)
            addDisplayEulaFragment();




    }

    private void testPersistence() {
        //IssuesManager issueMgr = new IssuesManager();
        //issueMgr.storeTest();

        Log.d(TAG, "Done with persistence tests.");
    }

    private void addDisplayEulaFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        EulaFragment fragment = new EulaFragment();
        transaction.replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
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

    public void loadJsonArticlesFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("PreloadIssues.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Realm realm = Realm.getInstance(this);
            JsonArticleParser jsonArticleParser = new JsonArticleParser(realm);
            jsonArticleParser.parseEmbeddedArticles(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        return;
    }



}

