package com.example.cool1024.android_example;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    public static final  String TAG = "SettingsActivity";

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initSharedPreferences();
        initView();
        loadSharedPreferences();
    }

    private void initSharedPreferences(){
        Log.d(TAG,"初始化共享数据文件");
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.share_file_path),Context.MODE_PRIVATE);
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
        findViewById(R.id.setting_notify_item).setTag(R.id.setting_notify_switch);
        findViewById(R.id.setting_request_item).setTag(R.id.setting_request_switch);
        findViewById(R.id.setting_sound_item).setTag(R.id.setting_sound_switch);
        findViewById(R.id.setting_shake_item).setTag(R.id.setting_shake_switch);
    }

    private void loadSharedPreferences(){
        Switch targetSwitch = findViewById(R.id.setting_notify_switch);
        Boolean isChecked = mSharedPreferences.getBoolean(String.valueOf(R.id.setting_notify_switch), Boolean.TRUE);
        targetSwitch.setChecked(isChecked);
        targetSwitch = findViewById(R.id.setting_request_switch);
        isChecked = mSharedPreferences.getBoolean(String.valueOf(R.id.setting_request_switch), Boolean.TRUE);
        targetSwitch.setChecked(isChecked);
        targetSwitch = findViewById(R.id.setting_sound_switch);
        isChecked = mSharedPreferences.getBoolean(String.valueOf(R.id.setting_sound_switch), Boolean.TRUE);
        targetSwitch.setChecked(isChecked);
        targetSwitch = findViewById(R.id.setting_shake_switch);
        isChecked = mSharedPreferences.getBoolean(String.valueOf(R.id.setting_shake_switch), Boolean.TRUE);
        targetSwitch.setChecked(isChecked);
    }

    private void showSettingMessage(View target){
        Boolean isChecked = ((Switch) target).isChecked();
        String prefixStr = isChecked ? "开启" : "关闭";
        String toastText = prefixStr + target.getTag().toString();
        Toast.makeText(getApplicationContext(),toastText,Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor edit =  mSharedPreferences.edit();
        edit.putBoolean(String.valueOf(target.getId()),isChecked);
        edit.apply();
    }

    public void switchClick(View target){
        Integer targetId = target.getId();
        switch (targetId){
            case R.id.setting_notify_switch:
                showSettingMessage(target);
                break;
            case R.id.setting_request_switch:
                showSettingMessage(target);
                break;
            case R.id.setting_sound_switch:
                showSettingMessage(target);
                break;
            case R.id.setting_shake_switch:
                showSettingMessage(target);
                break;
            default:{
                Switch targetSwitch = findViewById(Integer.valueOf(target.getTag().toString()));
                targetSwitch.toggle();
                targetSwitch.callOnClick();
            }
        }
    }
}
