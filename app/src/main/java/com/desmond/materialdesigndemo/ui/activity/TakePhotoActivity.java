package com.desmond.materialdesigndemo.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.adapter.PhotoFiltersAdapter;
import com.desmond.materialdesigndemo.ui.view.RevealBackgroundView;

import java.io.File;

public class TakePhotoActivity extends BaseActivity
        implements RevealBackgroundView.OnStateChangeListener, CameraHostProvider {

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final int STATE_TAKE_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;

    RevealBackgroundView mRevealBackground;

    ViewSwitcher mVUpperPanel;
    ViewSwitcher mVLowerPanel;
    View mVPhotoRoot;

    ImageButton mIvTakenPhoto;

    CameraView mCameraView;

    private boolean mPendingIntro;
    private int mCurrentState;

    private File mPhotoPath;

    public static void startCameraFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, TakePhotoActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        mVPhotoRoot = findViewById(R.id.vPhotoRoot);
        mCameraView = (CameraView) findViewById(R.id.cameraView);
        mIvTakenPhoto = (ImageButton) findViewById(R.id.ivTakenPhoto);

        updateStatusBarColor();
        setupRevealBackground(savedInstanceState);
        setupPhotoFilters();

        mVUpperPanel = (ViewSwitcher) findViewById(R.id.vUpperPanel);
        mVLowerPanel = (ViewSwitcher) findViewById(R.id.vLowerPanel);
        updateState(STATE_TAKE_PHOTO);
        mVUpperPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mVUpperPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                mPendingIntro = true;
                mVUpperPanel.setTranslationY(-mVUpperPanel.getHeight());
                mVLowerPanel.setTranslationY(mVLowerPanel.getHeight());
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    private void updateState(int state) {
        mCurrentState = state;

        if (STATE_TAKE_PHOTO == mCurrentState) {
            mVUpperPanel.setInAnimation(this, R.anim.slide_in_from_right);
            mVLowerPanel.setInAnimation(this, R.anim.slide_in_from_right);

            mVUpperPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            mVLowerPanel.setOutAnimation(this, R.anim.slide_out_to_left);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIvTakenPhoto.setVisibility(View.GONE);
                }
            }, 400);

        } else if (STATE_SETUP_PHOTO == mCurrentState) {
            mVUpperPanel.setInAnimation(this, R.anim.slide_in_from_left);
            mVLowerPanel.setInAnimation(this, R.anim.slide_in_from_left);

            mVUpperPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            mVLowerPanel.setOutAnimation(this, R.anim.slide_out_to_right);

            mIvTakenPhoto.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0xff111111);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            mVPhotoRoot.setVisibility(View.VISIBLE);
            if (mPendingIntro) {
                startIntroAnimation();
            }
        } else {
            mVPhotoRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        mRevealBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
        mRevealBackground.setFillPaintColor(0xFF16181a);
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

    private void startIntroAnimation() {
        ViewCompat.animate(mVUpperPanel).translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
        ViewCompat.animate(mVLowerPanel).translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
    }

    private void setupPhotoFilters() {
        RecyclerView rvFilters = (RecyclerView) findViewById(R.id.rvFilters);
        rvFilters.setHasFixedSize(true);
        rvFilters.setAdapter(new PhotoFiltersAdapter());
        rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    public void onTakePhotoClick(View view) {
        view.setEnabled(false);
        mCameraView.takePicture(true, true);
        animateShutter();
    }

    public void onAcceptClick(View view) {
        PublishActivity.openWithPhotoUri(this, Uri.fromFile(mPhotoPath));
        overridePendingTransition(0, 0);
    }

    public void showTakenPhoto(Bitmap bitmap) {
        mVUpperPanel.showNext();
        mVLowerPanel.showNext();

        mIvTakenPhoto.setImageBitmap(bitmap);

        updateState(STATE_SETUP_PHOTO);
    }

    private void animateShutter() {
        final View vShutter = findViewById(R.id.vShutter);
        vShutter.setVisibility(View.VISIBLE);
        vShutter.setAlpha(0F);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0F, 0.8F);
        alphaInAnim.setDuration(100);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0.8f, 0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vShutter.setVisibility(View.GONE);
            }
        });

        animatorSet.start();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentState == STATE_SETUP_PHOTO) {
            findViewById(R.id.btnTakePhoto).setEnabled(true);
            mVUpperPanel.showNext();
            mVLowerPanel.showNext();
            updateState(STATE_TAKE_PHOTO);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public CameraHost getCameraHost() {
        return new MyCameraHost(this);
    }

    public void setPhotoPath(File photoPath) {
        mPhotoPath = photoPath;
    }

    private static class MyCameraHost extends SimpleCameraHost {

        private Camera.Size mPreviewSize;
        private TakePhotoActivity mContext;

        public MyCameraHost(Context context) {
            super(context);
            mContext = (TakePhotoActivity) context;
        }

        @Override
        public boolean useFullBleedPreview() {
            return super.useFullBleedPreview();
        }

        @Override
        public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {
            return mPreviewSize;
        }

        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
            Camera.Parameters parameters1 = super.adjustPreviewParameters(parameters);
            mPreviewSize = parameters1.getPreviewSize();
            return parameters1;
        }

        @Override
        public void saveImage(PictureTransaction xact, final Bitmap bitmap) {
            super.saveImage(xact, bitmap);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.showTakenPhoto(bitmap);
                }
            });
        }

        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {
            super.saveImage(xact, image);
            mContext.setPhotoPath(getPhotoPath());
        }


    }
}
