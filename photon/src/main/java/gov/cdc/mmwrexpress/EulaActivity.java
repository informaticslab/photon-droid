package gov.cdc.mmwrexpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

public class EulaActivity extends AppCompatActivity {

    private Button mAgreeButton;
    private WebView mWebView;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eula);

        pref = getApplicationContext().getSharedPreferences(MmwrPreferences.PREFS_NAME, 0);

        //If EULA was already accepted, launch Article List
        if(pref.getBoolean(MmwrPreferences.AGREED_TO_EULA, false) == true)
        {
            Intent intent = new Intent(getApplicationContext(), ArticleListActivity.class);
            startActivity(intent);
            finish();
        }


        mWebView = (WebView) findViewById(R.id.webview);

        mWebView.loadUrl("file:///android_asset/eula.html");

        mAgreeButton = (Button) findViewById(R.id.btnAgree);
        mAgreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(MmwrPreferences.AGREED_TO_EULA, true);
                editor.putString(MmwrPreferences.APP_VERSION, getApplicationVersionName());
                editor.commit();
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

    public String getApplicationVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {} catch(Exception e){}
        return "";
    }
}
