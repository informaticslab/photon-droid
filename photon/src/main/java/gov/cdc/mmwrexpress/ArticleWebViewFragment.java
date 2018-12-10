package gov.cdc.mmwrexpress;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleWebViewFragment extends Fragment {
    public static final String ARG_PAGE = "page";
    public static final String ARG_LINK = "link";

    private int pageNumber;
    private String link;
    private WebView webView;
    private ProgressBar progressBar;


    public static ArticleWebViewFragment create(int pageNumber, String link) {
        ArticleWebViewFragment fragment = new ArticleWebViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_LINK, link);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleWebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARG_PAGE);
        link = getArguments().getString(ARG_LINK);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.article_webview_fragment, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.full_article_progress_bar);
        progressBar.setMax(100);
        webView = (WebView) rootView.findViewById(R.id.full_article_webview);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }

        });
        webView.getSettings()
                .setJavaScriptEnabled(true);

        // Enable zoom controls
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        webView.loadUrl(link);
    }
}
