package com.example.cool1024.android_example.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;

public class AutoFitCameraTextureView extends TextureView {

    private static final String TAG = "AutoFitCameraTextureLog";

    private Size mCameraSize = new Size(0, 0);

    public AutoFitCameraTextureView(Context context) {
        super(context, null);
    }

    public AutoFitCameraTextureView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public AutoFitCameraTextureView(Context context, AttributeSet attributes, int defStyleAttr) {
        super(context, attributes, defStyleAttr);
    }

    public void setCameraSize(Size cameraSize) {
        if (mCameraSize.getWidth() < 0 || mCameraSize.getHeight() < 0) {
            throw new IllegalArgumentException("相机的尺寸不能小于0");
        }
        mCameraSize = cameraSize;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取之前计算好的视图尺寸
        int width = MeasureSpec.getSize(getMeasuredWidth());
        int height = MeasureSpec.getSize(getMeasuredHeight());
        // 互换宽高
        int cWidth = mCameraSize.getWidth();
        int cHeight = mCameraSize.getHeight();
        if (mCameraSize.getWidth() > 0 && mCameraSize.getHeight() > 0) {
            if (width < cWidth * height / cHeight) {
                setMeasuredDimension(width, cHeight * width / cWidth);
            } else {
                setMeasuredDimension(cWidth * height / cHeight, height);
            }
        } else {
            setMeasuredDimension(width, height);
        }
        Log.d(TAG, "视图尺寸重新计算" + mCameraSize.toString());
    }
}
