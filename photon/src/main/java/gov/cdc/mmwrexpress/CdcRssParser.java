package gov.cdc.mmwrexpress;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

import io.realm.Realm;

public class CdcRssParser {

    // don't use namespaces
    private final String ns = null;
    private JsonArticleParser jsonArticleParser;
    private Realm realm;

    public CdcRssParser (Context ctx) {

        // uncomment this line and run once when data model changes
        this.realm = Realm.getInstance(ctx);
        this.jsonArticleParser = new JsonArticleParser(this.realm);
    }

    public List<ArticleListItem> parse(InputStream inputStream) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }



    private List<ArticleListItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "rss");
        String title = null;
        String link = null;
        String description = null;
        Article article = null;

        List<ArticleListItem> items = new ArrayList<ArticleListItem>();

        // get RSS2 tags <title>, <link> and <description>
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("link")) {

                // link to the article on the CDC website
                link = readLink(parser);
            } else if (name.equals("description")) {

                // all content for blue boxes of article are store in description
                description = readDescription(parser);
                article = this.jsonArticleParser.parseJsonArticle(description);

            }
            if (title != null && link != null && description != null && article != null ) {
                ArticleListItem item = new ArticleListItem(title, link, description, article);
                items.add(item);
                title = null;
                link = null;
                description = null;
                article = null;
            }
        }
        return items;
    }

    private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    private String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    // For the tags title and link, extract their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}