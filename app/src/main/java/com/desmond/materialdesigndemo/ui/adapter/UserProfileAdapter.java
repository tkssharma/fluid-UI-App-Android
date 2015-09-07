package com.desmond.materialdesigndemo.ui.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

/**
 * Created by desmond on 31/7/15.
 */
public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    private final int mCellSize;

    private final List<String> mPhotoList;

    private boolean mLockedAnimations = false;
    private int mLastAnimatedItem = -1;

    public UserProfileAdapter(Context context) {
        super();
        mCellSize = Utils.getScreenWidth(context) / 3;
        mPhotoList = Arrays.asList(context.getResources().getStringArray(R.array.user_photos));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        StaggeredGridLayoutManager.LayoutParams layoutParams
                = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.height = mCellSize;
        layoutParams.width = mCellSize;
        layoutParams.setFullSpan(false);
        view.setLayoutParams(layoutParams);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final PhotoViewHolder holder = (PhotoViewHolder) viewHolder;
        Picasso.with(holder.itemView.getContext())
                .load(mPhotoList.get(position))
                .resize(mCellSize, mCellSize)
                .centerCrop()
                .into(holder.ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhoto(holder);
                    }

                    @Override
                    public void onError() {}
                });

        if (mLastAnimatedItem < position) mLastAnimatedItem = position;
    }

    private void animatePhoto(PhotoViewHolder holder) {
        if (!mLockedAnimations) {

            // Prevent animation on the rest of the hidden images upon scrolling down
            if (mLastAnimatedItem == holder.getAdapterPosition()) {
                setDisableAnimations(true);
            }

            long animationDelay = PHOTO_ANIMATION_DELAY + holder.getAdapterPosition() * 30;

            holder.ivPhoto.setScaleY(0F);
            holder.ivPhoto.setScaleX(0F);

            ViewCompat.animate(holder.ivPhoto)
                    .scaleX(1.0F)
                    .scaleY(1.0F)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    public void setDisableAnimations(boolean lockedAnimations) {
        mLockedAnimations = lockedAnimations;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        FrameLayout flRoot;
        ImageView ivPhoto;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
        }
    }
}
