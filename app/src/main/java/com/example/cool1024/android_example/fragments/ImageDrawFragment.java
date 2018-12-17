package com.example.cool1024.android_example.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.cool1024.android_example.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ImageDrawFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ImageDrawFragmentLog";
    private static final String IMAGE_PATH = "IMAGE_PATH";

    private Context mContext;
    private RelativeLayout mMainLayout;
    private Uri mDrawImageUri;
    private DrawImageView mDrawImageView;

    public ImageDrawFragment() {
    }

    /**
     * 创建一个相册列表Fragment
     *
     * @param imagePath 编辑的图片文件路径
     * @return 新的相册列表Fragment
     */
    public static ImageDrawFragment newInstance(String imagePath) {
        ImageDrawFragment fragment = new ImageDrawFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_PATH, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContext = getContext();
            mDrawImageUri = Uri.parse(getArguments().getString(IMAGE_PATH));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "创建视图");
        View mainView = inflater.inflate(R.layout.fragment_image_draw, container, false);
        mMainLayout = mainView.findViewById(R.id.image_draw_layout);
        // 初始化其他视图组件
        initView();
        return mainView;
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
            Toast.makeText(mContext.getApplicationContext(), "图片保存成功", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Log.d(TAG, "拒绝授权，无法写入本地文件");
            Toast.makeText(mContext.getApplicationContext(), "图片保存失败，需要本地存储权限", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void initView() {

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
            InputStream stream = contentResolver.openInputStream(mDrawImageUri);
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
        mMainLayout.findViewById(R.id.btn_cancel).setOnClickListener(ImageDrawFragment.this);
        mMainLayout.findViewById(R.id.btn_save).setOnClickListener(ImageDrawFragment.this);
        mMainLayout.findViewById(R.id.btn_black).setOnClickListener(ImageDrawFragment.this);
        mMainLayout.findViewById(R.id.btn_red).setOnClickListener(ImageDrawFragment.this);
        mMainLayout.findViewById(R.id.btn_green).setOnClickListener(ImageDrawFragment.this);
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
        Drawable icon = mContext.getResources().getDrawable(R.drawable.ic_check_black_24dp);
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

    public class DrawLine {
        public ArrayList<Point> points;
        public Paint paint;

        DrawLine(ArrayList<Point> drawPoints, Paint drawPaint) {
            this.points = drawPoints;
            this.paint = new Paint(drawPaint);
        }
    }

    public class DrawImageView extends android.support.v7.widget.AppCompatImageView {

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
            drawLine = new DrawLine(new ArrayList<Point>(), drawPaint);
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
                    drawLine = new DrawLine(new ArrayList<Point>(), drawPaint);
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
