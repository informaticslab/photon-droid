package gov.cdc.mmwrexpress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class KeywordArticleListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "KeywordArticleListFragment";
    public static final String KEYWORD_TEXT = "KEYWORD_TEXT";

    private RecyclerView mArticlesRV;
    private View view;
    private KeywordArticleAdapter mAdapter;
    private String mKeywordText;
    private SwipeRefreshLayout swipeLayout;

    // Factory method for this fragment class. Constructs a new fragment for the given page number.
    public static KeywordArticleListFragment create(String keywordText) {
        KeywordArticleListFragment fragment = new KeywordArticleListFragment();
        Bundle args = new Bundle();
        args.putString(KEYWORD_TEXT, keywordText);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKeywordText = getArguments().getString(KEYWORD_TEXT);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_keyword_article_list, container, false);
            mArticlesRV = (RecyclerView) view.findViewById(R.id.keyword_articles_rv);
            mArticlesRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            mArticlesRV.addItemDecoration(new SimpleDividerItemDecoration(
                    getActivity()
            ));

            updateUI();
        } else {
            // If we are returning from a configuration change:
            // "view" is still attached to the previous view hierarchy
            // so we need to remove it and re-attach it to the current one
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        return view;
    }


    @Override
    public void onRefresh() {
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
            mAdapter = new KeywordArticleAdapter(getActivity(), mKeywordText);
            mArticlesRV.setAdapter(mAdapter);

        } else {
            mAdapter.notifyDataSetChanged();
        }

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
        }

        ;
    };


    private class KeywordArticleItem {

        private String text;
        private Article article;

        private KeywordArticleItem(Article article) {

            this.text = article.getTitle();
            this.article = article;

        }

    }


    private class KeywordArticleAdapter extends RecyclerView.Adapter<KeywordArticleHolder> {

        private static final String TAG = "KeywordArticleAdapter";
        private ArrayList<KeywordArticleItem> listItems;
        private Realm realm;
        private RealmList<Article> mKeywordArticles;
        private Keyword mKeyword;

        private final Context context;


        public KeywordArticleAdapter(Context context, String keywordText) {

            this.context = context;
            //Realm.deleteRealmFile(context);
            this.realm = Realm.getInstance(context);
            Keyword firstKeyword  = realm.where(Keyword.class).equalTo("text", keywordText).findFirst();
            mKeywordArticles = firstKeyword.getArticles();
            Log.d(TAG, "realm path: " + realm.getPath());

            // refresh data from database
            this.refreshData();
        }

        @Override
        public KeywordArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = null;
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            view = layoutInflater.inflate(R.layout.article_list_read_item, parent, false);

            return new KeywordArticleHolder(view, viewType, this);
        }

        public void onBindViewHolder(KeywordArticleHolder holder, int position) {
            KeywordArticleItem article = listItems.get(position);
            holder.bindArticle(article);

        }


        @Override
        public int getItemCount() {
            return listItems.size();
        }

        public void refreshData() {

            KeywordArticleItem item;

            Log.d(TAG, "Keyword articles[] size = " + String.valueOf(mKeywordArticles.size()));
            this.listItems = new ArrayList<KeywordArticleItem>();

            // use sorted articles for list view
            for (Article article : mKeywordArticles) {
                item = new KeywordArticleItem(article);
                listItems.add(item);
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

    private class KeywordArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private KeywordArticleItem mKeywordArticleItem;
        private int mViewType;
        private TextView mArticleTitleTextView;
        private KeywordArticleAdapter mAdapter;


        public KeywordArticleHolder(View itemView, int viewType, KeywordArticleAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            mViewType = viewType;
            mAdapter = adapter;

            mArticleTitleTextView = (TextView) itemView.findViewById(R.id.readArticleTitle);

        }

        public void bindArticle(KeywordArticleItem item) {
            mKeywordArticleItem = item;
            mArticleTitleTextView.setText(mKeywordArticleItem.text);
        }

        @Override
        public void onClick(View v) {

            Article article = mKeywordArticleItem.article;
            mAdapter.setArticleReadState(article);
            Intent intent = ContentActivity.newIntent(getActivity(), article.getAlready_known(),
                    article.getAdded_by_report(), article.getImplications());
            startActivity(intent);

        }

    }
}


