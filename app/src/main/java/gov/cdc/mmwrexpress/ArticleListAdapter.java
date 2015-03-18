package gov.cdc.mmwrexpress;

import java.text.DateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

public class ArticleListAdapter extends BaseAdapter {


    //    private final RealmList<Article> articles ;
    private final Context context;
    private ArrayList<Article> articles;
    private ArrayList<IssueArticleItem> listItems;
    private class IssueArticleItem {

        private static final int ISSUE = 0;
        private static final int ARTICLE = 1;

        private int type;
        private String text;
        private Article article;


        private IssueArticleItem(Issue issue) {

            this.type = ISSUE;
            this.article = null;
            DateFormat df = DateFormat.getDateInstance();
            this.text = df.format(issue.getDate()) + "                     VOL " + String.valueOf(issue.getVolume())
                    + " NO " + String.valueOf(issue.getNumber());

        }

        private IssueArticleItem(Article article) {

            this.type = ARTICLE;
            this.text = article.getTitle();
            this.article = article;

        }

    }

    public ArticleListAdapter(Context context) {

        Realm realm;
        RealmResults<Issue> issues;
        IssueArticleItem item;

        //this.items = items;
        this.context = context;
        realm = Realm.getInstance(context);
        issues = realm.where(Issue.class).findAllSorted("date", RealmResults.SORT_ORDER_DESCENDING);
        this.articles = new ArrayList<Article>();
        this.listItems = new ArrayList<IssueArticleItem>();

        // use sorted articles for list view
        for (Issue issue: issues) {
            item = new IssueArticleItem(issue);
            listItems.add(item);
            for (Article article: issue.getArticles()) {
                item = new IssueArticleItem(article);
                listItems.add(item);

            }
        }
   }

    @Override
    public int getCount() {
        return listItems.size();
    }


    @Override
    public int getItemViewType(int position) {
        return listItems.get(position).type;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        IssueArticleItem item  = listItems.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            if (item.type == IssueArticleItem.ARTICLE) {
                convertView = View.inflate(context, R.layout.article_list_item, null);
                holder.itemTitle = (TextView) convertView.findViewById(R.id.articleTitle);
                Log.d("ArticleListAdapter", "article view");
            } else if (item.type == IssueArticleItem.ISSUE) {
                convertView = View.inflate(context, R.layout.issue_list_item, null);
                holder.itemTitle = (TextView) convertView.findViewById(R.id.issueTitle);
                Log.d("ArticleListAdapter", "issue view");

            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //holder.itemTitle.setText(articles.get(position).getTitle());
        holder.itemTitle.setText(listItems.get(position).text);
        return convertView;
    }

    static class ViewHolder {
        TextView itemTitle;
    }

    public boolean itemIsArticle(int position) {
        if (listItems.get(position).type == IssueArticleItem.ARTICLE)
            return true;
        else
            return false;


    }

    public Article getArticle(int position) {

        IssueArticleItem selectedItem = listItems.get(position);
        if (selectedItem.type == IssueArticleItem.ARTICLE)
            return selectedItem.article;
        else
            return null;

    }

}