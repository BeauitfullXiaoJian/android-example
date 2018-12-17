package com.example.cool1024.android_example;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.cool1024.android_example.fragments.ImageDrawFragment;

public class FragmentDevActivity extends AppCompatActivity {

    public static final String TAG = "FragmentDevActivityLog";
    public static final String FRAGMENT_NAME_PARAM = "fragment_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_dev);
        // 载入选中的碎片
        setActiveFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case ImageDrawFragment.DRAW_IMAGE_SELECT_CODE: {
                    Log.d(TAG, "显示绘图界面");
                    Uri uri = data.getData();
                    if (uri != null) {
                        showFragment(ImageDrawFragment.newInstance(uri.toString()));
                    }
                    break;
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "你取消了数据选择，请返回上一个界面进行选择",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * 根据传递的参数设置某个Fragment为积极状态
     */
    private void setActiveFragment() {
        String activeFragmentName = getIntent().getStringExtra(FRAGMENT_NAME_PARAM);
        Log.d(TAG, String.format("打开的碎片为:%s", activeFragmentName));
        switch (activeFragmentName) {
            case ImageDrawFragment.TAG: {
                openFileDialog("image/*", "请选择要涂鸦的图片",
                        ImageDrawFragment.DRAW_IMAGE_SELECT_CODE);
                break;
            }
            default: {
                openFileDialog("", "", 0);
            }
        }
    }

    /**
     * 显示指定的视图碎片
     *
     * @param fragment 要显示的碎片
     */
    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.photo_view_layout, fragment)
                    .commitAllowingStateLoss();
        }
    }

    /**
     * 打开图片选择对话框
     *
     * @param fileType    文件类型
     * @param dialogTitle 对话框标题
     * @param requestCode 请求码
     */
    private void openFileDialog(String fileType, String dialogTitle, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(fileType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, dialogTitle), requestCode);
    }
}
