package com.example.cool1024.android_example;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.myclass.stream.CFileReader;
import com.myclass.stream.CFileWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Result;

public class PhotoViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhotoViewActivityLog";
    private static final int FILE_SELECT_CODE = 0;
    private static final String dataFileName = "PICTURE.DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        findViewById(R.id.btn_file_open).setOnClickListener(PhotoViewActivity.this);
        findViewById(R.id.btn_file_read).setOnClickListener(PhotoViewActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_file_open: {
                this.showFileDialog();
                break;
            }
            case R.id.btn_file_read: {
                try {
                    this.encodeSaveImageDataFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
