package gov.cdc.mmwrexpress;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by jtq6 on 10/28/14.
 */
public class JsonArticleParser {

    String  json;
    Article article;

    // JSON Node names
    private static final String TAG_ISSUE_DATE = "issue-date";
    private static final String TAG_ISSUE_VOL = "issue-vol";
    private static final String TAG_ISSUE_NUM = "issue-no";
    private static final String TAG_TITLE = "title";
    private static final String TAG_ALREADY_KNOWN = "already_known";
    private static final String TAG_ADDED_BY_REPORT = "added_by_report";
    private static final String TAG_IMPLICATIONS = "implications";
    private static final String TAG_TAGS = "tags";
    private static final String TAG_URL = "url";
    private static final String TAG_CONTENT_VER = "content-ver";
    private static final String TAG_SCHEMA_VER = "schema-ver";

    public JsonArticleParser(String jsonArticle) {

        if (jsonArticle != null) {
            this.json = jsonArticle;
            parseJson();
        }
    }


    private boolean parseJson() {

        // create new JSON object from string
        try {

            JSONObject jsonObject = new JSONObject(this.json);
            int size = jsonObject.length();

            Issue newIssue = new Issue(jsonObject.getString(TAG_ISSUE_DATE),
                    jsonObject.getInt(TAG_ISSUE_NUM), jsonObject.getInt(TAG_ISSUE_NUM));

            Article newArticle = new Article(jsonObject.getString(TAG_TITLE));
            newArticle.already_know = jsonObject.getString(TAG_ALREADY_KNOWN);
            newArticle.added_by_report = jsonObject.getString(TAG_ADDED_BY_REPORT);
            newArticle.implications = jsonObject.getString(TAG_IMPLICATIONS);
            newArticle.url = jsonObject.getString(TAG_URL);
            newArticle.already_know = jsonObject.getString(TAG_ALREADY_KNOWN);


            Log.d("JsonArticleParser", "JSON Article size = " + String.valueOf(size));

            Log.d("JsonArticleParser", "JSON issue date  = " + jsonObject.getString(TAG_ISSUE_DATE));

            return true;

        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }


}
