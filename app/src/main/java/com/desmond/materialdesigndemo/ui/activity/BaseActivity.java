package com.desmond.materialdesigndemo.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.desmond.materialdesigndemo.R;

public class BaseActivity extends AppCompatActivity {

    ImageView mIvLogo;
    MenuItem mInboxMenuItem;
    private Toolbar toolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mInboxMenuItem = menu.findItem(R.id.action_inbox);
        mInboxMenuItem.setActionView(R.layout.menu_item_view);
        return true;
    }

    protected void setupToolbar() {
        // Fetch the data remotely
        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
        setSupportActionBar(toolbar);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //pressHomeButton();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
