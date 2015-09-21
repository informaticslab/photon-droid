package gov.cdc.mmwrexpress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.webkit.WebView;

/**
 * Created by greg on 9/18/15.
 */
public class WebViewActivity extends BaseActivity {


    public static final String WEB_VIEW_PAGE = "gov.cdc.mmwrexpress.WEB_VIEW_PAGE";

    private String mWebPage;
    private WebView mWebView;

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

        mWebView.loadUrl("file:///android_asset/" + mWebPage);


    }


}
