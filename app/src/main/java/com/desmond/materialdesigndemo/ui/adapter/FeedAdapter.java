package com.desmond.materialdesigndemo.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.Utils;
import com.desmond.materialdesigndemo.ui.view.SendingProgressView;
import com.desmond.materialdesigndemo.ui.view.SquareFrameLayout;
import com.desmond.materialdesigndemo.ui.view.SquareImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by desmond on 29/7/15.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener, GestureDetector.OnDoubleTapListener {

    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int VIEW_TYPE_LOADER = 2;

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private int mLastAnimatedPosition = -1;
    private int mItemsCount = 0;

    private final SparseIntArray mLikesCount = new SparseIntArray();
    private final Map<RecyclerView.ViewHolder, AnimatorSet> mLikeAnimations = new HashMap<>();
//    private final List<Integer> mLikedPositions = new ArrayList<>();
    private final SparseBooleanArray mLikedPositions = new SparseBooleanArray();

    private boolean mShowLoadingView = false;
    private int mLoadingViewSize = Utils.dpToPx(200);

    private OnFeedItemClickListener mOnFeedItemClickListener;

    public interface OnFeedItemClickListener {
        void onCommentsClick(View view, int position);
        void onMoreClick(View view, int position);
        void onProfileClick(View view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        final CellFeedViewHolder holder = new CellFeedViewHolder(view);
        switch (viewType) {
            case VIEW_TYPE_DEFAULT:{
                holder.btnComments.setOnClickListener(this);
                holder.btnLike.setOnClickListener(this);
                holder.ivFeedCenter.setOnClickListener(this);
                holder.btnMore.setOnClickListener(this);
                holder.ivUserProfile.setOnClickListener(this);
                break;
            }
            case VIEW_TYPE_LOADER: {
                View bgView = new View(parent.getContext());
                bgView.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                bgView.setBackgroundColor(0x77ffffff);
                holder.vImageRoot.addView(bgView);
                holder.mVProgressBg = bgView;

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mLoadingViewSize, mLoadingViewSize);
                params.gravity = Gravity.CENTER;
                SendingProgressView sendingProgressView = new SendingProgressView(parent.getContext());
                sendingProgressView.setLayoutParams(params);
                holder.vImageRoot.addView(sendingProgressView);
                holder.mVSendingProgress = sendingProgressView;
                break;
            }
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);

        CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        if (getItemViewType(position) == VIEW_TYPE_DEFAULT) {
            bindDefaultFeedItem(position, holder);
        } else if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(holder);
        }
    }

    private void runEnterAnimation(View view, int position) {
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > mLastAnimatedPosition) {
            mLastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(view.getContext()));
            ViewCompat.animate(view)
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(700)
                    .start();
        }
    }

    private void bindDefaultFeedItem(int position, CellFeedViewHolder holder) {
        if (position % 2 == 0) {
            holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_1);
            holder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_1);
        } else {
            holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_2);
            holder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_2);
        }
        updateLikesCounter(holder, false);
        updateHeartButton(holder, false);

        holder.btnComments.setTag(position);
        holder.ivFeedCenter.setTag(holder);
        holder.btnLike.setTag(holder);
        holder.btnMore.setTag(position);

        if (mLikeAnimations.containsKey(holder)) {
            mLikeAnimations.get(holder).cancel();
            resetLikeAnimationState(holder);
        }
        resetLikeAnimationState(holder);
    }

    private void bindLoadingFeedItem(final CellFeedViewHolder holder) {
        holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_1);
        holder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_1);

        holder.mVSendingProgress.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.mVSendingProgress.getViewTreeObserver().removeOnPreDrawListener(this);
                holder.mVSendingProgress.setScaleX(1F);
                holder.mVSendingProgress.setScaleY(1F);
                holder.mVProgressBg.setAlpha(1F);
                holder.mVSendingProgress.simulateProgress();
                return true;
            }
        });
        holder.mVSendingProgress.setOnLoadingFinishedListener(new SendingProgressView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                ViewCompat.animate(holder.mVSendingProgress)
                        .scaleY(0).scaleX(0).setDuration(200).setStartDelay(100);
                ViewCompat.animate(holder.mVProgressBg)
                        .alpha(0.1F).setDuration(200).setStartDelay(100)
                        .setListener(new ViewPropertyAnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(View view) {
                                mShowLoadingView = false;
                                notifyItemChanged(0);
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemsCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    public void updateItems() {
        mItemsCount = 10;
        fillLikesWithRandomValues();
        notifyDataSetChanged();
    }

    private void fillLikesWithRandomValues() {
        for (int i = 0; i < getItemCount(); i++) {
            mLikesCount.put(i, i + 1);
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        mOnFeedItemClickListener = onFeedItemClickListener;
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.btnComments: {
                if (mOnFeedItemClickListener != null) {
                    mOnFeedItemClickListener.onCommentsClick(v, (Integer) v.getTag());
                }
                break;
            }
            case R.id.btnLike: {
                CellFeedViewHolder holder = (CellFeedViewHolder) v.getTag();
                boolean isLiked = mLikedPositions.get(holder.getAdapterPosition(), false);
                mLikedPositions.put(holder.getAdapterPosition(), !isLiked);
                updateLikesCounter(holder, true);
                updateHeartButton(holder, true);
                break;
            }
            case R.id.btnMore: {
                if (mOnFeedItemClickListener != null) {
                    mOnFeedItemClickListener.onMoreClick(v, (Integer) v.getTag());
                }
                break;
            }
            case R.id.ivFeedCenter: {
                CellFeedViewHolder holder = (CellFeedViewHolder) v.getTag();
                boolean isLiked = mLikedPositions.get(holder.getAdapterPosition(), false);
                mLikedPositions.put(holder.getAdapterPosition(), !isLiked);
                updateLikesCounter(holder, true);
                animatePhotoLike(holder);
                updateHeartButton(holder, false);
                break;
            }
            case R.id.ivUserProfile: {
                if (mOnFeedItemClickListener != null) {
                    mOnFeedItemClickListener.onProfileClick(v);
                }
                break;
            }
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private void updateLikesCounter(CellFeedViewHolder holder, boolean animated) {
        int currentLikesCount = mLikesCount.get(holder.getAdapterPosition());
        boolean isLiked = mLikedPositions.get(holder.getAdapterPosition(), false);
        if (isLiked) {
            // Increase like
            mLikesCount.put(holder.getAdapterPosition(), ++currentLikesCount);
        } else {
            // Decrease like
            mLikesCount.put(holder.getAdapterPosition(), --currentLikesCount);
        }

        String likesCountText = holder.itemView.getContext().getResources().getQuantityString(
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );

        if (animated) {
            // Update with animation
            holder.tsLikesCounter.setText(likesCountText);
        } else {
            // Update with no animation
            holder.tsLikesCounter.setCurrentText(likesCountText);
        }
    }

    private void updateHeartButton(final CellFeedViewHolder holder, boolean animated) {
        boolean isLiked = mLikedPositions.get(holder.getAdapterPosition(), false);
        if (isLiked) {
            // Increase like
            if (animated) {
                if (!mLikeAnimations.containsKey(holder)) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    mLikeAnimations.put(holder, animatorSet);

                    // Animate rotation
                    ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0F, 360F);
                    rotationAnim.setDuration(300);
                    rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                    // Animate bounce X and Y
                    ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2F, 1F);
                    bounceAnimX.setDuration(300);
                    bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                    ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2F, 1F);
                    bounceAnimY.setDuration(300);
                    bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                    bounceAnimY.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            holder.btnLike.setScaleX(0.2F);
                            holder.btnLike.setScaleY(0.2F);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                        }
                    });

                    animatorSet.play(rotationAnim);
                    animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            resetLikeAnimationState(holder);
                        }
                    });

                    animatorSet.start();
                }
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
            }
        } else {
            // Decrease like
            holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
        }
    }

    private void resetLikeAnimationState(CellFeedViewHolder holder) {
        mLikeAnimations.remove(holder);
        holder.vBgLike.setVisibility(View.GONE);
        holder.ivLike.setVisibility(View.GONE);
    }

    private void animatePhotoLike(final CellFeedViewHolder holder) {
        boolean isLiked = mLikedPositions.get(holder.getAdapterPosition(), false);
        if (isLiked) {
            // Increase like
            if (!mLikeAnimations.containsKey(holder)) {
                holder.vBgLike.setVisibility(View.VISIBLE);
                holder.ivLike.setVisibility(View.VISIBLE);

                holder.vBgLike.setScaleY(0.1F);
                holder.vBgLike.setScaleX(0.1F);
                holder.vBgLike.setAlpha(1F);
                holder.ivLike.setScaleY(0.1F);
                holder.ivLike.setScaleX(0.1F);

                AnimatorSet animatorSet = new AnimatorSet();
                mLikeAnimations.put(holder, animatorSet);

                ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleY", 0.1F, 1F);
                bgScaleYAnim.setDuration(200);
                bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
                ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleX", 0.1F, 1F);
                bgScaleXAnim.setDuration(200);
                bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
                ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.vBgLike, "alpha", 1F, 0F);
                bgAlphaAnim.setDuration(200);
                bgAlphaAnim.setStartDelay(150);
                bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

                ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 0.1F, 1F);
                imgScaleUpYAnim.setDuration(300);
                imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
                ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 0.1F, 1F);
                imgScaleUpXAnim.setDuration(300);
                imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

                ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 1F, 0F);
                imgScaleDownYAnim.setDuration(300);
                imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
                ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 1F, 0F);
                imgScaleDownXAnim.setDuration(300);
                imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
                animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        }
    }

    public void showLoadingView() {
        mShowLoadingView = true;
        notifyItemChanged(0);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        SquareImageView ivFeedCenter;
        ImageView ivFeedBottom;
        ImageButton btnComments;
        ImageButton btnLike;
        ImageButton btnMore;
        TextSwitcher tsLikesCounter;
        View vBgLike;
        ImageView ivLike;
        ImageView ivUserProfile;

        SquareFrameLayout vImageRoot;
        SendingProgressView mVSendingProgress;
        View mVProgressBg;

        public CellFeedViewHolder(View view) {
            super(view);
            ivFeedCenter = (SquareImageView) view.findViewById(R.id.ivFeedCenter);
            ivFeedBottom = (ImageView) view.findViewById(R.id.ivFeedBottom);
            btnComments = (ImageButton) view.findViewById(R.id.btnComments);
            btnLike = (ImageButton) view.findViewById(R.id.btnLike);
            btnMore = (ImageButton) view.findViewById(R.id.btnMore);
            tsLikesCounter = (TextSwitcher) view.findViewById(R.id.tsLikesCounter);
            vBgLike = view.findViewById(R.id.vBgLike);
            ivLike = (ImageView) view.findViewById(R.id.ivLike);
            ivUserProfile = (ImageView) view.findViewById(R.id.ivUserProfile);
            vImageRoot = (SquareFrameLayout) view.findViewById(R.id.vImageRoot);
        }
    }
}
