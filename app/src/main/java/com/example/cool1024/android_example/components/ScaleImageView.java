package com.example.cool1024.android_example.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ScaleImageView extends androidx.appcompat.widget.AppCompatImageView {

    private ScaleGestureDetector mScaleGestureDetector;

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public ScaleImageView(Context context, AttributeSet attributes, int defStyleAttr) {
        super(context, attributes, defStyleAttr);
        initGesture();
    }

    private void initGesture() {
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();

        // 缩放处理
        mScaleGestureDetector.onTouchEvent(event);

        return true;
    }

    private PointF[] getImageCenterPointAndScale(Matrix matrix) {
        float[] mtrValues = new float[9];
        Drawable drawable = getDrawable();
        matrix.getValues(mtrValues);
        float scaleX = mtrValues[Matrix.MSCALE_X];
        float scaleY = mtrValues[Matrix.MSCALE_Y];
        float originW = drawable.getIntrinsicWidth();
        float originH = drawable.getIntrinsicHeight();
        int w = Math.round(originW * scaleX);
        int h = Math.round(originH * scaleY);
        return new PointF[]{
                new PointF((getWidth() - w) / 2.0f, (getHeight() - h) / 2.0f),
                new PointF(scaleX, scaleY)
        };
    }

    private void backCenter() {
        Matrix matrix = getImageMatrix();
        PointF[] points = getImageCenterPointAndScale(matrix);
        PointF centerPoint = points[0];
        PointF scaleXY = points[1];
        ValueAnimator animator = ValueAnimator.ofFloat(scaleXY.x, 1);
        animator.addUpdateListener((animation -> {

        }));
        animator.start();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Matrix matrix = getImageMatrix();
            matrix.postScale(detector.getScaleFactor(), detector.getScaleFactor(),
                    detector.getFocusX(), detector.getFocusY());
            setImageMatrix(matrix);
            invalidate();
            return true;
        }
    }
}
