package gov.cdc.mmwrexpress;

import java.text.DateFormat;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

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
    private RealmChangeListener issuesChangeListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        realm = Realm.getDefaultInstance();
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

            swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            swipeLayout.setColorSchemeResources(R.color.mmwr_blue);
            swipeLayout.setOnRefreshListener(this);

            //update content on first launch
            if(!AppManager.pref.getBoolean(MmwrPreferences.REFRESHED_ARTICLE_LIST_ON_FIRST_LAUNCH, false)) {
                forceRefresh();
                AppManager.editor.putBoolean(MmwrPreferences.REFRESHED_ARTICLE_LIST_ON_FIRST_LAUNCH, true);
                AppManager.editor.commit();
            }
        }
        return view;
    }

    @Override public void onRefresh() {
        initiateRefresh();
    }


    @Override
    public void onResume() {
        super.onResume();
        AppManager.issuesManager.registerProgressIndicator(swipeLayout);
        updateUI();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.removeAllChangeListeners();
        realm.close();
        AppManager.issuesManager.unregisterProgressIndicator(swipeLayout);
    }

    private void updateUI() {

     if (mAdapter == null) {
            //read stored articles
            mAdapter = new ArticleAdapter(getActivity());
            mArticlesRV.setAdapter(mAdapter);

        } else {
            mAdapter.dataSetChanged();
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_LIST, Constants.SC_SECTION_ARTICLES);
    }

    //use to force refresh data AND force swipe layout to display progress notification
    public void forceRefresh() {
        if (swipeLayout.isRefreshing()) {
            Log.d("forceRefresh", "Refreshing, skip refresh request.");
        } else {
            Log.d("forceRefresh", "Init refresh.");
            swipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    initiateRefresh();
                    swipeLayout.setRefreshing(true);
                }
            });
        }
    }

    public void initiateRefresh() {
            AppManager.sc.trackEvent(Constants.SC_EVENT_REFRESH_ARTICLE_LIST ,Constants.SC_PAGE_TITLE_LIST, Constants.SC_SECTION_ARTICLES);
            AppManager.issuesManager.update();
        }

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
            issues = realm.where(Issue.class).findAllSorted("date", Sort.DESCENDING);
            issuesChangeListener = new RealmChangeListener() {
                @Override
                public void onChange(Object o) {
                    updateUI();
                }
            };
            realm.addChangeListener(issuesChangeListener);


            // refresh data from database
            this.dataSetChanged();
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

                //Log.d(TAG, "Issues size = " + String.valueOf(issues.size()));
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
        private ArticleAdapter mAdapter;

        private String title;
        private String date;
        private int volume;
        private int number;
        private String link;


        public IssueArticleHolder(View itemView, int viewType, ArticleAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            mViewType = viewType;
            mAdapter = adapter;

            if (viewType == ArticleAdapter.UNREAD_ARTICLE_VIEW_TYPE) {
                mArticleTitleTextView = (TextView) itemView.findViewById(R.id.unreadArticleTitle);
            }
            else if (viewType == ArticleAdapter.READ_ARTICLE_VIEW_TYPE) {
                mArticleTitleTextView = (TextView) itemView.findViewById(R.id.readArticleTitle);

            }
            else if (viewType == ArticleAdapter.ISSUE_VIEW_TYPE)
                mArticleTitleTextView = (TextView) itemView.findViewById(R.id.issueTitle);

        }

        public void bindArticle(final IssueArticleItem item) {
            mIssueArticleItem = item;
            mArticleTitleTextView.setText(mIssueArticleItem.text);
            if(getItemViewType() == ArticleAdapter.READ_ARTICLE_VIEW_TYPE || getItemViewType() == ArticleAdapter.UNREAD_ARTICLE_VIEW_TYPE) {

                DateFormat df = DateFormat.getDateInstance();

                title = item.article.getTitle();
                date = df.format(item.article.getIssue().getDate());
                volume = item.article.getIssue().getVolume();
                number = item.article.getIssue().getNumber();
                link = item.article.getUrl();
            }
        }

        @Override
        public void onClick(View v) {
            if(mViewType != ArticleAdapter.ISSUE_VIEW_TYPE) {
                Article article = mIssueArticleItem.article;
                mAdapter.setArticleReadState(article);
                Intent intent = ArticleDetailActivity.newIntent(getActivity(), article.getAlready_known(),
                        article.getAdded_by_report(), article.getImplications(), title, date, volume, number, link);
                startActivity(intent);
            }

        }

    }
}