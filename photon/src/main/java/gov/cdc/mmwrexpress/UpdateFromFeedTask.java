package gov.cdc.mmwrexpress;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jason on 3/10/16.
 */
public class UpdateFromFeedTask extends AsyncTask<Void, Void, Integer> {

    private static final String RSS_LINK = "http://t.cdc.gov/feed.aspx?";
    private static final String RSS_FEED_ID= "feedid=100";
    private static final String DEV_FEED_ID = "feedid=105";
    private static final String RSS_FORMAT = "format=rss2";
    private String fromDate;
    private boolean cancelled;
    private boolean silent;
    private HttpURLConnection httpURLConnection;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        cancelled = false;
        silent = false;
    }

    public InputStream getInputStream(String link) {
        try {
            URL url = new URL(link);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            if(httpURLConnection.getResponseCode() == 200 && httpURLConnection.getURL().equals(url)) {
                return url.openConnection().getInputStream();
            }
            else
                return null;
        } catch (IOException e) {
            Log.w(Constants.RSS_SERVICE, "Exception while retrieving the input stream", e);
            return null;
        }
        finally {
            httpURLConnection.disconnect();
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

                //Uncomment fromDate to pull by date.
                InputStream inputStream = getInputStream(RSS_LINK + "&" + (debug ? DEV_FEED_ID : RSS_FEED_ID) /*+"&" +fromDate*/ + "&" + RSS_FORMAT);
                if (inputStream != null) {
                    parser.parse(inputStream);
                    Date currDate = new Date();
                    AppManager.editor.putString(MmwrPreferences.LAST_UPDATE, new SimpleDateFormat("yyyy-MM-dd").format(currDate));
                    AppManager.editor.commit();
                    inputStream.close();
                } else
                    return 0;
            } catch (XmlPullParserException e) {
                Log.w(e.getMessage(), e);
            } catch (IOException e) {
                Log.w(e.getMessage(), e);

            }
            return 1;
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
