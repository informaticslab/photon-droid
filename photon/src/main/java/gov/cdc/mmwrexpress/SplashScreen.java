package gov.cdc.mmwrexpress;

import android.content.Intent;
import android.os.AsyncTask;
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
        setContentView(R.layout.activity_splash_screen);

        if(!AppManager.pref.getBoolean(MmwrPreferences.AGREED_TO_EULA, false))
            i = new Intent(getApplicationContext(), EulaActivity.class);
        else
            i = new Intent(getApplicationContext(), ArticleListActivity.class);

        new parsePreloadJsonTask().execute();




    }

    private class parsePreloadJsonTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

         if (!AppManager.pref.getBoolean(MmwrPreferences.PRELOAD_ARTICLES_LOADED, false)) {
                loadJsonArticlesFromAsset();
                AppManager.editor.putBoolean(MmwrPreferences.PRELOAD_ARTICLES_LOADED, true);
                AppManager.editor.commit();
           }
            else{
                try {
                    Thread.sleep(750);
                }
                catch (InterruptedException ie){
                   ie.printStackTrace();
                }
            }
            startActivity(i);
            finish();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        public void loadJsonArticlesFromAsset() {
            String json = null;
            String asset;
            asset = prodAsset;

            try {
                InputStream is = getAssets().open(asset);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                JsonArticleParser jsonArticleParser = new JsonArticleParser();
                jsonArticleParser.parseEmbeddedArticles(json);
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            return;
        }
    }
}
