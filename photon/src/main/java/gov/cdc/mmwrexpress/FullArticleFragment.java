package gov.cdc.mmwrexpress;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
public class FullArticleFragment extends Fragment {
    public static final String ARG_PAGE = "page";
    public static final String ARG_LINK = "link";

    private int pageNumber;
    private String link;
    private WebView webView;
    private ProgressBar progressBar;
    private ConnectivityManager cm;
    private NetworkInfo activeNetwork;


    public static FullArticleFragment create(int pageNumber, String link){
        FullArticleFragment fragment = new FullArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_LINK, link);
        fragment.setArguments(args);
        return fragment;
    }

    public FullArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARG_PAGE);
        link = getArguments().getString(ARG_LINK);
        //Check connection
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_full_article, container, false);

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

        // Add Download listener in case user clicks link to a file as most MMWR articles have a link to a PDF file.
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // Enable zoom controls
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        // Enable webview browser back navigation
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });

        //
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("http")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else return false;
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkConnectionAndLoadPage(link);
        AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_FULL, Constants.SC_SECTION_DETAILS);
    }

    private void checkConnectionAndLoadPage(final String link){
        activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null){
            webView.loadUrl(link);
        }
        else {
            Snackbar.make(getActivity().findViewById(R.id.full_article_webview), "No internet connection detected", Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(getResources().getColor(R.color.light_yellow))
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkConnectionAndLoadPage(link);
                        }
                    }).show();
        }
    }
}
