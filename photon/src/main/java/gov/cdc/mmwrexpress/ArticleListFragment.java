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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

//public class ArticleListFragment extends Fragment implements OnItemClickListener, OnRefreshListener {
public class ArticleListFragment extends Fragment implements OnRefreshListener {

    private static final String TAG = "ArticleListFragment";

    private RecyclerView mArticlesRV;
    private View view;
    private ArticleAdapter mAdapter;
    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
            //readStoredArticles();
            //startService();
        } else {
            // If we are returning from a configuration change:
            // "view" is still attached to the previous view hierarchy
            // so we need to remove it and re-attach it to the current one
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        return view;
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
                Toast.makeText(getActivity(), "An error occurred while accessing the CDC feed.",
                        Toast.LENGTH_LONG).show();
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

        private static final String TAG = "ArticleAdapter";
        private ArrayList<Article> articles;
        private ArrayList<IssueArticleItem> listItems;
        private Realm realm;
        private RealmResults<Issue> issues;

        public static final int ISSUE_VIEW_TYPE = 0;
        public static final int READ_ARTICLE_VIEW_TYPE = 1;
        public static final int UNREAD_ARTICLE_VIEW_TYPE = 2;
        private final Context context;


        public ArticleAdapter(Context context) {

            this.context = context;
            //Realm.deleteRealmFile(context);
            this.realm = Realm.getInstance(context);
            Log.d(TAG, "realm path: " + realm.getPath());

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

            return new IssueArticleHolder(view, viewType);
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
                    if (article.isUnread())
                        Log.d(TAG, "Unread article: " + article.getTitle());
                    else
                        Log.d(TAG, "Read article: " + article.getTitle());

                }
            }

        }

        @UiThread
        protected void dataSetChanged() {
            refreshData();
            notifyDataSetChanged();
        }
    }

    private class IssueArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private IssueArticleItem mIssueArticleItem;
        private int mViewType;
        private TextView mArticleTitleTextView;


        public IssueArticleHolder(View itemView, int viewType) {
            super(itemView);
            itemView.setOnClickListener(this);
            mViewType = viewType;

            if (viewType == ArticleAdapter.UNREAD_ARTICLE_VIEW_TYPE)
                mArticleTitleTextView = (TextView) itemView.findViewById(R.id.unreadArticleTitle);
            else if (viewType == ArticleAdapter.READ_ARTICLE_VIEW_TYPE)
                mArticleTitleTextView = (TextView) itemView.findViewById(R.id.readArticleTitle);
            else if (viewType == ArticleAdapter.ISSUE_VIEW_TYPE)
                mArticleTitleTextView = (TextView) itemView.findViewById(R.id.issueTitle);

        }

        public void bindArticle(IssueArticleItem item) {
            mIssueArticleItem = item;
            mArticleTitleTextView.setText(mIssueArticleItem.text);
        }

        @Override
        public void onClick(View v) {

            Article article = mIssueArticleItem.article;
            Intent intent = ContentActivity.newIntent(getActivity(), article.getAlready_known(),
                    article.getAdded_by_report(), article.getImplications());
            startActivity(intent);

        }

    }


}