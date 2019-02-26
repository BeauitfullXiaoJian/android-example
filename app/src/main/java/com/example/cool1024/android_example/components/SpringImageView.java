package com.example.cool1024.android_example.components;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

/**
 * 弹性动画演示
 */
public class SpringImageView extends androidx.appcompat.widget.AppCompatImageView {

    private GestureDetector mGestureDetector;

    public SpringImageView(Context context) {
        this(context, null);
    }

    public SpringImageView(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public SpringImageView(Context context, AttributeSet attributes, int defStyleAttr) {
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

        // X轴方向回弹
        SpringAnimation animationX = new SpringAnimation(new FloatValueHolder());
        SpringForce springForceX = new SpringForce();
        springForceX.setFinalPosition(centerPoint.x);
        animationX.setSpring(springForceX);
        animationX.setStartValue(saveValue.x);
        animationX.addUpdateListener((dynamicAnimation, value, velocity) -> {
            matrix.postTranslate(value - saveValue.x, 0);
            saveValue.x = value;
        });
        animationX.start();

        // Y轴方向回弹
        SpringAnimation animationY = new SpringAnimation(new FloatValueHolder());
        SpringForce springForceY = new SpringForce();
        springForceY.setFinalPosition(centerPoint.y);
        animationY.setSpring(springForceY);
        animationY.setStartValue(saveValue.y);
        animationY.addUpdateListener((dynamicAnimation, value, velocity) -> {
            matrix.postTranslate(0, value - saveValue.y);
            saveValue.y = value;
            invalidate();
        });
        animationY.start();
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
