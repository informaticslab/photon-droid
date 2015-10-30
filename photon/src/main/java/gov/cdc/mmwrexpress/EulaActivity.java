package gov.cdc.mmwrexpress;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

/**EulaActivity.java
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class EulaActivity extends AppCompatActivity {

    private Button mAgreeButton;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eula);

        mWebView = (WebView) findViewById(R.id.webview);

        mWebView.loadUrl("file:///android_asset/eula.html");

        mAgreeButton = (Button) findViewById(R.id.btnAgree);
        mAgreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppManager.editor.putBoolean(MmwrPreferences.AGREED_TO_EULA, true);
                AppManager.editor.putString(MmwrPreferences.APP_VERSION, getApplicationVersionName());
                AppManager.editor.commit();

                //Track event - Accepted EULA
                AppManager.sc.trackEvent(Constants.SC_EVENT_AGREE_TO_EULA, Constants.SC_PAGE_TITLE_EULA, Constants.SC_SECTION_EULA);

                Intent intent = new Intent(getApplicationContext(), ArticleListActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eula, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Track navigation event - EULA Page Launched
        AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_EULA, Constants.SC_SECTION_EULA);
    }

    public String getApplicationVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {} catch(Exception e){}
        return "";
    }
}
