package gov.cdc.mmwrexpress;


public class ArticleListItem {

    private final String title;
    private final String link;
    private final String description;
    private Article article;

    public ArticleListItem(String title, String link, String description, Article article) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.article = article;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return link;
    }
    
    public String getArticleTitle() {
        if (article == null )
            return "";
        else
            return article.getTitle();
    }

}