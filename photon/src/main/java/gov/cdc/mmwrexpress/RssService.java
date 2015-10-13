package gov.cdc.mmwrexpress;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class RssService extends IntentService {

    private static final String RSS_LINK = "http://t.cdc.gov/feed.aspx?feedid=100&format=rss2";
    public static final String ITEMS = "items";
    public static final String RECEIVER = "receiver";
    private Context ctx;

    public RssService() {
        super("RssService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(Constants.RSS_SERVICE, "Service started");
        ctx = this;
        List<ArticleListItem> articleListItems = null;
        try {
            CdcRssParser parser = new CdcRssParser(ctx);
            InputStream inputStream = getInputStream(RSS_LINK);
            if (inputStream != null)
                articleListItems = parser.parse(getInputStream(RSS_LINK));

        } catch (XmlPullParserException e) {
            Log.w(e.getMessage(), e);
        } catch (IOException e) {
            Log.w(e.getMessage(), e);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(ITEMS, (Serializable) articleListItems);
        ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        receiver.send(0, bundle);
    }

    public InputStream getInputStream(String link) {
        try {
            URL url = new URL(link);
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            Log.w(Constants.RSS_SERVICE, "Exception while retrieving the input stream", e);
            return null;
        }
    }
}

