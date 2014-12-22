package gov.cdc.mmwrexpress;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by jtq6 on 10/29/14.
 */
public class IssuesManager {

    RealmResults<Issue> issues;
    RealmResults<Keyword> keywords;
    boolean hasIssues;
    private Realm realm;

    public IssuesManager(Realm realm) {

        this.hasIssues = false;
        this.realm = realm;

    }


    public void getAllKeywords() {

 //       this.keywords = Keyword.listAll(Keyword.class);

    }

    public Issue addNewIssue(Issue newIssue) {

        this.issues.add(newIssue);
        return newIssue;
    }


    public Issue processRssIssue(String dateAsString, Integer volume, Integer number) {


        Date date = getIssueDateFromString(dateAsString);
        RealmQuery<Issue> query = realm.where(Issue.class).equalTo("volume",volume).equalTo("number",number);
        Issue foundIssue = query.findFirst();

        if (foundIssue != null) {
            return foundIssue;
        } else {
            Issue newIssue = realm.createObject(Issue.class);
            newIssue.setDate(date);
            newIssue.setVolume(volume);
            newIssue.setNumber(number);

            return newIssue;
        }

    }



    public Article createArticleInIssue(Issue issue, String title, int version) {

        Article newArticle = realm.createObject(Article.class);
        newArticle.setTitle(title);
        newArticle.setVersion(version);
        issue.getArticles().add(newArticle);
        return newArticle;

    }


    // return a new Article reference only if needed
    // otherwise return null
    public Article processRssArticle(Issue issue, String title, Integer version) {


         // quick check for any articles at all
        if (issue.getArticles().size() == 0)
            return createArticleInIssue(issue, title, version);

        for (Article article: issue.getArticles()) {

            // check if article with this title already exists in current issue
            if (article.getTitle().equals(title)) {

                if (article.getVersion() == version)
                    return article;

                // check if version number is greater than current version
                else if (article.getVersion() < version) {

                    article.removeFromRealm();
                    return createArticleInIssue(issue, title, version);

                }
            }

        }

        // no article matches so create one
        return createArticleInIssue(issue, title, version);

    }

    private Keyword getKeywordWithText(String text) {

        this.keywords = realm.allObjects(Keyword.class);

        for (Keyword currKeyword : this.keywords) {

            if (currKeyword.getText().equals(text))
                return currKeyword;
        }

        return null;

    }


    private Keyword createNewKeywordInArticle(String text, Article article) {

        Keyword newKeyword = realm.createObject(Keyword.class);
        newKeyword.setText(text);
        newKeyword.getArticles().add(article);
        return newKeyword;
    }


    public void addArticleKeywords(String[] foundKeywords, Article article) {
        Keyword keyword;

        for (String currKeyword : foundKeywords) {

            if ((keyword = getKeywordWithText(currKeyword)) == null) {
                createNewKeywordInArticle(currKeyword, article);

            } else {
                keyword.getArticles().add(article);

            }
        }

    }

    public static Date getIssueDateFromString(String dateAsString)
    {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(dateAsString);
            System.out.println(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;

    }



    public void storeTest() {

//        Realm realm = Realm.getInstance();
//
//        realm.beginTransaction();
//
//        Log.d(Constants.ISSUE_MGR,"Starting store test.");
//
//        Issue testIssue = new Issue("2014-11-02",64,32);
//        testIssue.save();
//
//        Article testArticle = new Article("Test Article title");
//
//        String testKeywords[] = {"keyword1", "keyword2"};
//
//        Integer version = 1;
//
//        this.newArticleInIssueWithKeywordsAndVersion(testArticle, testIssue, testKeywords, version);
//
//        realm.commitTransaction();
//
//        Log.d(Constants.ISSUE_MGR,"End of store test.");

    }


}