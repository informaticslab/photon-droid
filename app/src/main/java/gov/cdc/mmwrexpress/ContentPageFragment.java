package gov.cdc.mmwrexpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContentPageFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    public static final String BLUE_BOX_TITLE = "BLUE_BOX_TITLE";
    public static final String BLUE_BOX_TEXT = "BLUE_BOX_TEXT";

    private int pageNumber;
    private String title;
    private String text;

     // Factory method for this fragment class. Constructs a new fragment for the given page number.
    public static ContentPageFragment create(int pageNumber, String title, String text) {
        ContentPageFragment fragment = new ContentPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(BLUE_BOX_TITLE, title);
        args.putString(BLUE_BOX_TEXT, text);

        fragment.setArguments(args);
        return fragment;
    }

    public ContentPageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARG_PAGE);
        title = getArguments().getString(BLUE_BOX_TITLE);
        text = getArguments().getString(BLUE_BOX_TEXT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout containing a title and body text
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_content_page, container, false);

        // set the title view to show the page number.
        ((TextView) rootView.findViewById(R.id.blue_box_title)).setText(title);
        ((TextView) rootView.findViewById(R.id.blue_box_text)).setText(text);


        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return pageNumber;
    }
}
