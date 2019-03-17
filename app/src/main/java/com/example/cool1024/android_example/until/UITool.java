package com.example.cool1024.android_example.until;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;

public class UITool {

    private static Size sDefaultWindowSize;

    public static Size getNowWindowSize(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point sizePoint = new Point();
        display.getRealSize(sizePoint);
        return new Size(sizePoint.x, sizePoint.y);
    }

    public static Size getDefaultWindowSize(Activity activity) {
        if (sDefaultWindowSize == null) {
            Size nowSize = getNowWindowSize(activity);
            sDefaultWindowSize = new Size(
                    Math.min(nowSize.getWidth(), nowSize.getHeight()),
                    Math.max(nowSize.getWidth(), nowSize.getHeight())
            );
        }
        return sDefaultWindowSize;
    }

}
