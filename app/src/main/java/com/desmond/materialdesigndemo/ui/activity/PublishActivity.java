package com.desmond.materialdesigndemo.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.desmond.materialdesigndemo.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PublishActivity extends BaseActivity {

    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

    ToggleButton mTbFollowers;
    ToggleButton mTbDirect;
    ImageView mIvPhoto;

    private boolean mPropagatingToggleState = false;
    private Uri mPhotoUri;
    private int mPhotoSize;

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        setupToolbar();

        mPhotoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

        if (savedInstanceState == null) {
            mPhotoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
        } else {
            mPhotoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }

        updateStatusBarColor();
        setupToggleButtons();

        mIvPhoto = (ImageView) findViewById(R.id.ivPhoto);
        mIvPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mIvPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });
    }

    @Override
    protected void setupToolbar() {
        if (mToolBar == null) {
            mToolBar = (Toolbar) findViewById(R.id.toolbar);
        }

        setSupportActionBar(mToolBar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void loadThumbnailPhoto() {
        mIvPhoto.setScaleX(0);
        mIvPhoto.setScaleY(0);
        Picasso.with(this)
                .load(mPhotoUri)
                .centerCrop()
                .resize(mPhotoSize, mPhotoSize)
                .into(mIvPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ViewCompat.animate(mIvPhoto).scaleX(1.0F).scaleY(1.0F)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400).setStartDelay(200).start();
                    }

                    @Override
                    public void onError() {}
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            bringMainActivityToTop();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bringMainActivityToTop() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, mPhotoUri);
        super.onSaveInstanceState(outState);
    }

    private void setupToggleButtons() {
        mTbFollowers = (ToggleButton) findViewById(R.id.tbFollowers);
        mTbDirect = (ToggleButton) findViewById(R.id.tbDirect);

        mTbFollowers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!mPropagatingToggleState) {
                    mPropagatingToggleState = true;
                    mTbDirect.setChecked(!isChecked);
                    mPropagatingToggleState = false;
                }
            }
        });
        mTbDirect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!mPropagatingToggleState) {
                    mPropagatingToggleState = true;
                    mTbFollowers.setChecked(!isChecked);
                    mPropagatingToggleState = false;
                }
            }
        });
    }
}
