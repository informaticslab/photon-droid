package gov.cdc.mmwrexpress;


import io.realm.RealmObject;

/**
 * Created by jtq6 on 10/29/14.
 */
public class Article extends RealmObject {


    String title;
    String url;
    Issue issue;
    String already_known;
    String added_by_report;
    String implications;
    String[] tags;
    Integer version;

    boolean unread;

    public Article() {

    }
    public void setArticle (String title) {
        this.title = title;

    }
    public void setArticle (String title, Integer ver) {
        this.title = title;
        this.version = ver;

    }


}
