package com.desmond.materialdesigndemo.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.Utils;
import com.desmond.materialdesigndemo.ui.adapter.CommentsAdapter;
import com.desmond.materialdesigndemo.ui.view.SendCommentButton;

public class CommentsActivity extends BaseActivity implements SendCommentButton.OnSendClickListener {

    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    LinearLayout mContentRoot;
    LinearLayout mAddComment;
    RecyclerView mRvComment;
    EditText mEtComment;
    SendCommentButton mBtnSendComment;

    private CommentsAdapter mCommentsAdapter;
    private int mDrawingStartLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mContentRoot = (LinearLayout) findViewById(R.id.contentRoot);
        mAddComment = (LinearLayout) findViewById(R.id.addComment);
        mRvComment = (RecyclerView) findViewById(R.id.rvComments);
        mEtComment = (EditText) findViewById(R.id.etComment);
        mBtnSendComment = (SendCommentButton) findViewById(R.id.btnSendComment);

        setupToolbar();
        setupComments();
        setupSendCommentBtn();

        mDrawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            mContentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                // Called when all views in the tree have been measured and given a frame, but before drawing
                @Override
                public boolean onPreDraw() {
                    mContentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
    }

    private void setupComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvComment.setLayoutManager(linearLayoutManager);
        mRvComment.setHasFixedSize(true);

        mCommentsAdapter = new CommentsAdapter(this);
        mRvComment.setAdapter(mCommentsAdapter);
        mRvComment.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRvComment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mCommentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    private void setupSendCommentBtn() {
        mBtnSendComment.setOnSendClickListener(this);
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            mCommentsAdapter.addItem();
            mCommentsAdapter.setAnimationsLocked(false);
            mCommentsAdapter.setDelayEnterAnimation(false);
            mRvComment.smoothScrollBy(
                    0, mRvComment.getChildAt(0).getHeight() * mCommentsAdapter.getItemCount());

            mEtComment.setText(null);
            mBtnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
        }
    }

    private void startIntroAnimation() {
        mContentRoot.setScaleY(0.1F);
        mContentRoot.setPivotY(mDrawingStartLocation);
        mAddComment.setTranslationY(100);

        ViewCompat.animate(mContentRoot)
                .scaleY(1.0F)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        animateContent();
                    }
                }).start();
    }

    private void animateContent() {
        mCommentsAdapter.updateItems();
        ViewCompat.animate(mAddComment)
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(mEtComment.getText())) {
            mBtnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getToolbar(), 0);
        ViewCompat.animate(mContentRoot)
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }
}
