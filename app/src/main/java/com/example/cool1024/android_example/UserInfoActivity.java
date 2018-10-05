package com.example.cool1024.android_example;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class UserInfoActivity extends AppCompatActivity {

    public static final int REQUEST_STORAGE_READ = 1;
    public static final int IMAGE_PICK_CODE = 1;
    public static final String TAG = "UserInfoActivity";

    private View mAvatarItem;
    private ImageView mImageAvatar;
    private View mQRCodeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
        initViewEvent();
    }

    /**
     * Find all ui components from view
     */
    private void initView(){
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

    private void initViewEvent(){
        mAvatarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE_READ);
            }
        });
        mQRCodeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewQRCode();
            }
        });
    }

    private void viewPhotos(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void viewQRCode(){
//        LinearLayout parentView = findViewById(R.id.user_info_layout);
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View qrCodeView = inflater.inflate(R.layout.qrcode_popup, null, false);
//        PopupWindow popupWindow = new PopupWindow(qrCodeView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
//        popupWindow.showAtLocation(parentView, Gravity.CENTER,0,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK){
            try {
                Uri uri = data.getData();
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                mImageAvatar.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"相册数据解析失败");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_STORAGE_READ && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"成功授权，可以读取本地文件");
            viewPhotos();
        }else{
            Log.d(TAG,"拒绝授权，无法读取本地文件");
            Toast.makeText(UserInfoActivity.this,"拒绝授权,无法修改头像",Toast.LENGTH_SHORT)
                .show();
        }
    }
}
