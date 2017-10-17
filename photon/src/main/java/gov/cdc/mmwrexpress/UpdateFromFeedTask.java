package gov.cdc.mmwrexpress;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by jason on 3/10/16.
 */
public class UpdateFromFeedTask extends AsyncTask<Void, Void, Integer> {

    private static final String FEED_LINK = "https://prototype.cdc.gov/api/v2/resources/media/";
    private static final String RSS_FEED_ID= "338387";
    private static final String DEV_FEED_ID = "338384";
    private static final String RSS_FORMAT = ".rss?";
    private static final String FROM_DATE = "fromdatemodified=";
    private String fromDate;
    private boolean cancelled;
    private boolean silent;
    private HttpsURLConnection httpsURLConnection;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        cancelled = false;
        silent = false;
    }

    public InputStream getInputStream(String link) {
        try {
            URL url = new URL(link);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();

            if(httpsURLConnection.getResponseCode() >= 400) {
                return null;
            } else {
                return url.openConnection().getInputStream();
            }
        } catch (IOException e) {
            Log.w(Constants.RSS_SERVICE, "Exception while retrieving the input stream", e);
            return null;
        }
        finally {
            if(httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        while (!cancelled) {
            try {
                CdcRssParser parser = new CdcRssParser();
                boolean debug = false;

                // Check for production version and set debug to false. Don't want prod version
                // pointing to dev feed - ever.
                if(!BuildConfig.APPLICATION_ID.contains("development")){
                    debug = false;
                }

                String lastUpdate = AppManager.pref.getString(MmwrPreferences.LAST_UPDATE, "");

                InputStream inputStream = getInputStream(FEED_LINK +RSS_FEED_ID +RSS_FORMAT +FROM_DATE +lastUpdate);
                Log.d("UpdateFromFeedTask", "doInBackground: UpdateURL: " +FEED_LINK +DEV_FEED_ID +FROM_DATE +lastUpdate);
                if (inputStream != null) {
                    Log.d("UpdateFromFeedTask", "doInBackground: pulling articles after " +lastUpdate);
                    parser.parse(inputStream);
                    Date currDate = new Date();
                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(currDate);
                    Log.d("UpdateFromFeedTask", "doInBackground: currDate: " +currDate +" formatted: " +formattedDate);
                    AppManager.editor.putString(MmwrPreferences.LAST_UPDATE, formattedDate);
                    AppManager.editor.commit();
                    inputStream.close();
                    return 1;
                } else {
                    return 0;
                }
            } catch (XmlPullParserException e) {
                Log.w(e.getMessage(), e);
            } catch (IOException e) {
                Log.w(e.getMessage(), e);
            }
            return 3;
        }
        return 2;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        AppManager.issuesManager.onRefreshComplete(integer);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancelled = true;
    }
}
