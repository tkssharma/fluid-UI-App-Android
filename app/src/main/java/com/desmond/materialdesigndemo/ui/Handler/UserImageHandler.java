package com.desmond.materialdesigndemo.ui.Handler;

import android.util.Log;

import com.desmond.materialdesigndemo.ui.HttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by giriraj on 8/9/15.
 */
public class UserImageHandler {
    public static final String MOVIE_DETAIL_KEY = "movie";
    private static final String TAG = UserImageHandler.class.getSimpleName();

    private HttpClient client;
    private String imageUrl;

    public String getProfileImageUrl(String url) {

        client = new HttpClient();
        client.get(url,null ,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, JSONObject body) {
                JSONArray items = null;
                try {
                    JSONObject urlData = body.getJSONObject("data");
                    imageUrl = urlData.getString("url");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        });
        return imageUrl;
    }
}
