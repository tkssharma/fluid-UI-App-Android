package com.desmond.materialdesigndemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.fbUser;
import com.desmond.materialdesigndemo.ui.view.RevealBackgroundView;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends BaseActivity implements RevealBackgroundView.OnStateChangeListener {

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    RevealBackgroundView mRevealBackground;

    RecyclerView mRvUserProfile;
    TabLayout mTlUserProfileTabs;
    TextView vUserDetails;
    TextView FirstName;
    TextView LastName;
    ImageView mIvUserProfilePhoto;

    Button mBtnFollow;
    TextView Work;
    View mVUserDetails;
    View mVUserStats;
    View mVUserProfileRoot;

    private int mAvatarSize;
    private String mProfilePhoto;


    public static void startUserProfileFromLocation(int[] startingLocation, BaseActivity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAvatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        mProfilePhoto = getString(R.string.user_profile_photo);


        mIvUserProfilePhoto = (ImageView) findViewById(R.id.ivUserProfilePhoto);
        mVUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        mVUserDetails = findViewById(R.id.vUserDetails);


        Picasso.with(this)
                .load(fbUser.imageUri)
                .into(mIvUserProfilePhoto);

        FirstName = (TextView) findViewById(R.id.FirstName);
        LastName = (TextView) findViewById(R.id.LastName);
        Work = (TextView) findViewById(R.id.work);
        FirstName.setText(fbUser.name);
        Work.setText(fbUser.email);
        LastName.setText(fbUser.gender);

        setupToolbar();
        // setupTabs();

        setupRevealBackground(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

            mRevealBackground.setToFinishedFrame();
        }
    }


    @Override
    public void onStateChange(int state) {

    }
}
