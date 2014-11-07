package gov.cdc.mmwrexpress;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by jtq6 on 10/29/14.
 */
public class IssuesManager {

    List<Issue> issues;
    List<Keyword> keywords;
    boolean hasIssues;

    public IssuesManager() {

        this.hasIssues = false;
        this.issues = Issue.listAll(Issue.class);

    }


    public void getAllKeywords() {

        this.keywords = Keyword.listAll(Keyword.class);


    }

    public Issue addNewIssue(Issue newIssue) {

        this.issues.add(newIssue);
        return newIssue;
    }

    public Issue getStoredIssueForIssue(Issue issue) {

        return getIssueWithDateVolNum(issue.date.toString(), issue.volume.toString(), issue.number.toString());
    }

    public Issue getIssueWithDateVolNum(String date, String volume, String number) {


        List<Issue> foundIssues = Issue.find(Issue.class, "date = ? and volume = ? and number = ?", date, volume, number);

        if (foundIssues.size() == 0)
            return null;
        else if (foundIssues.size() == 1)
            return foundIssues.get(0);
        else
            Log.d(Constants.ISSUE_MGR, "Found multiple stored issues with same date, volume and number.");

        return null;

    }


    public boolean isIssueNew(Issue downloadedIssue) {

        if (issues.isEmpty())
            return true;

        Issue issue = getStoredIssueForIssue(downloadedIssue);

        if (issue != null)
            return false;
        else
            return true;
    }

    public void createArticleInIssue(Article newArticle, Issue issue) {
        issue.addArticle(newArticle);
    }

    private boolean isArticleNewInIssue(Article newArticle, Issue issue) {

        if (issue.articles.size() == 0)
            return true;

        // check if article with this title already exists in current issue
        Article existingArticle = issue.getArticleWithTitle(newArticle.title);

        // if no article with title
        if (existingArticle == null)
            return true;

        return false;

    }

    public void newArticleInIssueWithKeywordsAndVersion(Article article, Issue issue, String[] keywords, Integer ver) {

        Issue storedIssue = null;
        Article storedArticle = null;

        if (isIssueNew(issue)) {
            storedIssue = addNewIssue(issue);
        } else {
            storedIssue = getStoredIssueForIssue(issue);

        }
        // check  article with this title already exists in current issue
        if (isArticleNewInIssue(article, storedIssue)) {

            this.createArticleInIssue(article, storedIssue);
            this.addArticleKeywords(keywords, article);

        } else {

            storedArticle = storedIssue.getArticleWithTitle(article.title);

            // check if version number is greater than current version
            if (article.version > storedArticle.version.intValue()) {

                // create new article object and replace the old with new
                storedIssue.replaceArticle(storedArticle, article);
                this.addArticleKeywords(keywords, storedArticle);

            }
        }

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
}