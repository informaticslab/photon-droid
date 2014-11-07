package gov.cdc.mmwrexpress;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by jtq6 on 10/29/14.
 */
public class Keyword extends SugarRecord<Keyword> {

    String text;
    List<Article> articles;

    public Keyword () {

    }
    public Keyword(String text) {
        this.text = text;
    }

    public void foundInArticle(Article article) {

        this.articles.add(article);
    }



}

