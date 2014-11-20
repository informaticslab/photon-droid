package gov.cdc.mmwrexpress;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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
//        this.issues = realm.allObjects(Issue.class);
//        this.keywords = realm.allObjects(Keyword.class);

    }


    public void getAllKeywords() {

 //       this.keywords = Keyword.listAll(Keyword.class);


    }

    public Issue addNewIssue(Issue newIssue) {

        this.issues.add(newIssue);
        return newIssue;
    }


    public Issue processRssIssue(String dateAsString, Integer volume, Integer number) {


        Date date = Issue.getIssueDateFromString(dateAsString);
        Issue foundIssue = realm.where(Issue.class).equalTo("date", date).equalTo("volume",volume).equalTo("number",number).findFirst();

        if (foundIssue != null) {
            return foundIssue;
        } else {
            Issue newIssue = realm.createObject(Issue.class);
            newIssue.setIssue(dateAsString, volume, number);

            return newIssue;
        }

    }



    public Article createArticleInIssue(Issue issue, String title, Integer version) {

        Article newArticle = realm.createObject(Article.class);
        newArticle.title = title;
        newArticle.version = version;
        issue.articles.add(newArticle);
        return newArticle;

    }


    // return a new Article reference only if needed
    // otherwise return null
    public Article processRssArticle(Issue issue, String title, Integer version) {


         // quick check for any articles at all
        if (issue.articles.size() == 0) {
            Article newArticle = createArticleInIssue(issue, title, version);
            return newArticle;
        }

        for (Article article: issue.articles) {

            // check if article with this title already exists in current issue
            if (article.title.equals(title)) {

                if (article.version == version)
                    return article;

                // check if version number is greater than current version
                else if (article.version < version) {

                    article.removeFromRealm();
                    Article newArticle = createArticleInIssue(issue, title, version);
                    return newArticle;

                }
            }

        }

        // no article matches so create one
        Article newArticle = createArticleInIssue(issue, title, version);
        return newArticle;
    }

    private Keyword getKeywordWithText(String text) {

        for (Keyword currKeyword : this.keywords) {

            if (currKeyword.text.equals(text))
                return currKeyword;
        }

        return null;

    }


    private Keyword createNewKeywordInArticle(String text, Article article) {

        Keyword newKeyword = new Keyword(text);
        newKeyword.foundInArticle(article);
        this.keywords.add(newKeyword);

        return newKeyword;
    }


    public void addArticleKeywords(String[] foundKeywords, Article article) {
        Keyword keyword;

        for (String currKeyword : foundKeywords) {

            if ((keyword = getKeywordWithText(currKeyword)) == null) {
                keyword = createNewKeywordInArticle(currKeyword, article);

            } else {
                keyword.foundInArticle(article);

            }
        }

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