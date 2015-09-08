package com.desmond.materialdesigndemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.Utils;
import com.desmond.materialdesigndemo.ui.adapter.FeedAdapter;
import com.desmond.materialdesigndemo.ui.fbUser;
import com.desmond.materialdesigndemo.ui.view.FeedContextMenu;
import com.desmond.materialdesigndemo.ui.view.FeedContextMenuManager;
import com.squareup.picasso.Picasso;

public class MainActivity extends BaseDrawerActivity
        implements FeedAdapter.OnFeedItemClickListener, FeedContextMenu.OnFeedContextMenuItemClickListener {

    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;
    CoordinatorLayout mClContent;
    TextView user_name;
    ImageView profile;
    FloatingActionButton mFabCreate;
    RecyclerView mRvFeed;
    private int mAvatarSize;
    private ImageView mIvMenuUserProfilePhoto;
    private FeedAdapter mFeedAdapter;

    private boolean mPendingIntroAnimation;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equalsIgnoreCase(intent.getAction())) {
            showFeedLoadingItemDelayed();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                mRvFeed.smoothScrollToPosition(0);
                mRvFeed.scrollToPosition(0);
                mFeedAdapter.showLoadingView();
            }
        }, 500);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClContent = (CoordinatorLayout) findViewById(R.id.content);
        mFabCreate = (FloatingActionButton) findViewById(R.id.btnCreate);
        mIvMenuUserProfilePhoto = (ImageView) mNaviView.findViewById(R.id.ivMenuUserProfilePhoto);
        username.setText(fbUser.name);
        Picasso.with(this)
                .load(fbUser.imageUri)
                .into(mIvMenuUserProfilePhoto);
        setupToolbar();
        setupFeed();

        if (savedInstanceState == null) {
            mPendingIntroAnimation = true;
        } else {
            mFeedAdapter.updateItems();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (mPendingIntroAnimation) {
            mPendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void setupFeed() {
        mRvFeed = (RecyclerView) findViewById(R.id.rvFeed);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            // Increase the amount of extra space that should be laid out by LayoutManager ahead
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        mRvFeed.setLayoutManager(linearLayoutManager);

        mFeedAdapter = new FeedAdapter();
        mFeedAdapter.setOnFeedItemClickListener(this);
        mRvFeed.setAdapter(mFeedAdapter);
        mRvFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @Override
    public void onCommentsClick(View view, int position) {

    }

    @Override
    public void onMoreClick(View view, int position) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(view, position, this);
    }

    @Override
    public void onProfileClick(View view) {
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;
        UserProfileActivity.startUserProfileFromLocation(startingLocation, MainActivity.this);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    private void startIntroAnimation() {
        mFabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionBarSize = Utils.dpToPx(56);
        mToolBar.setTranslationY(-actionBarSize);
        mIvLogo.setTranslationY(-actionBarSize);
        mInboxMenuItem.getActionView().setTranslationY(-actionBarSize);

        ViewCompat.animate(mToolBar)
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);

        ViewCompat.animate(mIvLogo)
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);

        ViewCompat.animate(mInboxMenuItem.getActionView())
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        ViewCompat.animate(mFabCreate)
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.0F))
                .setDuration(ANIM_DURATION_FAB)
                .setStartDelay(300)
                .start();

        mFeedAdapter.updateItems();
    }

    public void startTakePhoto(View view) {
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;
        // TakePhotoActivity.startCameraFromLocation(startingLocation, MainActivity.this);
        overridePendingTransition(0, 0);
    }
}
