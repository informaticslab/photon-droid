package gov.cdc.mmwrexpress;

/**
 * Created by jtq6 on 10/29/14.
 */
public class Article {


    String title;
    String url;
    Issue issue;
    String already_know;
    String added_by_report;
    String implications;
    String[] tags;
    Integer version;
    boolean unread;


    public Article (String title) {
        this.title = title;

    }
    public Article (String title, Integer ver) {

    }

}
