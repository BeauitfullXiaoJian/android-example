package com.example.cool1024.android_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    public static final  String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    /**
     * Find all ui components from view
     */
    private void initView(){
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.setting_notify_item).setTag(R.id.setting_notify_switch);
    }

    public void switchClick(View target){
        Integer targetId = target.getId();
        Switch targetSwitch = null;
        switch (targetId){
            case R.id.setting_notify_switch:
                targetSwitch = (Switch)target;
                String prefixStr = ((Switch) target).isChecked()?"开启":"关闭";
                String toastText = prefixStr + target.getTag().toString();
                Toast.makeText(SettingsActivity.this,toastText,Toast.LENGTH_SHORT).show();
                break;
            default:{
                targetSwitch = findViewById(Integer.valueOf(target.getTag().toString()));
                targetSwitch.toggle();
                targetSwitch.callOnClick();
            }
        }
    }
}
