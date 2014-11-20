    package gov.cdc.mmwrexpress;


    import java.lang.reflect.Array;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;

    import io.realm.RealmList;
    import io.realm.RealmObject;

    /**
     * Created by jtq6 on 10/29/14.
     */
    public class Issue extends RealmObject {


        String title;
        Date date;
        Integer number;
        Integer volume;
        boolean unread;
        RealmList<Article> articles;

        public Issue () {

        }

        public void setIssue (String dateAsString, Integer vol, Integer num) {

            this.date = getIssueDateFromString(dateAsString);
            this.volume = vol;
            this.number = num;

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


        public void updateUnreadArticleStatus() {

            int unreadCount = 0;

            for (Article article :articles) {
                if (article.unread)
                    unreadCount++;

                if (unreadCount > 0)
                    this.unread = true;
                else
                    this.unread = false;
            }

        }

        public Article addArticle(Article newArticle) {

            newArticle.issue = this;
            this.articles.add(newArticle);

            return newArticle;
        }


        public Article addArticleWithTitle(String title) {

            Article newArticle = new Article();
            newArticle.issue = this;
            this.articles.add(newArticle);

            return newArticle;
        }


        public Integer numberOfArticles() {

            return articles.size();
        }

    }
