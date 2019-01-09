package com.example.cool1024.android_example.fragments.FlvFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.cool1024.android_example.fragments.BaseTabFragment;

public class FlvCommentFragment extends BaseTabFragment implements
        TextView.OnEditorActionListener, View.OnClickListener {

    public static final String TAG = "FlvCommentFragmentLog";
    private static final String FLV_ID_PARAM = "FLV_ID_PARAM";

    private EditText mCommentEditText;

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
            getArguments().getString(FLV_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_flv_comment, container, false);
        mCommentEditText = mainView.findViewById(R.id.input_comment);
        mCommentEditText.setOnEditorActionListener(FlvCommentFragment.this);
        mainView.findViewById(R.id.btn_send).setOnClickListener(FlvCommentFragment.this);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View mainView = getView();
        if (mainView == null) return;
        // 填充评论列表
        TextView commentView = mainView.findViewById(R.id.reply_user_nick);
        commentView.setText(getCommentText("很好的创意，不知道什么时候可以开放出来～", "🍭棒棒糖"));
        commentView.setMovementMethod(LinkMovementMethod.getInstance());
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
