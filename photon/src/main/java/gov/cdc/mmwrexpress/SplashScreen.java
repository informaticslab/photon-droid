package gov.cdc.mmwrexpress;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;
import java.io.InputStream;

/**SplashScreen.java
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class SplashScreen extends AppCompatActivity {
    private Intent i;
    private String prodAsset = "PreloadIssues.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadArticlesFromPreload();

                if (!AppManager.pref.getBoolean(MmwrPreferences.AGREED_TO_EULA, false))
                    i = new Intent(getApplicationContext(), EulaActivity.class);
                else
                    i = new Intent(getApplicationContext(), ArticleListActivity.class);

                startActivity(i);
                finish();
            }
        }, 750);

    }

    private void loadArticlesFromPreload() {
        if (!AppManager.pref.getBoolean(MmwrPreferences.PRELOAD_ARTICLES_LOADED, false)) {
            String json = null;
            String prodAsset = "PreloadIssues.json";

            try {
                InputStream is = getAssets().open(prodAsset);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                JsonArticleParser jsonArticleParser = new JsonArticleParser();
                jsonArticleParser.parseEmbeddedArticles(json);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                AppManager.editor.putBoolean(MmwrPreferences.PRELOAD_ARTICLES_LOADED, true).commit();
            }
        }
    }

}