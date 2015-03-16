package gov.cdc.mmwrexpress;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

public class ArticleListAdapter extends BaseAdapter {

//    private final RealmList<Article> articles ;
    private final Context context;
    private Realm realm;
    private RealmResults<Issue> issues;
    private RealmResults<Article> articles;
    private ArrayList<Article> sortedArticles;

    public ArticleListAdapter(Context context) {
        //this.items = items;
        this.context = context;
        this.realm = Realm.getInstance(context);
        this.issues = realm.where(Issue.class).findAllSorted("date", RealmResults.SORT_ORDER_DESCENDING);
        this.sortedArticles = new ArrayList<Article>();

        // use sorted articles for list view
        for (Issue issue: issues) {
            for (Article article: issue.getArticles()) {
                this.sortedArticles.add(article);

            }
        }
   }

    @Override
    public int getCount() {
        return sortedArticles.size();
    }

    @Override
    public Object getItem(int position) {
        return sortedArticles.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.rss_item, null);
            holder = new ViewHolder();
            holder.itemTitle = (TextView) convertView.findViewById(R.id.itemTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemTitle.setText(sortedArticles.get(position).getTitle());
//        holder.itemTitle.setText(items.get(position).getDescription());

        return convertView;
    }

    static class ViewHolder {
        TextView itemTitle;
    }

    public Article getArticle(int position)
    {
        return (Article)this.getItem(position);

    }
}