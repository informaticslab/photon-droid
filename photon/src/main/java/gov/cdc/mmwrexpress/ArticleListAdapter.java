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
    private Realm realm;
    private RealmResults<Issue> issues;

    private static int ISSUE_VIEW_TYPE = 0;
    private static int READ_ARTICLE_VIEW_TYPE = 1;
    private static int UNREAD_ARTICLE_VIEW_TYPE = 2;

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
            this.text = df.format(issue.getDate()) + "                       VOL " + String.valueOf(issue.getVolume())
                    + " NO " + String.valueOf(issue.getNumber());

        }

        private IssueArticleItem(Article article) {

            this.type = ARTICLE;
            this.text = article.getTitle();
            this.article = article;

        }

    }

    public ArticleListAdapter(Context context) {

        this.context = context;
        //Realm.deleteRealmFile(context);
        this.realm = Realm.getDefaultInstance();
        Log.d("ArticleListAdapter", "realm path: " + realm.getPath());

        // refresh data from database
        this.refreshData();
     }

    public void refreshData() {

        IssueArticleItem item;

        issues = realm.where(Issue.class).findAllSorted("date", RealmResults.SORT_ORDER_DESCENDING);
        Log.d("ArticleListAdapter", "Issues size = " + String.valueOf(issues.size()));
        this.articles = new ArrayList<Article>();
        this.listItems = new ArrayList<IssueArticleItem>();

        // use sorted articles for list view
        for (Issue issue: issues) {
            item = new IssueArticleItem(issue);
            listItems.add(item);
            for (Article article: issue.getArticles()) {
                item = new IssueArticleItem(article);
                listItems.add(item);
                if (article.isUnread())
                    Log.d("ArticleListAdapter", "Unread article: " + article.getTitle());
                else
                    Log.d("ArticleListAdapter", "Read article: " + article.getTitle());

            }
        }

    }


    @Override
    public boolean isEnabled (int position) {

        if (itemIsArticle(position))
            return true;
        else
            return false;

    }

    @Override
    public int getCount() {
            return listItems.size();
    }


    @Override
    public int getItemViewType(int position) {
        IssueArticleItem item = listItems.get(position);
        if (item.type == IssueArticleItem.ISSUE)
            return ISSUE_VIEW_TYPE;
        else if ((item.type == IssueArticleItem.ARTICLE) && item.article.isUnread())
            return UNREAD_ARTICLE_VIEW_TYPE;
        else
            return READ_ARTICLE_VIEW_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
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
        IssueArticleItem item = listItems.get(position);

        if (getItemViewType(position) == UNREAD_ARTICLE_VIEW_TYPE) { // Folder
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.article_list_unread_item, null);
                holder.itemTitle = (TextView) convertView.findViewById(R.id.unreadArticleTitle);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

        } else if (getItemViewType(position) == READ_ARTICLE_VIEW_TYPE) { // Folder
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.article_list_read_item, null);
                holder.itemTitle = (TextView) convertView.findViewById(R.id.readArticleTitle);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

        } else if (getItemViewType(position) == ISSUE_VIEW_TYPE) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.issue_list_item, null);
                holder.itemTitle = (TextView) convertView.findViewById(R.id.issueTitle);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
        }

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
        // Log.d("ArticleListAdapter", "Selected article at position issue " + String.valueOf(position));
        if (selectedItem.type == IssueArticleItem.ARTICLE)
            return selectedItem.article;
        else
            return null;

    }

    public void setArticleReadState(Article article) {

        realm.beginTransaction();

        article.setUnread(false);

        realm.commitTransaction();

    }

}