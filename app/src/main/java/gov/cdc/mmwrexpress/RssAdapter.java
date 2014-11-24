package gov.cdc.mmwrexpress;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import gov.cdc.mmwrexpress.R;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RssAdapter extends BaseAdapter {

//    private final RealmList<Article> articles ;
    private final Context context;
    private Realm realm;
    private RealmResults<Article> articles;

    public RssAdapter(Context context, List<RssItem> items) {
        //this.items = items;
        this.context = context;
        this.realm = Realm.getInstance(context);
        this.articles = realm.where(Article.class).findAll();
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
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

        holder.itemTitle.setText(articles.get(position).getTitle());
//        holder.itemTitle.setText(items.get(position).getDescription());

        return convertView;
    }

    static class ViewHolder {
        TextView itemTitle;
    }
}