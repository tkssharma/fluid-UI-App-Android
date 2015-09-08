package com.desmond.materialdesigndemo.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.Handler.UserImageHandler;
import com.desmond.materialdesigndemo.ui.fbUser;
import com.desmond.materialdesigndemo.ui.utils.CircleTransformation;
import com.desmond.materialdesigndemo.ui.view.RevealBackgroundView;
import com.squareup.picasso.Picasso;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
                .load( fbUser.imageUri)
                .into(mIvUserProfilePhoto);

        FirstName = (TextView) findViewById(R.id.FirstName);
        LastName = (TextView) findViewById(R.id.LastName);
        FirstName.setText(fbUser.name);
        LastName.setText( fbUser.name);


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

            mRevealBackground.setToFinishedFrame();
        }
    }

    private void setupUserProfileGrid() {
        mRvUserProfile = (RecyclerView) findViewById(R.id.rvUserProfile);
        final StaggeredGridLayoutManager layoutMgr =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRvUserProfile.setLayoutManager(layoutMgr);

    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            // Finished reveal animation
            mRvUserProfile.setVisibility(View.VISIBLE);
            mVUserProfileRoot.setVisibility(View.VISIBLE);
            mTlUserProfileTabs.setVisibility(View.VISIBLE);


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

        ViewCompat.animate(mVUserProfileRoot).translationY(0F).setDuration(300).setInterpolator(INTERPOLATOR);
        ViewCompat.animate(mIvUserProfilePhoto).translationY(0F).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        ViewCompat.animate(mVUserDetails).translationY(0F).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
        ViewCompat.animate(mVUserStats).alpha(1F).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR);
    }


}
