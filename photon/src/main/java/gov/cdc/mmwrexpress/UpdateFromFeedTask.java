package gov.cdc.mmwrexpress;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by jason on 3/10/16.
 */
public class UpdateFromFeedTask extends AsyncTask<Void, Void, Integer> {
    private static final String PROD_FEED_LINK = "https://tools.cdc.gov/api/v2/resources/media/";
    private static final String DEV_FEED_LINK = "https://prototype.cdc.gov/api/v2/resources/media/";
    private static final String PROD_FEED_ID = "342419";
    private static final String DEV_FEED_ID= "338387";
    private static final String TEST_FEED_ID = "338384";
    private static final String RSS_FORMAT = ".rss?";
    private static final String FROM_DATE = "fromdatemodified=";
    private String fromDate;
    private boolean cancelled;
    private boolean silent;
    private HttpsURLConnection httpsURLConnection;
    private InputStream inputStream;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        cancelled = false;
        silent = false;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        while (!cancelled) {
            try {
                CdcRssParser parser = new CdcRssParser();

                String lastUpdate = AppManager.pref.getString(MmwrPreferences.LAST_UPDATE, "");

                //Modify this url to point to different feed
                URL url = new URL(PROD_FEED_LINK +PROD_FEED_ID +RSS_FORMAT +FROM_DATE +lastUpdate);

                httpsURLConnection = (HttpsURLConnection) url.openConnection();

                if(httpsURLConnection.getResponseCode() >= 400) {
                    return 0;
                }

                inputStream = url.openConnection().getInputStream();
                Log.d("UpdateFromFeedTask", "doInBackground: UpdateURL: " +url);
                if (inputStream != null) {
                    Log.d("UpdateFromFeedTask", "doInBackground: pulling articles after " +lastUpdate);
                    parser.parse(inputStream);
                    Date currDate = new Date();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    df.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String formattedDate = df.format(currDate);
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
