package gov.cdc.mmwrexpress;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class ArticleListFragment extends Fragment implements OnRefreshListener {

    private static final String TAG = "ArticleListFragment";
    private Realm realm;
    private RecyclerView mArticlesRV;
    private View view;
    private ArticleAdapter mAdapter;
    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        realm = Realm.getDefaultInstance();
        startService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.article_list_fragment, container, false);
            mArticlesRV = (RecyclerView) view.findViewById(R.id.articles_rv);
            mArticlesRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            mArticlesRV.addItemDecoration(new SimpleDividerItemDecoration(
                    getActivity()
            ));
//            listView.setOnItemClickListener(this);
            swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            swipeLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeLayout.setRefreshing(true);
                    startService();
                }
            });

            updateUI();
        } else {
            // If we are returning from a configuration change:
            // "view" is still attached to the previous view hierarchy
            // so we need to remove it and re-attach it to the current one
//            ViewGroup parent = (ViewGroup) view.getParent();
//            parent.removeView(view);
        }
        return view;
    }

    @Override public void onStart() {
        super.onStart();


    }
    @Override public void onRefresh() {
    }

    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void updateUI() {

        if (mAdapter == null) {
            // read stored articles
            mAdapter = new ArticleAdapter(getActivity());
            mArticlesRV.setAdapter(mAdapter);

        } else {
            mAdapter.notifyDataSetChanged();
        }

    }


    private void startService() {
        Intent intent = new Intent(getActivity(), RssService.class);
        intent.putExtra(RssService.RECEIVER, resultReceiver);
        getActivity().startService(intent);
    }

    private void readStoredArticles() {

        // read stored articles
        mAdapter = new ArticleAdapter(getActivity());
        mArticlesRV.setAdapter(mAdapter);

    }


    private void refreshFromStoredArticles() {

        // reread stored articles
        mAdapter.refreshData();

    }

    /**
     * Once the {@link RssService} finishes its task, the result is sent to this
     * ResultReceiver.
     */
    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            List<ArticleListItem> items = (List<ArticleListItem>) resultData.getSerializable(RssService.ITEMS);
            if (items != null) {

                //refreshFromStoredArticles();
                mAdapter.dataSetChanged();
            } else {
                //Toast.makeText(getActivity(), "An error occurred while accessing the CDC feed.",
                        //Toast.LENGTH_LONG).show();
            }

            swipeLayout.setRefreshing(false);
        };
    };


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

    private class ArticleAdapter extends RecyclerView.Adapter<IssueArticleHolder> {

        private static final String TAG = "KeywordArticleAdapter";
        private ArrayList<Article> articles;
        private ArrayList<IssueArticleItem> listItems;
        private RealmResults<Issue> issues;

        public static final int ISSUE_VIEW_TYPE = 0;
        public static final int READ_ARTICLE_VIEW_TYPE = 1;
        public static final int UNREAD_ARTICLE_VIEW_TYPE = 2;
        private final Context context;


        public ArticleAdapter(Context context) {

            this.context = context;
            //Realm.deleteRealmFile(context);

            //Log.d(TAG, "realm path: " + realm.getPath());

            // refresh data from database
            this.refreshData();
        }

        @Override
        public IssueArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = null;
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if (viewType == ISSUE_VIEW_TYPE) {
                view = layoutInflater.inflate(R.layout.issue_list_item, parent, false);

            } else if (viewType == UNREAD_ARTICLE_VIEW_TYPE) {
                view = layoutInflater.inflate(R.layout.article_list_unread_item, parent, false);

            } else if (viewType == READ_ARTICLE_VIEW_TYPE) {
                view = layoutInflater.inflate(R.layout.article_list_read_item, parent, false);

            }

            return new IssueArticleHolder(view, viewType, this);
        }

        public void onBindViewHolder(IssueArticleHolder holder, int position) {
            IssueArticleItem article = listItems.get(position);
            holder.bindArticle(article);

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
        public int getItemCount() {
            return listItems.size();
        }

        public void refreshData() {

            IssueArticleItem item;
            issues = realm.where(Issue.class).findAllSorted("date", RealmResults.SORT_ORDER_DESCENDING);
            Log.d(TAG, "Issues size = " + String.valueOf(issues.size()));
            this.articles = new ArrayList<Article>();
            this.listItems = new ArrayList<IssueArticleItem>();

            // use sorted articles for list view
            for (Issue issue: issues) {
                item = new IssueArticleItem(issue);
                listItems.add(item);
                for (Article article: issue.getArticles()) {
                    item = new IssueArticleItem(article);
                    listItems.add(item);
                    //if (article.isUnread())
                        //Log.d(TAG, "Unread article: " + article.getTitle());
                    //else
                        //Log.d(TAG, "Read article: " + article.getTitle());

                }
            }
        }

        public void setArticleReadState(Article article) {
            realm.beginTransaction();

            article.setUnread(false);

            realm.commitTransaction();
        }


        @UiThread
        protected void dataSetChanged() {
            refreshData();
            notifyDataSetChanged();
        }
    }

    public class ArticleListAdapter extends BaseAdapter {


        //    private final RealmList<Article> articles ;
        private final Context context;
        private ArrayList<Article> articles;
        private ArrayList<IssueArticleItem> listItems;
        private RealmResults<Issue> issues;

        private int ISSUE_VIEW_TYPE = 0;
        private int READ_ARTICLE_VIEW_TYPE = 1;
        private int UNREAD_ARTICLE_VIEW_TYPE = 2;

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

        private class ViewHolder {
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

    private class IssueArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private IssueArticleItem mIssueArticleItem;
        private int mViewType;
        private TextView mArticleTitleTextView;
        private ImageButton mArticleInfoButton;
        private ArticleAdapter mAdapter;


        public IssueArticleHolder(View itemView, int viewType, ArticleAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            mViewType = viewType;
            mAdapter = adapter;

            if (viewType == ArticleAdapter.UNREAD_ARTICLE_VIEW_TYPE) {
                mArticleTitleTextView = (TextView) itemView.findViewById(R.id.unreadArticleTitle);
                mArticleInfoButton = (ImageButton) itemView.findViewById(R.id.articleInfoButton);
            }
            else if (viewType == ArticleAdapter.READ_ARTICLE_VIEW_TYPE) {
                mArticleTitleTextView = (TextView) itemView.findViewById(R.id.readArticleTitle);
                mArticleInfoButton = (ImageButton) itemView.findViewById(R.id.articleInfoButton);
            }
            else if (viewType == ArticleAdapter.ISSUE_VIEW_TYPE)
                mArticleTitleTextView = (TextView) itemView.findViewById(R.id.issueTitle);

        }

        public void bindArticle(final IssueArticleItem item) {
            mIssueArticleItem = item;
            mArticleTitleTextView.setText(mIssueArticleItem.text);
            if(getItemViewType() == ArticleAdapter.READ_ARTICLE_VIEW_TYPE || getItemViewType() == ArticleAdapter.UNREAD_ARTICLE_VIEW_TYPE) {
                mArticleInfoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateFormat df = DateFormat.getDateInstance();
                        Toast.makeText(getActivity().getApplicationContext(), "Publication date: " + df.format(item.article.getIssue().getDate()), Toast.LENGTH_LONG).show();

                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            if(mViewType != ArticleAdapter.ISSUE_VIEW_TYPE) {
                Article article = mIssueArticleItem.article;
                mAdapter.setArticleReadState(article);
                Intent intent = ContentActivity.newIntent(getActivity(), article.getAlready_known(),
                        article.getAdded_by_report(), article.getImplications());
                startActivity(intent);
            }

        }

    }


}