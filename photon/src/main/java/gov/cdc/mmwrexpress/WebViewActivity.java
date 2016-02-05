package gov.cdc.mmwrexpress;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**WebViewActivity.java
 * photon-droid
 *
 * Created by greg on 9/18/15.
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class WebViewActivity extends BaseActivity {


    public static final String WEB_VIEW_PAGE = "gov.cdc.mmwrexpress.WEB_VIEW_PAGE";

    private String mWebPage;
    private WebView mWebView;
    private String toolbarTitle = "";
    private ProgressBar progressBar;

    public static Intent newIntent(Context packageContext, String webPage) {

        Intent intent = new Intent(packageContext, WebViewActivity.class);
        intent.putExtra(WEB_VIEW_PAGE, webPage);
        return intent;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // navigationview setup
        setupToolbar();
        initNavigationDrawer();

        // Get the message from the intent
        Intent intent = getIntent();
        mWebPage = intent.getStringExtra(WEB_VIEW_PAGE);
        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        toolbarTitle = intent.getStringExtra("toolbarTitle");
        setActionBarTitle(toolbarTitle);

        mWebView.loadUrl("file:///android_asset/" + mWebPage);

    }

    @Override
    protected void onStart() {


        if(toolbarTitle.equals("Help"))
            AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_HELP, Constants.SC_SECTION_HELP);
        else if(toolbarTitle.equals("About"))
            AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_ABOUT, Constants.SC_SECTION_ABOUT);
        else if(toolbarTitle.equals("User License Agreement"))
            AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_EULA, Constants.SC_SECTION_EULA);
        super.onStart();

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
}

