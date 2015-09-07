package com.desmond.materialdesigndemo.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desmond.materialdesigndemo.R;

/**
 * Created by desmond on 2/8/15.
 */
public class PhotoFiltersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int mItemCount = 12;

    public PhotoFiltersAdapter() {
        super();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_filter, parent, false);
        return new PhotoFilerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {}

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    public static class PhotoFilerViewHolder extends RecyclerView.ViewHolder {

        public PhotoFilerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
