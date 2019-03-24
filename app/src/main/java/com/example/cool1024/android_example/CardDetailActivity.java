package com.example.cool1024.android_example;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.cool1024.android_example.fragments.HomeFragment;

public class CardDetailActivity extends AppCompatActivity {

    public static final String TAG = "CardDetailActivity";

    private HomeFragment.CardData cardData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        loadIntentData();
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(cardData.getCardTitle());
            // actionBar.setSubtitle(cardData.getCardBody());
        }

        WebView webView = findViewById(R.id.web_view);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadData("<style>img{width:100%;}*{word-break:break-all;}</style>" +
                        "<h2>Sample</h2>" +
                        "<p>This is an instance of the <a href=\"https://ckeditor.com/docs/ckeditor5/latest/builds/guides/overview.html#classic-editor\">classic editor build</a>.</p>" +
                        "<img src=\"" + cardData.getCardImageUrl() + "\" />" +
                        "<p>" + cardData.getCardContent() + "</p>"
                , "text/html", "UTF-8");

//        Target target = new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                mActionBar.setLogo(resource);
//            }
//        };
//        ImageView avatarImageView = findViewById(R.id.avatar_view);
//        GlideApp.with(this)
//                .load(cardData.getCardAvatarUrl())
//                .apply(new RequestOptions().circleCrop())
//                .into(avatarImageView);
//        GlideApp.with(getBaseContext())
//                .load(cardData.getCardAvatarUrl())
//                .apply(new RequestOptions().circleCrop())
//                .into(mAvatarView);
//        GlideApp.with(getBaseContext())
//                .load(cardData.getCardImageUrl())
//                .into(mCardImageView);
//        mAuthorNickView.setText(cardData.getCardTitle());
//        mDetailContent.setText(cardData.getCardContent());
//        mLabelView.setText(cardData.getCardBody());
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
