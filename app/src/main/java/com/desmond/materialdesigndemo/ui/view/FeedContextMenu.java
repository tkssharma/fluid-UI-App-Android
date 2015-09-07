package com.desmond.materialdesigndemo.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.Utils;

/**
 * Created by desmond on 30/7/15.
 */
public class FeedContextMenu extends LinearLayout {

    private static final int CONTEXT_MENU_WIDTH = Utils.dpToPx(240);

    private int feedItem = -1;

    private OnFeedContextMenuItemClickListener onItemClickListener;

    public interface OnFeedContextMenuItemClickListener {
        void onReportClick(int feedItem);
        void onSharePhotoClick(int feedItem);
        void onCopyShareUrlClick(int feedItem);
        void onCancelClick(int feedItem);
    }

    public FeedContextMenu(Context context) {
        super(context);
        init();
    }

    public FeedContextMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FeedContextMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FeedContextMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);
        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.bg_container_shadow);
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Manually inflate a layout, this callback is not called.
     * Instantiating using 'new CustomView(context)' will naturally mean it has no children
     * & hence it does not have to wait for them to be instantiated in the recursive fashion, as in XML inflation.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);
//        setOrientation(VERTICAL);
//        setBackgroundResource(R.drawable.bg_container_shadow);
//        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        setClickListener();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setClickListener();
    }

    private void setClickListener() {
        findViewById(R.id.btnReport).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onReportClick();
            }
        });

        findViewById(R.id.btnSharePhoto).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSharePhotoClick();
            }
        });

        findViewById(R.id.btnCopyShareUrl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCopyShareUrlClick();
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick();
            }
        });
    }

    private void onReportClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onReportClick(feedItem);
        }
    }

    private void onSharePhotoClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onSharePhotoClick(feedItem);
        }
    }

    private void onCopyShareUrlClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onCopyShareUrlClick(feedItem);
        }
    }

    private void onCancelClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onCancelClick(feedItem);
        }
    }

    public void setOnFeedMenuItemClickListener(OnFeedContextMenuItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void bindToItem(int feedItem) {
        this.feedItem = feedItem;
    }

    public void dismiss() {
        ((ViewGroup) getParent()).removeView(FeedContextMenu.this);
    }
}
