package gov.cdc.mmwrexpress;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;



public class EulaFragment extends Fragment{

    private static final String TAG = "EulaFragment";

    private View view;
    private Button mAgreeButton;
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_eula, container, false);
            mWebView = (WebView)view.findViewById(R.id.webview);

            mWebView.loadUrl("file:///android_asset/eula.html");

            mAgreeButton = (Button) view.findViewById(R.id.btnAgree);
            mAgreeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ArticleListActivity.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("agreedToEula", true);
                    editor.commit();
                    getFragmentManager().popBackStack();
                }
            });
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

    }


}
