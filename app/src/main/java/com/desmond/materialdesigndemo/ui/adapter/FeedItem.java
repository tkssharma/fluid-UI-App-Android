package com.desmond.materialdesigndemo.ui.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tsharma3 on 9/8/2015.
 */
public class FeedItem {
    private int id;
    private String name, status, image, profilePic, timeStamp, url;

    public FeedItem() {
    }

    public FeedItem(int id, String name, String image, String status,
                    String profilePic, String timeStamp, String url) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.url = url;
    }

    public static FeedItem fromJson(JSONObject jsonObject) {
        FeedItem items = new FeedItem();
        try {
            // Deserialize json into object fields

            items.setId(jsonObject.getInt("id"));
            items.setName(jsonObject.getString("name"));

            // Image might be null sometimes
            String image = jsonObject.isNull("image") ? null : jsonObject
                    .getString("image");
            items.setImge(image);
            items.setStatus(jsonObject.getString("status"));
            items.setProfilePic(jsonObject.getString("profilePic"));
            items.setTimeStamp(jsonObject.getString("timeStamp"));

            // url might be null sometimes
            String feedUrl = jsonObject.isNull("url") ? null : jsonObject
                    .getString("url");
            items.setUrl(feedUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return items;
    }

    // Decodes array of box office movie json results into business model objects
    public static ArrayList<FeedItem> fromJson(JSONArray jsonArray) {
        ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>(jsonArray.length());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject businessJson = null;
            try {
                businessJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            FeedItem feeddata = FeedItem.fromJson(businessJson);
            if (feeddata != null) {
                feedItems.add(feeddata);
            }
        }

        return feedItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImge() {
        return image;
    }

    public void setImge(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}