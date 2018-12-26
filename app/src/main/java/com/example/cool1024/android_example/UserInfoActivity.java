package com.example.cool1024.android_example;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class UserInfoActivity extends AppCompatActivity {

    public static final int REQUEST_STORAGE_READ = 1;
    public static final int IMAGE_PICK_CODE = 1;
    public static final String TAG = "UserInfoActivity";

    private View mAvatarItem;
    private ImageView mImageAvatar;
    private View mQRCodeItem;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
        initViewEvent();
        initSharedPreferences();
        loadSharedPreferences();
    }

    private void initSharedPreferences() {
        Log.d(TAG, "初始化共享数据文件");
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.share_file_path), Context.MODE_PRIVATE);
    }

    private void loadSharedPreferences() {
        String avatarPath = mSharedPreferences.getString(getResources().getString(R.string.avatar_key), "NONE");
        if (!avatarPath.equals("NONE")) {
            try {
                Uri uri = Uri.parse(avatarPath);
                Glide.with(UserInfoActivity.this)
                        .load(uri)
                        .into(mImageAvatar);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "本地头像数据载入失败：" + avatarPath);
            }
        }
    }

    /**
     * 初始化&找到视图组件
     */
    private void initView() {
        Toolbar toolBar = findViewById(R.id.toolbar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mAvatarItem = findViewById(R.id.item_avatar);
        mImageAvatar = mAvatarItem.findViewById(R.id.image_avatar);
        mQRCodeItem = findViewById(R.id.code_item);
    }


    /**
     * 初始化视图事件
     */
    private void initViewEvent() {
        mAvatarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(UserInfoActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_READ);
            }
        });
        mQRCodeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewQRCode();
            }
        });
    }

    private void viewPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void viewQRCode() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                if (uri != null) {
                    Glide.with(UserInfoActivity.this)
                            .load(uri)
                            .into(mImageAvatar);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();
                    edit.putString(getResources().getString(R.string.avatar_key), uri.toString());
                    edit.apply();
                } else {
                    Log.e(TAG, "相册通信异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "相册数据解析失败");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_READ && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "成功授权，可以读取本地文件");
            viewPhotos();
        } else {
            Log.d(TAG, "拒绝授权，无法读取本地文件");
            Toast.makeText(UserInfoActivity.this, "拒绝授权,无法修改头像", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
