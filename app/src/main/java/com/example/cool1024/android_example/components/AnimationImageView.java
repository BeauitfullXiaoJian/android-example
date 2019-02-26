package com.example.cool1024.android_example.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * 默认动画演示
 */
public class AnimationImageView extends androidx.appcompat.widget.AppCompatImageView {

    private GestureDetector mGestureDetector;

    public AnimationImageView(Context context) {
        this(context, null);
    }

    public AnimationImageView(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public AnimationImageView(Context context, AttributeSet attributes, int defStyleAttr) {
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

    private void backCenter() {
        Matrix matrix = getImageMatrix();
        PointF saveValue = getMatrixPoint(matrix);
        PointF centerPoint = getImageCenterPoint(matrix);

        // X轴方向移动
        ValueAnimator animatorX = ValueAnimator.ofFloat(saveValue.x, centerPoint.x);
        animatorX.setDuration(500);
        animatorX.addUpdateListener((animation) -> {
            float value = (float) animation.getAnimatedValue();
            matrix.postTranslate(value - saveValue.x, 0);
            saveValue.x = value;
        });
        animatorX.start();

        // Y轴方向移动
        ValueAnimator animatorY = ValueAnimator.ofFloat(saveValue.y, centerPoint.y);
        animatorY.setDuration(500);
        animatorY.addUpdateListener((animation) -> {
            float value = (float) animation.getAnimatedValue();
            matrix.postTranslate(0, value - saveValue.y);
            saveValue.y = value;
            invalidate();
        });
        animatorY.start();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Matrix matrix = getImageMatrix();
            matrix.postTranslate(-distanceX, -distanceY);
            invalidate();
            return true;
        }
    }
}
