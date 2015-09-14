package gov.cdc.mmwrexpress;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.ArrayList;

import io.realm.Realm;


/**
 * Created by jtq6 on 10/28/14.
 */
public class JsonArticleParser {

    String  json;
    Article article;
    private Realm realm;
    private IssuesManager issuesManager;

    // JSON Node names
    private static final String TAG_ISSUE_DATE = "issue-date";
    private static final String TAG_ISSUE_VOL = "issue-vol";
    private static final String TAG_ISSUE_NUM = "issue-no";
    private static final String TAG_TITLE = "title";
    private static final String TAG_ALREADY_KNOWN = "already_known";
    private static final String TAG_ADDED_BY_REPORT = "added_by_report";
    private static final String TAG_IMPLICATIONS = "implications";
    private static final String TAG_TAGS = "tags";
    private static final String TAG_TAG = "tag";
    private static final String TAG_URL = "url";
    private static final String TAG_CONTENT_VER = "content-ver";
    private static final String TAG_SCHEMA_VER = "schema-ver";

    public JsonArticleParser(Realm realm) {

        this.realm = realm;
        this.issuesManager = new IssuesManager(realm);
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public Article parseJsonArticle(String jsonArticle) {

        if (jsonArticle == null)
            return null;

        if (isJSONValid(jsonArticle) == false)
            return null;

            // create new JSON object from string
        try {

            JSONObject jsonObject = new JSONObject(jsonArticle);

            int size = jsonObject.length();

            realm.beginTransaction();

            // process issue found in RSS feed and find stored issue
            // if no stored issue create a new one
            Issue issue = issuesManager.processRssIssue(jsonObject.getString(TAG_ISSUE_DATE),
                    jsonObject.getInt(TAG_ISSUE_VOL), jsonObject.getInt(TAG_ISSUE_NUM));



            // process article found in RSS feed and find stored issue
            // or in not there create a new one
            Article article = issuesManager.processRssArticle(issue, jsonObject.getString(TAG_TITLE),jsonObject.getInt(TAG_CONTENT_VER));
            if (article != null) {

                article.setAlready_known(jsonObject.getString(TAG_ALREADY_KNOWN));
                article.setAdded_by_report(jsonObject.getString(TAG_ADDED_BY_REPORT));
                article.setImplications(jsonObject.getString(TAG_IMPLICATIONS));
                article.setUrl(jsonObject.getString(TAG_URL));

                try {
                    JSONArray keywordJsonArray = jsonObject.getJSONArray(TAG_TAGS);
                    JSONObject keywordJson;
                    String[] keywords = new String[keywordJsonArray.length()];
                    for (int i = 0; i < keywordJsonArray.length(); i++) {
                        keywordJson = (JSONObject) keywordJsonArray.get(i);
                        keywords[i] = keywordJson.getString(TAG_TAG);
                    }
                    issuesManager.addArticleKeywords(keywords, article);

                } catch (JSONException ex ) {
                    ex.printStackTrace();
                    realm.cancelTransaction();
                }

            }

            realm.commitTransaction();


            //Log.d("JsonArticleParser", "JSON Article title = " + jsonObject.getString(TAG_TITLE));

            //Log.d("JsonArticleParser", "JSON Article size = " + String.valueOf(size));
            //Log.d("JsonArticleParser", "JSON issue date  = " + jsonObject.getString(TAG_ISSUE_DATE));

            return article;

        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }

    }


    public void parseJsonArticlesFromString(String input) {
//        try {
//            JSONObject issues = new JSONObject(input);
//            JSONArray m_jArry = issues.getJSONArray();
//            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
//            HashMap<String, String> m_li;
//
//            for (int i = 0; i < m_jArry.length(); i++) {
//                JSONObject jo_inside = m_jArry.getJSONObject(i);
//                Log.d("Details-->", jo_inside.getString("formule"));
//                String formula_value = jo_inside.getString("formule");
//                String url_value = jo_inside.getString("url");
//
//                //Add your values in your `ArrayList` as below:
//                m_li = new HashMap<String, String>();
//                m_li.put("formule", formula_value);
//                m_li.put("url", url_value);
//
//                formList.add(m_li);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
    }
}
