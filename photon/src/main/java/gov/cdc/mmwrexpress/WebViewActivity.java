package gov.cdc.mmwrexpress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by greg on 9/18/15.
 */
public class WebViewActivity extends BaseActivity {


    public static final String WEB_VIEW_PAGE = "gov.cdc.mmwrexpress.WEB_VIEW_PAGE";

    private String mWebPage;
    private WebView mWebView;
    private String toolbarTitle = "";

    public static Intent newIntent(Context packageContext, String webPage) {

        Intent intent = new Intent(packageContext, WebViewActivity.class);
        intent.putExtra(WEB_VIEW_PAGE, webPage);
        return intent;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // navigationview setup
        setupToolbar();
        initNavigationDrawer();

        // Get the message from the intent
        Intent intent = getIntent();
        mWebPage = intent.getStringExtra(WEB_VIEW_PAGE);
        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        toolbarTitle = intent.getStringExtra("toolbarTitle");
        setActionBarTitle(toolbarTitle);

        mWebView.loadUrl("file:///android_asset/" + mWebPage);


    }

    @Override
    protected void onResume() {
        if(toolbarTitle.equals("Help"))
                mNavigationView.setCheckedItem(R.id.nav_help_fragment);
        else if(toolbarTitle.equals("About"))
                mNavigationView.setCheckedItem(R.id.nav_about_fragment);
        else if(toolbarTitle.equals("User License Agreement"))
                mNavigationView.setCheckedItem(R.id.nav_eula_fragment);

        super.onResume();
    }

    public class WebAppInterface{
        Context context;

        WebAppInterface(Context c){
            context = c;
        }
        String version = getApplicationVersionName();

        @JavascriptInterface
        public String getVersion(){
            return version;
        }
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
                            Intent intent = new Intent(getApplicationContext(), ArticleListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        if (menuItem.getItemId() == R.id.nav_search_fragment) {
                            Intent intent = new Intent(getApplicationContext(), KeywordSearchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if (menuItem.getItemId() == R.id.nav_help_fragment) {
                            if(toolbarTitle.equals("Help"))
                            {
                                mDrawerLayout.closeDrawers();
                            }
                            else {
                                Intent intent = WebViewActivity.newIntent(getApplicationContext(), "help.html");
                                intent.putExtra("toolbarTitle", "Help");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                        if (menuItem.getItemId() == R.id.nav_eula_fragment) {
                            if(toolbarTitle.equals("User License Agreement"))
                            {
                                mDrawerLayout.closeDrawers();
                            }
                            else {
                                Intent intent = WebViewActivity.newIntent(getApplicationContext(), "eula.html");
                                intent.putExtra("toolbarTitle", "User License Agreement");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                        if (menuItem.getItemId() == R.id.nav_about_fragment) {
                            if(toolbarTitle.equals("About"))
                            {
                                mDrawerLayout.closeDrawers();
                            }
                            else {
                                Intent intent = WebViewActivity.newIntent(getApplicationContext(), "about.html");
                                intent.putExtra("toolbarTitle", "About");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}
