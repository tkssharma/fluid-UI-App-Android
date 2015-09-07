package com.desmond.materialdesigndemo.ui.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

/**
 * Created by desmond on 29/7/15.
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int mItemsCount = 0;
    private int mLastAnimatedPosition = -1;
    private int mAvatarSize;

    private boolean mAnimationsLocked = false;
    private boolean mDelayEnterAnimation = true;

    public CommentsAdapter(Context context) {
        mAvatarSize = context.getResources().getDimensionPixelSize(R.dimen.btn_fab_size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        CommentViewHolder holder = (CommentViewHolder) viewHolder;
        switch (position % 3) {
            case 0:
                holder.mTvComment.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit.");
                break;
            case 1:
                holder.mTvComment.setText("Cupcake ipsum dolor sit amet bear claw.");
                break;
            case 2:
                holder.mTvComment.setText("Cupcake ipsum dolor sit. Amet gingerbread cupcake. Gummies ice cream dessert icing marzipan apple pie dessert sugar plum.");
                break;
        }

        Picasso.with(viewHolder.itemView.getContext())
                .load(R.drawable.ic_launcher)
                .centerCrop()
                .resize(mAvatarSize, mAvatarSize)
                .transform(new RoundedTransformation())
                .into(holder.mIvUserAvatar);
    }

    @Override
    public int getItemCount() {
        return mItemsCount;
    }

    public void updateItems() {
        mItemsCount = 10;
        notifyDataSetChanged();
    }

    public void addItem() {
        mItemsCount++;
        notifyItemInserted(mItemsCount - 1);
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        mAnimationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        mDelayEnterAnimation = delayEnterAnimation;
    }

    private void runEnterAnimation(View view, int position) {
        if (mAnimationsLocked) return;

        if (position > mLastAnimatedPosition) {
            mLastAnimatedPosition = position;

            view.setTranslationY(100);
            view.setAlpha(0.1F);

            ViewCompat.animate(view)
                    .translationY(0)
                    .alpha(1.0F)
                    .setStartDelay(mDelayEnterAnimation ? 20 * position : 0)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(300)
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(View view) {
                            mAnimationsLocked = true;
                        }
                    })
                    .start();
        }
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView mIvUserAvatar;
        TextView mTvComment;

        public CommentViewHolder(View itemView) {
            super(itemView);
            mIvUserAvatar = (ImageView) itemView.findViewById(R.id.ivUserAvatar);
            mTvComment = (TextView) itemView.findViewById(R.id.tvComment);
        }
    }
}
