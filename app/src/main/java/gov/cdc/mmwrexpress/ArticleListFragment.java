package gov.cdc.mmwrexpress;


import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ArticleListFragment extends Fragment implements OnItemClickListener, OnRefreshListener {

    private ProgressBar progressBar;
    private ListView listView;
    private View view;
    private ArticleListAdapter adapter;
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
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            listView = (ListView) view.findViewById(R.id.listView);
            listView.setOnItemClickListener(this);
            swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            swipeLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeLayout.setRefreshing(true);
                    startService();
                }
            });

            readStoredArticles();
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

    private void startService() {
        Intent intent = new Intent(getActivity(), RssService.class);
        intent.putExtra(RssService.RECEIVER, resultReceiver);
        getActivity().startService(intent);
    }

    private void readStoredArticles() {

        // read stored articles
        this.adapter = new ArticleListAdapter(getActivity());
        listView.setAdapter(adapter);

    }


    private void refreshFromStoredArticles() {

        // reread stored articles
        this.adapter.notifyDataSetChanged();
        listView.invalidateViews();

    }

    /**
     * Once the {@link RssService} finishes its task, the result is sent to this
     * ResultReceiver.
     */
    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            progressBar.setVisibility(View.GONE);
            List<ArticleListItem> items = (List<ArticleListItem>) resultData.getSerializable(RssService.ITEMS);
            if (items != null) {
                refreshFromStoredArticles();
            } else {
                Toast.makeText(getActivity(), "An error occurred while accessing the CDC feed.",
                        Toast.LENGTH_LONG).show();
            }

            swipeLayout.setRefreshing(false);
        };
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArticleListAdapter adapter = (ArticleListAdapter) parent.getAdapter();
        Article article = (Article) adapter.getItem(position);

        try {
            // ((OnArticleSelectedListener) getActivity()).onArticleSelected(item.getArticleTitle());
            ((OnArticleSelectedListener) getActivity()).onArticleSelected(article.getAlready_known(),
                    article.getAdded_by_report(), article.getImplications());

        } catch (ClassCastException cce) {


        }
    }

    public interface OnArticleSelectedListener {
        public void onArticleSelected(String known, String added, String implications);
    }
}