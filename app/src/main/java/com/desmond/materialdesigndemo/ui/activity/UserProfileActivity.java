package com.desmond.materialdesigndemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.adapter.UserProfileAdapter;
import com.desmond.materialdesigndemo.ui.utils.CircleTransformation;
import com.desmond.materialdesigndemo.ui.view.RevealBackgroundView;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends BaseActivity implements RevealBackgroundView.OnStateChangeListener {

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    RevealBackgroundView mRevealBackground;

    RecyclerView mRvUserProfile;
    TabLayout mTlUserProfileTabs;

    ImageView mIvUserProfilePhoto;

    Button mBtnFollow;

    View mVUserDetails;
    View mVUserStats;
    View mVUserProfileRoot;

    private int mAvatarSize;
    private String mProfilePhoto;
    private UserProfileAdapter mUserPhotosAdapter;

    public static void startUserProfileFromLocation(int[] startingLocation, BaseActivity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAvatarSize= getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        mProfilePhoto = getString(R.string.user_profile_photo);
        mIvUserProfilePhoto = (ImageView) findViewById(R.id.ivUserProfilePhoto);
        Picasso.with(this)
                .load(mProfilePhoto)
                .resize(mAvatarSize, mAvatarSize)
                .placeholder(R.drawable.img_circle_placeholder)
                .transform(new CircleTransformation())
                .centerCrop()
                .into(mIvUserProfilePhoto);

        mVUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        mVUserDetails = findViewById(R.id.vUserDetails);
        mVUserStats = findViewById(R.id.vUserStats);

        setupToolbar();
        setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
    }

    private void setupTabs() {
        mTlUserProfileTabs = (TabLayout) findViewById(R.id.tlUserProfileTabs);

        mTlUserProfileTabs.addTab(mTlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        mTlUserProfileTabs.addTab(mTlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        mTlUserProfileTabs.addTab(mTlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        mTlUserProfileTabs.addTab(mTlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        mRevealBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
        mRevealBackground.setOnStateChangeListener(this);

        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            mRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    mRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            mUserPhotosAdapter.setDisableAnimations(true);
            mRevealBackground.setToFinishedFrame();
        }
    }

    private void setupUserProfileGrid() {
        mRvUserProfile = (RecyclerView) findViewById(R.id.rvUserProfile);
        final StaggeredGridLayoutManager layoutMgr =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRvUserProfile.setLayoutManager(layoutMgr);
        mRvUserProfile.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mUserPhotosAdapter.setDisableAnimations(true);
            }
        });
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            // Finished reveal animation
            mRvUserProfile.setVisibility(View.VISIBLE);
            mVUserProfileRoot.setVisibility(View.VISIBLE);
            mTlUserProfileTabs.setVisibility(View.VISIBLE);

            mUserPhotosAdapter = new UserProfileAdapter(this);
            mRvUserProfile.setAdapter(mUserPhotosAdapter);

            animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            // Reveal animation has started
            mRvUserProfile.setVisibility(View.INVISIBLE);
            mVUserProfileRoot.setVisibility(View.INVISIBLE);
            mTlUserProfileTabs.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        mTlUserProfileTabs.setTranslationY(-mTlUserProfileTabs.getTranslationY());
        ViewCompat.animate(mTlUserProfileTabs)
                .translationY(0F)
                .setDuration(300)
                .setStartDelay(USER_OPTIONS_ANIMATION_DELAY)
                .setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
        mVUserProfileRoot.setTranslationY(-mVUserProfileRoot.getHeight());
        mIvUserProfilePhoto.setTranslationY(-mIvUserProfilePhoto.getHeight());
        mVUserDetails.setTranslationY(-mVUserDetails.getHeight());
        mVUserStats.setAlpha(0F);

        ViewCompat.animate(mVUserProfileRoot).translationY(0F).setDuration(300).setInterpolator(INTERPOLATOR);
        ViewCompat.animate(mIvUserProfilePhoto).translationY(0F).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        ViewCompat.animate(mVUserDetails).translationY(0F).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
        ViewCompat.animate(mVUserStats).alpha(1F).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR);
    }
}
