package com.example.cool1024.android_example.components;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;

/**
 * 惯性动画演示
 */
public class FlingImageView extends androidx.appcompat.widget.AppCompatImageView {

    private GestureDetector mGestureDetector;
    private boolean mDragLock = Boolean.FALSE;

    public FlingImageView(Context context) {
        this(context, null);
    }

    public FlingImageView(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public FlingImageView(Context context, AttributeSet attributes, int defStyleAttr) {
        super(context, attributes, defStyleAttr);
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            backCenter();
        }
        return true;
    }

    private PointF getMatrixPoint(Matrix matrix) {
        float[] mtrValues = new float[9];
        matrix.getValues(mtrValues);
        float x = mtrValues[Matrix.MTRANS_X];
        float y = mtrValues[Matrix.MTRANS_Y];
        return new PointF(x, y);
    }

    private PointF getImageCenterPoint(Matrix matrix) {
        float[] mtrValues = new float[9];
        Drawable drawable = getDrawable();
        matrix.getValues(mtrValues);
        float scaleX = mtrValues[Matrix.MSCALE_X];
        float scaleY = mtrValues[Matrix.MSCALE_Y];
        float originW = drawable.getIntrinsicWidth();
        float originH = drawable.getIntrinsicHeight();
        int w = Math.round(originW * scaleX);
        int h = Math.round(originH * scaleY);
        return new PointF((getWidth() - w) / 2.0f, (getHeight() - h) / 2.0f);
    }

    private boolean isDragAllow() {
        return !mDragLock;
    }

    private void lockDrag() {
        mDragLock = true;
    }

    private void unlockDrag() {
        mDragLock = false;
    }

    private void backCenter() {
        lockDrag();
        Matrix matrix = getImageMatrix();
        PointF saveValue = getMatrixPoint(matrix);
        PointF centerPoint = getImageCenterPoint(matrix);
        float minX = Math.min(saveValue.x, centerPoint.x);
        float maxX = Math.max(saveValue.x, centerPoint.x);
        float minY = Math.min(saveValue.y, centerPoint.y);
        float maxY = Math.max(saveValue.y, centerPoint.y);
        float velocityX = (centerPoint.x - saveValue.x) * 5;
        float velocityY = (centerPoint.y - saveValue.y) * 5;
        FlingAnimation animationX = new FlingAnimation(new FloatValueHolder());
        FlingAnimation animationY = new FlingAnimation(new FloatValueHolder());

        // X轴方向滑动
        animationX.addUpdateListener((DynamicAnimation animation, float value, float v) -> {
            matrix.postTranslate(value - saveValue.x, 0);
            saveValue.x = value;
        });
        animationX.addEndListener((animation, canceled, value, velocity) -> {
            if (!animationY.isRunning()) {
                unlockDrag();
            }
        });
        animationX.setStartVelocity(velocityX)
                .setStartValue(saveValue.x)
                .setMinValue(minX)
                .setMaxValue(maxX)
                .start();

        // Y轴方向滑动
        animationY.addUpdateListener((DynamicAnimation animation, float value, float v) -> {
            matrix.postTranslate(0, value - saveValue.y);
            saveValue.y = value;
            invalidate();
        });
        animationY.addEndListener((animation, canceled, value, velocity) -> {
            if (!animationX.isRunning()) {
                unlockDrag();
            }
        });
        animationY.setStartVelocity(velocityY)
                .setStartValue(saveValue.y)
                .setMinValue(minY)
                .setMaxValue(maxY)
                .start();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (isDragAllow()) {
                Matrix matrix = getImageMatrix();
                matrix.postTranslate(-distanceX, -distanceY);
                invalidate();
            }
            return true;
        }
    }
}
