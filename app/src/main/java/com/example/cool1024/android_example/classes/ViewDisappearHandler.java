package com.example.cool1024.android_example.classes;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class ViewDisappearHandler extends Handler implements Animation.AnimationListener {

    private int lastHandlerWhat = 0;
    private View targetView;
    private AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);

    public ViewDisappearHandler(View view) {
        super();
        alphaAnimation.setDuration(1000);
        alphaAnimation.setAnimationListener(ViewDisappearHandler.this);
        targetView = view;
    }

    public void showView() {
        targetView.setVisibility(View.VISIBLE);
    }

    public void hideView(int time) {
        sendEmptyMessageDelayed(++lastHandlerWhat, time);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == lastHandlerWhat && targetView != null) {
            targetView.startAnimation(alphaAnimation);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        targetView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
