package gov.cdc.mmwrexpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.v4.app.Fragment;

/**ArticleSummaryFragment.java
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class ArticleSummaryFragment extends Fragment {

    public static final String KNOWN_TEXT = "KNOWN_TEXT";
    public static final String ADDED_TEXT = "ADDED_TEXT";
    public static final String IMPLICATIONS_TEXT = "IMPLICATIONS_TEXT";


    // Images for each content pages
    private int known_image_id = R.drawable.known_icon;
    private int added_image_id = R.drawable.added_icon;
    private int implications_image_id = R.drawable.implications_icon;

    private String knownText;
    private String addedText;
    private String implicationsText;


    // Factory method for this fragment class. Constructs a new fragment for the given page number.
    public static ArticleSummaryFragment create(String known, String added, String implications) {
        ArticleSummaryFragment fragment = new ArticleSummaryFragment();
        Bundle args = new Bundle();
        args.putString(KNOWN_TEXT, known);
        args.putString(ADDED_TEXT, added);
        args.putString(IMPLICATIONS_TEXT, implications);

        fragment.setArguments(args);
        return fragment;
    }

    public ArticleSummaryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        knownText = getArguments().getString(KNOWN_TEXT);
        addedText = getArguments().getString(ADDED_TEXT);
        implicationsText = getArguments().getString(IMPLICATIONS_TEXT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout containing a title and body text
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.article_summary_fragment, container, false);

        // set the title view to show the page number.
        ((TextView) rootView.findViewById(R.id.known_text)).setText(knownText);
        ((TextView) rootView.findViewById(R.id.added_text)).setText(addedText);
        ((TextView)rootView.findViewById(R.id.implications_text)).setText(implicationsText);


        return rootView;
    }
}
