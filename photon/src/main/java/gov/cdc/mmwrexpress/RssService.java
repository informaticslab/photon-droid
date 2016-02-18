package gov.cdc.mmwrexpress;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**RssService.java
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class RssService extends IntentService {

    private static final String RSS_LINK = "http://t.cdc.gov/feed.aspx?";
    private static final String RSS_FEED_ID= "feedid=100";
    private static final String DEV_FEED_ID = "feedid=105";
    private static final String RSS_FORMAT = "format=rss2";
    private String fromDate;
    public static final String ITEMS = "items";
    public static final String RECEIVER = "receiver";
    private Context ctx;
    private ResultReceiver receiver;

    public RssService() {
        super("RssService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fromDate = AppManager.pref.getString(MmwrPreferences.LAST_UPDATE, "2015-05-31");
        //Log.d("RSS: ", fromDate);
        Log.d(Constants.RSS_SERVICE, "Service started");
        ctx = this;
        receiver = intent.getParcelableExtra(RECEIVER);
        List<ArticleListItem> articleListItems = null;
        try {
            CdcRssParser parser = new CdcRssParser();

            boolean testFeed = true;

            //Uncomment fromDate to pull by date.
            InputStream inputStream = getInputStream(RSS_LINK + "&" +(testFeed ? DEV_FEED_ID : RSS_FEED_ID) /*+"&" +fromDate*/ +"&" +RSS_FORMAT);
            if (inputStream != null) {
                articleListItems = parser.parse(inputStream);
                Date currDate = new Date();
                AppManager.editor.putString(MmwrPreferences.LAST_UPDATE, new SimpleDateFormat("yyyy-MM-dd").format(currDate));
                AppManager.editor.commit();
            }


        } catch (XmlPullParserException e) {
            Log.w(e.getMessage(), e);
        } catch (IOException e) {
            Log.w(e.getMessage(), e);

        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(ITEMS, (Serializable) articleListItems);
        receiver.send(0, bundle);
    }

    public InputStream getInputStream(String link) {
        try {
            URL url = new URL(link);
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            Log.w(Constants.RSS_SERVICE, "Exception while retrieving the input stream", e);
            receiver.send(1, new Bundle());
            return null;
        }
    }
}

