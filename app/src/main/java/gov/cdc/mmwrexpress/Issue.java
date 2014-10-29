package gov.cdc.mmwrexpress;

import java.lang.reflect.Array;
import java.util.Date;

/**
 * Created by jtq6 on 10/29/14.
 */
public class Issue {


    String title;
    Date date;
    Integer number;
    Integer volume;
    boolean unread;
    Article dummy;

    Article[] articles;

    public Issue (String title) {


    }

    public Issue (String date, Integer vol, Integer num) {

    }


    public void updateUnreadArticleStatus() {

    }

    public Article storeArticle(Article newArticle) {

        return newArticle;
    }

    public Article getArticleWithTitle(String title) {
        return dummy;
    }


    public Article addArticleWithTitle(String title) {

        return dummy;
    }

    public void replaceArticle( Article oldArticle, Article newArticle) {

    }

    public Integer numberOfArticles() {

        return 0;
    }

}
