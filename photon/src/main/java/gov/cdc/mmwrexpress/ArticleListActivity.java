package gov.cdc.mmwrexpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

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
    }

    private void testPersistence() {
        //IssuesManager issueMgr = new IssuesManager();
        //issueMgr.storeTest();

        Log.d(TAG, "Done with persistence tests.");
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

    @Override
    protected void onResume() {
        mNavigationView.setCheckedItem(R.id.nav_articles_list_fragment);
        super.onResume();
    }

    @Override
    protected void setupDrawerContent(final NavigationView navigationView) {
        //setting up selected item listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        if (menuItem.getItemId() == R.id.nav_articles_list_fragment) {
                            mDrawerLayout.closeDrawers();
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
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
    @Override
    protected void onStop(){
        mDrawerLayout.closeDrawers();
        super.onStop();
    }
}

