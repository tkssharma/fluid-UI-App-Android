package com.desmond.materialdesigndemo.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Created by desmond on 31/7/15.
 */
public class CircleTransformation implements Transformation {

    private static final int STROKE_WIDTH = 6;

    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();

        int size = Math.min(width, height);
        int x = (width - size) / 2;
        int y = (height - size) / 2;

        Bitmap squareBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squareBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);

        Paint avatarPaint = new Paint();
        BitmapShader shader = new BitmapShader(squareBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        avatarPaint.setShader(shader);

        Paint outlinePaint = new Paint();
        outlinePaint.setColor(Color.WHITE);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(STROKE_WIDTH);
        outlinePaint.setAntiAlias(true);

        float radius = size / 2F;
        canvas.drawCircle(radius, radius, radius, avatarPaint);
        canvas.drawCircle(radius, radius, radius - STROKE_WIDTH / 2, outlinePaint);

        squareBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circleTransformation";
    }
}
