package com.example.cool1024.android_example;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.example.cool1024.android_example.fragments.ImageDrawFragment;
import com.myclass.stream.CFileReader;
import com.myclass.stream.CFileWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotoViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhotoViewActivityLog";
    private static final int FILE_SELECT_CODE = 0;
    private static final int DRAW_IMAGE_SELECT_CODE = 1;
    private static final String dataFileName = "PICTURE.DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        showImageFileDialog();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.photo_view_layout, fragment)
                    .commitAllowingStateLoss ();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && data != null){
            switch (requestCode){
                case FILE_SELECT_CODE:{
                    try {
                        Uri uri = data.getData();
                        if (uri != null) {
                            ContentResolver contentResolver = getContentResolver();
                            InputStream stream = contentResolver.openInputStream(uri);
                            if (stream != null) {
                                String imageString = decodeImageFile(stream);
                                File saveFile = new File(this.getFilesDir(), dataFileName);
                                if (saveFile.exists() || saveFile.createNewFile()) {
                                    CFileWriter cFileWriter = new CFileWriter(saveFile.getAbsolutePath());
                                    cFileWriter.write(imageString.getBytes());
                                    cFileWriter.close();
                                }
                                stream.close();
                                new File(uri.getEncodedPath()).delete();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "文件编码失败");
                    }
                    break;
                }
                case DRAW_IMAGE_SELECT_CODE:{
                    Log.d(TAG,"显示绘图界面");
                    Uri uri = data.getData();
                    showFragment(ImageDrawFragment.newInstance(uri.toString()));
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 打开图片选择对话框
     */
    private void showImageFileDialog() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "请选择要绘制的图片"), DRAW_IMAGE_SELECT_CODE);
    }

    private void showFileDialog() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "请选择要编码的图片"), FILE_SELECT_CODE);
    }

    private String decodeImageFile(InputStream stream) throws IOException {
        int count = stream.available();
        byte[] imageByte = new byte[count];
        return stream.read(imageByte) > 0
                ? Base64.encodeToString(imageByte, Base64.DEFAULT)
                : "";
    }

    private void encodeSaveImageDataFile() throws IOException {
        File readFile = new File(this.getFilesDir(), dataFileName);
        File saveFile = new File(this.getExternalFilesDir("output"), "a.jpg");
        if (readFile.exists() && saveFile.createNewFile()) {
            CFileReader cFileReader = new CFileReader(readFile.getAbsolutePath());
            FileOutputStream stream = new FileOutputStream(saveFile.getAbsoluteFile());
            stream.write(Base64.decode(cFileReader.read(), Base64.DEFAULT));
            stream.close();
        }
    }
}
