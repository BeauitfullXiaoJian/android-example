package com.example.cool1024.android_example;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.cool1024.android_example.fragments.HomeFragment;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";

    public static final String EMPTY_TAG = "NONE";

    private HomeFragment.CardData cardData;

    private Toolbar mToolbar;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        loadIntentData();
        findView();
        initView();
    }

    private void findView() {
        mToolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        mActionBar =  getSupportActionBar();
    }

    private void initView() {
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle(cardData.getCardTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 载入传递的数据
     */
    private void loadIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String jsonString = bundle.getString(TAG, "NONE");
            if (!jsonString.equals("NONE")) {
                cardData = HomeFragment.CardData.formJsonString(jsonString);
            }
        }
    }
}
