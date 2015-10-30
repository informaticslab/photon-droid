package gov.cdc.mmwrexpress;


import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**Keyword.java
 * photon-droid
 *
 * Created by jtq6 on 10/29/14.
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

@RealmClass
public class Keyword extends RealmObject {

    private String text;
    private RealmList<Article> articles;

    public Keyword () {


    }
//    public Keyword(String text) {
//
//        this();
//
//        this.text = text;
//    }
//
//    public void foundInArticle(Article article) {
//
//        this.articles.add(article);
//    }
//
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RealmList<Article> getArticles() {
        return articles;
    }

    public void setArticles(RealmList<Article> articles) {
        this.articles = articles;
    }
}

