    package gov.cdc.mmwrexpress;


    import java.lang.reflect.Array;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;

    import io.realm.RealmList;
    import io.realm.RealmObject;
    import io.realm.annotations.RealmClass;

    /**
     * Created by jtq6 on 10/29/14.
     */
    @RealmClass
    public class Issue extends RealmObject {


        private Date date;
        private int number;
        private int volume;
        private boolean unread;
        private RealmList<Article> articles;



        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }


        public boolean isUnread() {
            return unread;
        }

        public void setUnread(boolean unread) {
            this.unread = unread;
        }

        public RealmList<Article> getArticles() {
            return articles;
        }

        public void setArticles(RealmList<Article> articles) {
            this.articles = articles;
        }
    }
