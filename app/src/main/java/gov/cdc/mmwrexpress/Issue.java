    package gov.cdc.mmwrexpress;

    import com.orm.SugarRecord;

    import java.lang.reflect.Array;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.List;

    /**
     * Created by jtq6 on 10/29/14.
     */
    public class Issue extends SugarRecord<Issue> {


        String title;
        Date date;
        Integer number;
        Integer volume;
        boolean unread;
        Article dummy;

        List<Article> articles;

        public Issue () {

        }

        public Issue (String dateAsString, Integer vol, Integer num) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(dateAsString);
                System.out.println(date);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            this.volume = vol;
            this.number = num;

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

        public Article getArticleWithTitle(String title) {

            if (numberOfArticles() == 0)
                return null;

            for (Article article :articles) {
                if (article.title.equals(title) ) {
                    return article;

                }

            }

            return null;
        }

        public Article addArticle(Article newArticle) {

            newArticle.issue = this;
            this.articles.add(newArticle);

            return newArticle;
        }


        public Article addArticleWithTitle(String title) {

            Article newArticle = new Article(title);
            newArticle.issue = this;
            this.articles.add(newArticle);

            return newArticle;
        }


        public void replaceArticle( Article oldArticle, Article newArticle) {

        }

        public Integer numberOfArticles() {

            return articles.size();
        }

    }
