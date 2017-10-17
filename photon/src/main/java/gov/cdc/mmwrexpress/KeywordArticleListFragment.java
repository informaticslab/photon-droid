package gov.cdc.mmwrexpress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;


/**KeywordArticleListFragment.java
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class KeywordArticleListFragment extends Fragment {

    private static final String TAG = "KeywordArticleListFragment";
    public static final String KEYWORD_TEXT = "KEYWORD_TEXT";

    private RecyclerView mArticlesRV;
    private View view;
    private KeywordArticleAdapter mAdapter;
    private String mKeywordText;
    private Realm realm;

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
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                updateUI();
            }
        });
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_SEARCH_KEYWORD_ARTICLES, Constants.SC_SECTION_SEARCH);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.removeAllChangeListeners();
        realm.close();
    }

    private void updateUI() {

        if (mAdapter == null) {
            // read stored articles
            mAdapter = new KeywordArticleAdapter(getActivity(), mKeywordText);
            mArticlesRV.setAdapter(mAdapter);

        } else {
            mAdapter.dataSetChanged();
        }

    }

    private class KeywordArticleItem implements Comparable<KeywordArticleItem>{

        private String text;
        private Article article;
        private Issue issue;

        private KeywordArticleItem(Article article) {

            this.text = article.getTitle();
            this.article = article;
            this.issue = article.getIssue();

        }

        @Override
        public int compareTo(KeywordArticleItem another) {
            int last = this.issue.getDate().compareTo(another.issue.getDate());
            return last == 0 ? last: this.issue.getDate().compareTo(another.issue.getDate()) ;

        }
    }


    private class KeywordArticleAdapter extends RecyclerView.Adapter<KeywordArticleHolder> {

        private static final String TAG = "KeywordArticleAdapter";
        private ArrayList<KeywordArticleItem> listItems;
        private RealmList<Article> mKeywordArticles;
        private String keywordText;
        private Keyword mKeyword;

        private final Context context;


        public KeywordArticleAdapter(Context context, String keywordText) {

            this.context = context;
            this.keywordText = keywordText;
            //Realm.deleteRealmFile(context);

            //Log.d(TAG, "realm path: " + realm.getPath());

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
            Keyword firstKeyword  = realm.where(Keyword.class).equalTo("text", keywordText).findFirst();
            if(firstKeyword != null) {
                mKeywordArticles = firstKeyword.getArticles();
            } else {
                mKeywordArticles = null;
            }

            KeywordArticleItem item;

            //Log.d(TAG, "Keyword articles[] size = " + String.valueOf(mKeywordArticles.size()));
            this.listItems = new ArrayList<KeywordArticleItem>();

            if(mKeywordArticles != null) {
                // use sorted articles for list view
                for (Article article : mKeywordArticles) {
                    item = new KeywordArticleItem(article);
                    listItems.add(item);
                }
                Collections.sort(listItems, Collections.reverseOrder());
            }
        }

        @UiThread
        protected void dataSetChanged() {
            refreshData();
            notifyDataSetChanged();
        }

        public void setArticleReadState(Article article) {
            realm.beginTransaction();

            article.setUnread(false);

            realm.commitTransaction();
        }
    }

    private class KeywordArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private KeywordArticleItem mKeywordArticleItem;
        private int mViewType;
        private TextView mArticleTitleTextView;
        private KeywordArticleAdapter mAdapter;

        private String title;
        private String date;
        private int volume;
        private int number;
        private String link;


        public KeywordArticleHolder(View itemView, int viewType, KeywordArticleAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            mViewType = viewType;
            mAdapter = adapter;

            mArticleTitleTextView = (TextView) itemView.findViewById(R.id.readArticleTitle);
        }

        public void bindArticle(final KeywordArticleItem item) {
            mKeywordArticleItem = item;
            mArticleTitleTextView.setText(mKeywordArticleItem.text);
            DateFormat df = DateFormat.getDateInstance();

            title = item.article.getTitle();
            date = df.format(item.article.getIssue().getDate());
            volume = item.article.getIssue().getVolume();
            number = item.article.getIssue().getNumber();
            link = item.article.getUrl();

        }

        @Override
        public void onClick(View v) {

            Article article = mKeywordArticleItem.article;
            mAdapter.setArticleReadState(article);
            Intent intent = ContentActivity.newIntent(getActivity(), article.getAlready_known(),
                    article.getAdded_by_report(), article.getImplications(), title, date, volume, number, link);
            startActivity(intent);

        }

    }
}


