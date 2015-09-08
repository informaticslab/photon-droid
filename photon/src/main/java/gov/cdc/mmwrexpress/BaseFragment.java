package gov.cdc.mmwrexpress;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment {

    private static final String TAG = "ArticleListFragment";
    private View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override public void onRefresh() {
//        super.o
//    }

    @Override
    public void onPause() {
        //state
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

}

