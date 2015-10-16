package gov.cdc.mmwrexpress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;


public class KeywordSearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "KeywordSearchFragment";


    private RecyclerView mKeywordsRV;
    private View view;
    private KeywordAdapter mAdapter;
    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_keyword_list, container, false);
            mKeywordsRV = (RecyclerView) view.findViewById(R.id.keywords_rv);
            mKeywordsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            mKeywordsRV.addItemDecoration(new SimpleDividerItemDecoration(
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_keyword, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

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
            mAdapter = new KeywordAdapter(getActivity());
            mKeywordsRV.setAdapter(mAdapter);

        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    private class KeywordItem implements Comparable<KeywordItem>{

        private String text;
        private Keyword keyword;


        private KeywordItem(Keyword keyword) {

            this.text = keyword.getText();
            this.keyword = keyword;

        }

        @Override
        public int compareTo(KeywordItem another) {
            int last = this.text.toLowerCase().compareTo(another.text.toLowerCase());
            return last == 0 ? this.text.toLowerCase().compareTo(another.text.toLowerCase()) : last;
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mAdapter.setFilter(query);
        mKeywordsRV.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }



    private class KeywordAdapter extends RecyclerView.Adapter<KeywordHolder> {

        private static final String TAG = "KeywordAdapter";
        private ArrayList<Keyword> mKeywords;
        private ArrayList<KeywordItem> listItems;
        private Context context;
        private RealmResults<Keyword> realmKeywords;



        public KeywordAdapter(Context context) {

            this.context = context;
            //Log.d(TAG, "realm path: " + realm.getPath());

            // refresh data from database
            this.refreshData();
        }

        @Override
        public KeywordHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = null;
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            view = layoutInflater.inflate(R.layout.keyword_list_item, parent, false);

            return new KeywordHolder(view, viewType);
        }

        public void onBindViewHolder(KeywordHolder holder, int position) {
            KeywordItem keyword = listItems.get(position);
            holder.bindKeyword(keyword);

        }
         @Override
        public int getItemCount() {
            return listItems.size();
        }

        public void refreshData() {

            KeywordItem item;
            realmKeywords = realm.where(Keyword.class).findAllSorted("text".toLowerCase(), RealmResults.SORT_ORDER_ASCENDING);

            //Log.d(TAG, "Keywords size = " + String.valueOf(realmKeywords.size()));
            this.mKeywords = new ArrayList<Keyword>();
            this.listItems = new ArrayList<KeywordItem>();

            // use sorted articles for list view
            for (Keyword keyword : realmKeywords) {
                item = new KeywordItem(keyword);
                listItems.add(item);
                    //Log.d(TAG, "Keyword: " + keyword.getText());
            }
            Collections.sort(listItems);
        }


        public void setFilter(String queryText) {

            KeywordItem item;

            realmKeywords = realm.where(Keyword.class).findAllSorted("text".toLowerCase(), RealmResults.SORT_ORDER_ASCENDING);

            listItems = new ArrayList<>();
            //constraint = constraint.toString().toLowerCase();
            for (Keyword keyword: realmKeywords) {
                item = new KeywordItem(keyword);
                if (keyword.getText().toLowerCase().startsWith(queryText.toLowerCase()))
                    listItems.add(item);
            }
            Collections.sort(listItems);
            notifyDataSetChanged();
        }

        @UiThread
        protected void dataSetChanged() {
            refreshData();
            notifyDataSetChanged();
        }

    }

    private class KeywordHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private KeywordItem mKeywordItem;
        private TextView mKeywordTextView;
        private int mViewType;


        public KeywordHolder(View itemView, int viewType) {
            super(itemView);
            itemView.setOnClickListener(this);
            mViewType = viewType;

            mKeywordTextView = (TextView) itemView.findViewById(R.id.keywordTitle);

        }

        public void bindKeyword(KeywordItem item) {
            mKeywordItem = item;
            mKeywordTextView.setText(mKeywordItem.text);
        }

        @Override
        public void onClick(View v) {

            Keyword keyword = mKeywordItem.keyword;
            Intent intent = KeywordArticleListActivity.newIntent(getActivity(), keyword.getText());
            startActivity(intent);

        }

    }

}
