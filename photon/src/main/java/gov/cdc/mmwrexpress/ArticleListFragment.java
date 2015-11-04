package gov.cdc.mmwrexpress;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import io.realm.Realm;
import io.realm.RealmResults;

/**ArticleListFragment
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class ArticleListFragment extends Fragment implements OnRefreshListener {

    private static final String TAG = "ArticleListFragment";
    private Realm realm;
    private RecyclerView mArticlesRV;
    private View view;
    private ArticleAdapter mAdapter;
    private SwipeRefreshLayout swipeLayout;
    private ConnectivityManager cm;
    private NetworkInfo activeNetwork;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        realm = Realm.getDefaultInstance();
        //Check connection
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
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
            swipeLayout.setColorSchemeResources(R.color.mmwr_blue);
            swipeLayout.setOnRefreshListener(this);

            //Added to display refreshing when fragment starts
            if(activeNetwork != null) {
                swipeLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        onRefresh();
                        swipeLayout.setRefreshing(true);
                    }
                });
            }
            else
            {
                Snackbar.make(getActivity().findViewById(R.id.main_content), "No internet connection detected. Please check your connection and try again.", Snackbar.LENGTH_LONG).show();
            }
        }
        return view;
    }

    @Override public void onRefresh() {
        activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null)
        {
            startService();
        }
        else
        {
            Snackbar.make(getActivity().findViewById(R.id.main_content), "No internet connection detected. Please check your connection and try again.", Snackbar.LENGTH_LONG).show();
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       activeNetwork = cm.getActiveNetworkInfo();
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_LIST, Constants.SC_SECTION_ARTICLES);
    }

    private void startService() {
        Intent intent = new Intent(getActivity(), RssService.class);
        intent.putExtra(RssService.RECEIVER, resultReceiver);
        getActivity().startService(intent);
    }

    /**
     * Once the {@link RssService} finishes its task, the result is sent to this
     * ResultReceiver.
     */
    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String message = null;
            if(resultCode == 0) {
                List<ArticleListItem> items = (List<ArticleListItem>) resultData.getSerializable(RssService.ITEMS);
                if (items != null) {
                    //refreshFromStoredArticles();
                    mAdapter.dataSetChanged();
                    message = "Article list updated.";
                }
            }
            else if(resultCode == 1){
                message = "Unable to access CDC feed. Please try again later.";
            }

            //display Snackbar message if view has not been destroyed.
            try {
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                swipeLayout.setRefreshing(false);
            }
            catch (NullPointerException npe){

            }
        }
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
            try {
                IssueArticleItem item;
                issues = realm.where(Issue.class).findAllSorted("date", RealmResults.SORT_ORDER_DESCENDING);
                Log.d(TAG, "Issues size = " + String.valueOf(issues.size()));
                this.articles = new ArrayList<Article>();
                this.listItems = new ArrayList<IssueArticleItem>();

                // use sorted articles for list view
                for (Issue issue : issues) {
                    item = new IssueArticleItem(issue);
                    listItems.add(item);
                    for (Article article : issue.getArticles()) {
                        item = new IssueArticleItem(article);
                        listItems.add(item);
                        //if (article.isUnread())
                        //Log.d(TAG, "Unread article: " + article.getTitle());
                        //else
                        //Log.d(TAG, "Read article: " + article.getTitle());

                    }
                }
            }
            //IllegalStateException is thrown if application closed while refreshing.
            catch (IllegalStateException ise){
                ise.printStackTrace();
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

                        String title = item.article.getTitle();
                        String date = df.format(item.article.getIssue().getDate());
                        int volume = item.article.getIssue().getVolume();
                        int number = item.article.getIssue().getNumber();
                        String link = item.article.getUrl();

                        //Toast.makeText(getActivity().getApplicationContext(), "Publication date: " + df.format(item.article.getIssue().getDate()), Toast.LENGTH_LONG).show();
                        Intent intent = ArticleDetailActivity.newIntent(getActivity(), title, date, volume, number, link );
                        startActivity(intent);
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