package gov.cdc.mmwrexpress;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.text.DateFormat;



public class ArticleDetailFragment extends Fragment {
    private String title;
    private String date;
    private int volume;
    private int number;
    private String link;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ArticleDetailFragment.
     */
    public static ArticleDetailFragment newInstance(String title, String pubDate, int volume, int number, String link ) {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        args.putString("article_title", title);
        args.putString("publication_date",pubDate);
        args.putInt("volume", volume);
        args.putInt("number", number);
        args.putString("link", link);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("article_title");
            date = getArguments().getString("publication_date");
            volume = getArguments().getInt("volume", -1);
            number = getArguments().getInt("number", -1);
            link = getArguments().getString("link");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        TextView titleText = (TextView) view.findViewById(R.id.article_title);
        TextView published = (TextView) view.findViewById(R.id.article_date);
        TextView volumeText = (TextView) view.findViewById(R.id.volume);
        TextView numberText = (TextView) view.findViewById(R.id.number);
        Button articleLink = (Button) view.findViewById(R.id.link);

        titleText.setText(title);
        published.setText("Published on " +date);
        volumeText.setText("Volume " +volume);
        numberText.setText("Number " +number);
        articleLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return view;
    }
    private void share(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Dummy text");
        startActivity(shareIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
