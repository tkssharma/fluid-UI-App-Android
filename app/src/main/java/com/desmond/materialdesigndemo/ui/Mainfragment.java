package com.desmond.materialdesigndemo.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.adapter.FeedItem;
import com.desmond.materialdesigndemo.ui.adapter.FeedListAdaptor;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Mainfragment extends Fragment {

    public static final String MOVIE_DETAIL_KEY = "json";
    private static final String TAG = Mainfragment.class.getSimpleName();
    ArrayList<FeedItem> FeedListItems;
    private ListView mFeedItems;
    private FeedListAdaptor feedAdaptor;
    private HttpClient client;

    public Mainfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.mainfragment, container, false);
        FeedListItems = new ArrayList<FeedItem>();
        feedAdaptor = new FeedListAdaptor(getActivity(), FeedListItems);
        mFeedItems = (ListView) rootView.findViewById(R.id.FeedItems);
        setupMovieSelectedListener();

        fetchBoxOfficeMovies("", "");
        return rootView;
    }

    public void setupMovieSelectedListener() {
        mFeedItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int position, long rowId) {
               /* Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra(MOVIE_DETAIL_KEY, adapterMovies.getItem(position));
                startActivity(i);*/
            }
        });
    }

    private void fetchBoxOfficeMovies(String url, String query) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading...");
        dialog.setMessage("Please wait.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        client = new HttpClient();
        HttpClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, JSONObject body) {
                JSONArray items = null;
                try {
                    Log.d("", body.toString());
                    // Get the movies json array
                    items = body.getJSONArray("feed");
                    // Parse json array into array of model objects
                    ArrayList<FeedItem> itemData = FeedItem.fromJson(items);
                    // Load model objects into the adapter which displays them
                    if (feedAdaptor != null) {
                        feedAdaptor.clear();
                    }
                    FeedListItems.addAll(itemData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mFeedItems.setAdapter(feedAdaptor);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });


    }

}
