package com.example.cool1024.android_example.fragments.FlvFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.classes.FlvComment;
import com.example.cool1024.android_example.classes.FlvDetail;
import com.example.cool1024.android_example.fragments.BaseFragment;
import com.example.cool1024.android_example.http.ApiData;
import com.example.cool1024.android_example.http.RequestAsyncTask;

import java.util.Arrays;

public class FlvCommentFragment extends BaseFragment implements
        TextView.OnEditorActionListener, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "FlvCommentFragmentLog";
    private static final String FLV_ID_PARAM = "FLV_ID_PARAM";
    private static final int FLV_COMMENT_REQUEST_CODE = 1;

    private FlvDetail mFlvDetail;
    private EditText mCommentEditText;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static FlvCommentFragment newInstance(int flvId) {
        FlvCommentFragment fragment = new FlvCommentFragment();
        Bundle args = new Bundle();
        args.putInt(FLV_ID_PARAM, flvId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFlvDetail = new FlvDetail();
            mFlvDetail.setId(getArguments().getInt(FLV_ID_PARAM));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flv_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCommentEditText = view.findViewById(R.id.input_comment);
        mCommentEditText.setOnEditorActionListener(FlvCommentFragment.this);
        view.findViewById(R.id.btn_send).setOnClickListener(FlvCommentFragment.this);
        mSwipeRefreshLayout = view.findViewById(R.id.flv_comment_swipe);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(FlvCommentFragment.this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            if (v.getText().length() > 4) {
                showToast("评论成功～");
                v.setText("");
                v.clearFocus();
            } else {
                showToast("评论必须要4个字以上哦～");
            }

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send: {
                // 点击发送按钮等价与输入完成(ACTION_SEND)
                mCommentEditText.onEditorAction(EditorInfo.IME_ACTION_SEND);
                break;
            }
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onResponse(ApiData apiData) {
        if (apiData.getRequestCode() == FLV_COMMENT_REQUEST_CODE) {
            mFlvDetail = apiData.getDataObject(FlvDetail.class);
        }
        updateView();
    }

    @Override
    public void onComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadData() {
        mSwipeRefreshLayout.setRefreshing(true);
        RequestAsyncTask.get("https://www.cool1024.com/comment.json?id=" + mFlvDetail.getId(),
                FlvCommentFragment.this).setRequestCode(FLV_COMMENT_REQUEST_CODE).execute();
    }

    private void updateView() {
        View mainView = getView();
        if (mainView != null) {
            // 填充评论列表
            TextView commentView = getView().findViewById(R.id.reply_user_nick);
            commentView.setText(getCommentText("很好的创意，不知道什么时候可以开放出来～", "🍭棒棒糖"));
            commentView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private SpannableString getCommentText(String comment, String nickName) {
        ClickableSpan clickSpan = new NickClickSpan();
        SpannableString spbString = new SpannableString(String.format("%s:%s", nickName, comment));
        spbString.setSpan(clickSpan, 0, nickName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spbString;
    }

    class NickClickSpan extends ClickableSpan {
        @Override
        public void onClick(@NonNull View widget) {
            Log.d(TAG, "点击了昵称");
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            int color = getResources().getColor(R.color.colorPrimary);
            ds.setColor(color);
            ds.setUnderlineText(Boolean.FALSE);
        }
    }
}
