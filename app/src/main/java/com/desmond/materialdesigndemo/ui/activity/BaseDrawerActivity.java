package com.desmond.materialdesigndemo.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.utils.CircleTransformation;
import com.squareup.picasso.Picasso;

/**
 * Created by desmond on 2/8/15.
 */
public class BaseDrawerActivity extends BaseActivity {

    DrawerLayout mDrawerLayout;
    NavigationView mNaviView;
    ActionBarDrawerToggle mDrawerToggle;

    ImageView mIvMenuUserProfilePhoto;

    private int mAvatarSize;
    private String mProfilePhotos;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_drawer);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mNaviView = (NavigationView) findViewById(R.id.vNavigation);
        mIvMenuUserProfilePhoto = (ImageView) mNaviView.findViewById(R.id.ivMenuUserProfilePhoto);

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);

        setupHeader();
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();

        setupNaviDrawer();
    }

    private void setupHeader() {
        mAvatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
        mProfilePhotos = getString(R.string.user_profile_photo);
        Picasso.with(this)
                .load(mProfilePhotos)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(mAvatarSize, mAvatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(mIvMenuUserProfilePhoto);


        mNaviView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mDrawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int[] startingLocation = new int[2];
                        v.getLocationOnScreen(startingLocation);
                        startingLocation[0] += v.getWidth() / 2;
                        UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                        overridePendingTransition(0, 0);
                    }
                }, 200);
            }
        });
    }

    private void setupNaviDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(
                BaseDrawerActivity.this, mDrawerLayout, R.string.hello_world, R.string.hello_world);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mNaviView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.menu_feed:
                        break;
                    case R.id.menu_direct:
                        break;
                    case R.id.menu_news:
                        break;
                    case R.id.menu_popular:
                        break;
                }
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
