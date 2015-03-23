package gov.cdc.mmwrexpress;


import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.RealmClass;

/**
 * Created by jtq6 on 10/29/14.
 */
@RealmClass
public class Article extends RealmObject {


    private String title;
    private String url;
    @Ignore Issue issue;
    private String already_known;
    private String added_by_report;
    private String implications;
    @Ignore private String[] tags;
    private int version;
    private boolean unread;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getAlready_known() {
        return already_known;
    }

    public void setAlready_known(String already_known) {
        this.already_known = already_known;
    }

    public String getAdded_by_report() {
        return added_by_report;
    }

    public void setAdded_by_report(String added_by_report) {
        this.added_by_report = added_by_report;
    }

    public String getImplications() {
        return implications;
    }

    public void setImplications(String implications) {
        this.implications = implications;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}
