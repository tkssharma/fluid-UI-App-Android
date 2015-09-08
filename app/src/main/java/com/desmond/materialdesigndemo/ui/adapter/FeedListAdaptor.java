package com.desmond.materialdesigndemo.ui.adapter;

/**
 * Created by tsharma3 on 9/8/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.materialdesigndemo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedListAdaptor extends ArrayAdapter<FeedItem> {
    public FeedListAdaptor(Context context, ArrayList<FeedItem> aFeedItem) {
        super(context, 0, aFeedItem);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FeedItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_feed, null);
        }


        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        TextView txtmessage = (TextView) convertView.findViewById(R.id.txtStatusMsg);
        ImageView coverimage = (ImageView) convertView.findViewById(R.id.feedImage1);
        ImageView profilePic = (ImageView) convertView.findViewById(R.id.profilePic);

        // Populate the data into the template view using the data object
        name.setText(item.getName());
        timestamp.setText(item.getTimeStamp());
        txtmessage.setText(item.getStatus());
        Picasso.with(getContext()).load(item.getUrl()).into(coverimage);
        Picasso.with(getContext()).load(item.getProfilePic()).into(profilePic);
        // Return the completed view to render on screen
        return convertView;
    }
}

