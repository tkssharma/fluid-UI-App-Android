package com.desmond.materialdesigndemo.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.desmond.materialdesigndemo.R;

/**
 * Created by desmond on 2/8/15.
 */
public class SendingProgressView extends View {

    private static final String TAG = SendingProgressView.class.getSimpleName();

    public static final int STATE_NOT_STARTED = 0;
    public static final int STATE_PROGRESS_STARTED = 1;
    public static final int STATE_DONE_STARTED = 2;
    public static final int STATE_FINISHED = 3;

    private static final int PROGRESS_STROKE_SIZE = 10;
    private static final int INNER_CIRCLE_PADDING = 30;
    private static final int MAX_DONE_BG_OFFSET = 800;
    private static final int MAX_DONE_IMG_OFFSET = 400;

    private int state = STATE_NOT_STARTED;
    private float currentProgress = 0;
    private float currentDoneBgOffset = MAX_DONE_BG_OFFSET;
    private float currentCheckmarkOffset = MAX_DONE_IMG_OFFSET;

    private Paint progressPaint;
    private Paint doneBgPaint;
    private Paint maskPaint;

    private RectF progressBounds;

    private Bitmap checkmarkBitmap;
    private Bitmap innerCircleMaskBitmap;

    private int checkmarkXPosition = 0;
    private int checkmarkYPosition = 0;

    private Paint checkmarkPaint;
    private Bitmap tempBitmap;
    private Canvas tempCanvas;

    private ObjectAnimator simulateProgressAnimator;
    private ObjectAnimator doneBgAnimator;
    private ObjectAnimator checkmarkAnimator;

    private OnLoadingFinishedListener onLoadingFinishedListener;

    public interface OnLoadingFinishedListener {
        void onLoadingFinished();
    }

    public SendingProgressView(Context context) {
        super(context);
        init();
    }

    public SendingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SendingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SendingProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setupProgressPaint();
        setupDonePaints();
        setupSimulateProgressAnimator();
        setupDoneAnimators();
    }

    private void setupProgressPaint() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(0xffffffff);
        progressPaint.setStrokeWidth(PROGRESS_STROKE_SIZE);
    }

    private void setupDonePaints() {
        doneBgPaint = new Paint();
        doneBgPaint.setAntiAlias(true);
        doneBgPaint.setStyle(Paint.Style.FILL);
        doneBgPaint.setColor(0xff39cb72);

        checkmarkPaint = new Paint();

        maskPaint = new Paint();
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    /**
     * This is to fake a progress in this demo
     */
    private void setupSimulateProgressAnimator() {
        simulateProgressAnimator = ObjectAnimator.ofFloat(this, "currentProgress", 0, 100).setDuration(2000);
        simulateProgressAnimator.setInterpolator(new AccelerateInterpolator());
        simulateProgressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_DONE_STARTED);
            }
        });
    }

    private void setupDoneAnimators() {
        doneBgAnimator = ObjectAnimator.ofFloat(this, "currentDoneBgOffset", MAX_DONE_BG_OFFSET, 0).setDuration(300);
        doneBgAnimator.setInterpolator(new DecelerateInterpolator());

        checkmarkAnimator = ObjectAnimator.ofFloat(this, "currentCheckmarkOffset", MAX_DONE_IMG_OFFSET, 0).setDuration(300);
        checkmarkAnimator.setInterpolator(new OvershootInterpolator());
        checkmarkAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_FINISHED);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateProgressBounds();
        setupCheckmarkBitmap();
        setupDoneMaskBitmap();
        resetTempCanvas();
    }

    private void updateProgressBounds() {
        // bound that do not exceed the drawn circular progress bar
        progressBounds = new RectF(
                PROGRESS_STROKE_SIZE,
                PROGRESS_STROKE_SIZE,
                getWidth() - PROGRESS_STROKE_SIZE,
                getWidth() - PROGRESS_STROKE_SIZE
        );
    }

    private void setupCheckmarkBitmap() {
        checkmarkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp);
        checkmarkXPosition = getWidth() / 2 - checkmarkBitmap.getWidth() / 2;
        checkmarkYPosition = getWidth() / 2 - checkmarkBitmap.getHeight() / 2;
    }

    private void setupDoneMaskBitmap() {
        innerCircleMaskBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
        Canvas srcCanvas = new Canvas(innerCircleMaskBitmap);
        srcCanvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - INNER_CIRCLE_PADDING, new Paint());
    }

    private void resetTempCanvas() {
        tempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);

        // To use PorterDuff & blending modes, we are using the alpha channel. So we need
        // a bitmap that is filled by transparency. Using the canvas given in onDraw()
        // will not work because the bitmap in that canvas is already filled by the window background
        tempCanvas = new Canvas(tempBitmap);
    }

    private void changeState(int state) {
        if (this.state == state) return;

        tempBitmap.recycle();
        resetTempCanvas();

        this.state = state;

        switch (state) {
            case STATE_PROGRESS_STARTED: {
                setCurrentProgress(0);
                simulateProgressAnimator.start();
                break;
            }
            case STATE_DONE_STARTED: {
                setCurrentDoneBgOffset(MAX_DONE_BG_OFFSET);
                setCurrentCheckmarkOffset(MAX_DONE_IMG_OFFSET);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(doneBgAnimator, checkmarkAnimator);
                animatorSet.start();
                break;
            }
            case STATE_FINISHED: {
                if (onLoadingFinishedListener != null) {
                    onLoadingFinishedListener.onLoadingFinished();
                }
                break;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (this.state) {
            case STATE_PROGRESS_STARTED: {
                drawArcForCurrentProgress();
                break;
            }
            case STATE_DONE_STARTED: {
                drawFrameForDoneAnimation();
                postInvalidate();
                break;
            }
            case STATE_FINISHED: {
                drawFinishedState();
                break;
            }
        }

        canvas.drawBitmap(tempBitmap, 0, 0, null);
    }

    private void drawArcForCurrentProgress() {
        tempCanvas.drawArc(progressBounds, -90F, 360F * currentProgress / 100, false, progressPaint);
    }

    private void drawFrameForDoneAnimation() {

        // Draw the green part
        tempCanvas.drawCircle(
                getWidth() / 2, getWidth() / 2 + currentDoneBgOffset,
                getWidth() / 2 - INNER_CIRCLE_PADDING, doneBgPaint);

        // Draw the done tick
        tempCanvas.drawBitmap(
                checkmarkBitmap, checkmarkXPosition,
                checkmarkYPosition + currentCheckmarkOffset, checkmarkPaint);

        // Draw the masked bitmap on the tempCanvas
        tempCanvas.drawBitmap(innerCircleMaskBitmap, 0, 0, maskPaint);

        // Draw the progress arc
        // Draw this after the masking, otherwise the arc will not be shown
        // as it is outside of the innerCicleMaskBitmap
        tempCanvas.drawArc(progressBounds, 0, 360F, false, progressPaint);
    }

    private void drawFinishedState() {
        tempCanvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - INNER_CIRCLE_PADDING, doneBgPaint);
        tempCanvas.drawBitmap(checkmarkBitmap, checkmarkXPosition, checkmarkYPosition, checkmarkPaint);
        tempCanvas.drawArc(progressBounds, 0, 360F, false, progressPaint);
    }

    public void simulateProgress() {
        changeState(STATE_PROGRESS_STARTED);
    }

    public void setCurrentProgress(float currentProgress) {
        this.currentProgress = currentProgress;
        postInvalidate();
    }

    public void setCurrentDoneBgOffset(float currentDoneBgOffset) {
        this.currentDoneBgOffset = currentDoneBgOffset;
        postInvalidate();
    }

    public void setCurrentCheckmarkOffset(float currentCheckmarkOffset) {
        this.currentCheckmarkOffset = currentCheckmarkOffset;
        postInvalidate();
    }

    public void setOnLoadingFinishedListener(OnLoadingFinishedListener onLoadingFinishedListener) {
        this.onLoadingFinishedListener = onLoadingFinishedListener;
    }
}
