package com.example.cool1024.android_example;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.cool1024.android_example.fragments.HomeFragment;

public class CardDetailActivity extends AppCompatActivity {

    public static final String TAG = "CardDetailActivity";

    private HomeFragment.CardData cardData;

    private ActionBar mActionBar;
    private ImageView mAvatarView;
    private ImageView mCardImageView;
    private TextView mAuthorNickView;
    private TextView mLabelView;
    private TextView mDetailContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        loadIntentData();
        findView();
        initView();
    }

    private void findView() {
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mAvatarView = findViewById(R.id.author_avatar);
        mAuthorNickView = findViewById(R.id.author_nick);
        mLabelView = findViewById(R.id.label_string);
        mDetailContent = findViewById(R.id.detail_content);
        mCardImageView = findViewById(R.id.card_image);
    }

    private void initView() {
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(cardData.getCardTitle());
        GlideApp.with(getBaseContext())
                .load(cardData.getCardAvatarUrl())
                .apply(new RequestOptions().circleCrop())
                .into(mAvatarView);
        GlideApp.with(getBaseContext())
                .load(cardData.getCardImageUrl())
                .into(mCardImageView);
        mAuthorNickView.setText(cardData.getCardTitle());
        mDetailContent.setText(cardData.getCardContent());
        mLabelView.setText(cardData.getCardBody());
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
            cardData = (HomeFragment.CardData) bundle.getSerializable(TAG);
        }
    }
}
