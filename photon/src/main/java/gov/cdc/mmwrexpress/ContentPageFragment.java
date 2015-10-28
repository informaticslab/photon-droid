package gov.cdc.mmwrexpress;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    public static final String BLUE_BOX_IMAGE_ID = "BLUE_BOX_IMAGE_ID";

    private int pageNumber;
    private String title;
    private String text;
    private int mSnackbarStringId;
    private int imageId;


    // Factory method for this fragment class. Constructs a new fragment for the given page number.
    public static ContentPageFragment create(int pageNumber, String title, String text, int imaged_id) {
        ContentPageFragment fragment = new ContentPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(BLUE_BOX_TITLE, title);
        args.putString(BLUE_BOX_TEXT, text);
        args.putInt(BLUE_BOX_IMAGE_ID,imaged_id);

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
        imageId = getArguments().getInt(BLUE_BOX_IMAGE_ID);

        if (pageNumber == 0)
            mSnackbarStringId = R.string.content_page_1;
        else if (pageNumber == 1)
            mSnackbarStringId = R.string.content_page_2;
        else if (pageNumber == 2)
            mSnackbarStringId = R.string.content_page_3;



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
        ((ImageView)rootView.findViewById(R.id.blue_box_image_view)).setImageResource(imageId);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_SUMMARY, Constants.SC_SECTION_SUMMARY);
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return pageNumber;
    }


}
