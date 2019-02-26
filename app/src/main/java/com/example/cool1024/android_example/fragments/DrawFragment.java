package com.example.cool1024.android_example.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cool1024.android_example.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class DrawFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "ImageDrawFragmentLog";
    private static final int DRAW_IMAGE_SELECT_CODE = 1;
    private Context mContext;
    private RelativeLayout mMainLayout;
    private DrawImageView mDrawImageView;

    public static DrawFragment newInstance() {
        return new DrawFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_draw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mMainLayout = view.findViewById(R.id.image_draw_layout);
        String fileType = "image/*";
        String dialogTitle = "请选择要涂鸦的图片";
        openFileDialog(fileType, dialogTitle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save: {
                // 请求保存图片，需要获取写入权限
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                break;
            }
            case R.id.btn_cancel: {
                // 撤销之前的操作
                mDrawImageView.reDo();
                break;
            }
            case R.id.btn_black: {
                changePaintColor(Color.BLACK);
                break;
            }
            case R.id.btn_red: {
                changePaintColor(Color.RED);
                break;
            }
            case R.id.btn_green: {
                changePaintColor(Color.GREEN);
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "成功授权，可以写入本地文件");
            Bitmap bitmap = mDrawImageView.getImageBitmap();
            Log.d(TAG, "图片大小" + bitmap.getByteCount());
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    bitmap, "截图快照", "这个图片来源于手绘板");
            showToast("图片保存成功");
        } else {
            Log.d(TAG, "拒绝授权，无法写入本地文件");
            showToast("图片保存失败，需要本地存储权限");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DRAW_IMAGE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                prepareDrawPad(uri);
            }
        } else {
            showToast("你取消图片选择，请返回上一个界面进行选择");
        }
    }

    private void prepareDrawPad(Uri imageUri) {

        // 初始化绘画板
        mDrawImageView = new DrawImageView(this.getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mDrawImageView.setLayoutParams(layoutParams);
        mDrawImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mDrawImageView.setBackgroundColor(Color.BLACK);
        mMainLayout.addView(mDrawImageView, 0);

        // 设置画板图片
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            InputStream stream = contentResolver.openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            Point size = getAutoSize(bitmap);
            ViewGroup.LayoutParams params = mDrawImageView.getLayoutParams();
            params.width = size.x;
            params.height = size.y;
            mDrawImageView.setLayoutParams(params);
            mDrawImageView.setImageBitmap(bitmap);
            Log.d(TAG, "图片设置成功");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "打开的图片找不到");
            e.printStackTrace();
        }

        // 设置相关按钮事件
        mMainLayout.findViewById(R.id.btn_cancel).setOnClickListener(DrawFragment.this);
        mMainLayout.findViewById(R.id.btn_save).setOnClickListener(DrawFragment.this);
        mMainLayout.findViewById(R.id.btn_black).setOnClickListener(DrawFragment.this);
        mMainLayout.findViewById(R.id.btn_red).setOnClickListener(DrawFragment.this);
        mMainLayout.findViewById(R.id.btn_green).setOnClickListener(DrawFragment.this);
    }

    private void changePaintColor(int color) {
        ((FloatingActionButton) mMainLayout.findViewById(R.id.btn_black)).setImageDrawable(null);
        ((FloatingActionButton) mMainLayout.findViewById(R.id.btn_red)).setImageDrawable(null);
        ((FloatingActionButton) mMainLayout.findViewById(R.id.btn_green)).setImageDrawable(null);
        FloatingActionButton btn;
        if (color == Color.BLACK) {
            btn = mMainLayout.findViewById(R.id.btn_black);
        } else if (color == Color.RED) {
            btn = mMainLayout.findViewById(R.id.btn_red);
        } else {
            btn = mMainLayout.findViewById(R.id.btn_green);
        }
        Drawable icon = mContext.getResources().getDrawable(R.drawable.ic_check);
        btn.setImageDrawable(icon);
        mDrawImageView.setPaintColor(color);
    }

    private Point getAutoSize(Bitmap bitmap) {
        float w = bitmap.getWidth();
        float h = bitmap.getHeight();
        float statusH = 0;
        Point outSize = new Point();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int resourceId = mContext.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            statusH = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getSize(outSize);
            outSize.offset(0, -(int) statusH);
            // 强制设置宽度为屏幕宽度
            float k = w / outSize.x;
            w = outSize.x;
            h = h / k;
            // 如果高度超出，那么就缩小高度
            if (h > outSize.y) {
                k = h / outSize.y;
                h = outSize.y;
                w = w / k;
            }
            Log.d(TAG, "适配后的图片尺寸" + w + "," + h);
            Log.d(TAG, "窗口原始大小" + outSize.x + "," + outSize.y);
        }
        return new Point((int) w, (int) h);
    }

    /**
     * 打开图片选择对话框
     *
     * @param fileType    文件类型
     * @param dialogTitle 对话框标题
     */
    private void openFileDialog(String fileType, String dialogTitle) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(fileType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, dialogTitle), DRAW_IMAGE_SELECT_CODE);
    }

    public class DrawLine {
        ArrayList<Point> points;
        Paint paint;

        DrawLine(ArrayList<Point> drawPoints, Paint drawPaint) {
            this.points = drawPoints;
            this.paint = new Paint(drawPaint);
        }
    }

    public class DrawImageView extends androidx.appcompat.widget.AppCompatImageView {

        // 绘画相关
        private DrawLine drawLine;
        private ArrayList<DrawLine> drawLines = new ArrayList<>();
        private Paint drawPaint;

        public DrawImageView(Context context) {
            super(context);
            drawPaint = new Paint();
            drawPaint.setColor(Color.RED);
            drawPaint.setAntiAlias(true);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);
            drawPaint.setStrokeWidth(10);
            drawLine = new DrawLine(new ArrayList<>(), drawPaint);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            // 绘制线条
            for (DrawLine line : drawLines) {
                drawLine(canvas, line);
            }
            drawLine(canvas, drawLine);
            Log.d(TAG, "重绘成功");
        }

        @Override
        public boolean performClick() {
            return super.performClick();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            // 获取坐标
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    Log.d(TAG, "开始绘制线条");
                    performClick();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    Log.d(TAG, "正在绘制线条");
                    drawLine.points.add(new Point(x, y));
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    Log.d(TAG, "结束绘制线条");
                    drawLines.add(drawLine);
                    drawLine = new DrawLine(new ArrayList<>(), drawPaint);
                    break;
                }
            }
            // 重绘视图
            invalidate();

            return true;
        }

        private void drawLine(Canvas canvas, DrawLine line) {
            for (int i = 0; i < line.points.size() - 1; i++) {
                Point startPoint = line.points.get(i);
                Point endPoint = line.points.get(i + 1);
                canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, line.paint);
            }
        }

        public void setPaintColor(int color) {
            drawPaint.setColor(color);
            drawLine.paint.setColor(color);
        }

        public void reDo() {
            int lineNum = this.drawLines.size();
            if (lineNum > 0) {
                this.drawLines.remove(lineNum - 1);
                invalidate();
            }
        }

        public Bitmap getImageBitmap() {
            this.setDrawingCacheEnabled(true);
            return this.getDrawingCache();
        }
    }
}
