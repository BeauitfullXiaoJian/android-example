package com.example.cool1024.android_example.fragments.FlvFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.classes.FlvDetail;
import com.example.cool1024.android_example.fragments.BaseTabFragment;
import com.example.cool1024.android_example.http.ApiData;
import com.example.cool1024.android_example.http.RequestAsyncTask;

import java.util.Locale;


public class FlvDetailFragment extends BaseTabFragment implements
        SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "BaseTabFragmentLog";
    private static final String FLV_DETAIL_PARAM = "FLV_DETAIL_PARAM";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FlvDetail mFlvDetail;

    public static FlvDetailFragment newInstance(int flvId) {
        FlvDetailFragment fragment = new FlvDetailFragment();
        Bundle args = new Bundle();
        args.putInt(FLV_DETAIL_PARAM, flvId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFlvDetail = new FlvDetail();
            mFlvDetail.setId(getArguments().getInt(FLV_DETAIL_PARAM));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_flv_detail, container, false);
        mSwipeRefreshLayout = mainView.findViewById(R.id.flv_detail_swipe);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(FlvDetailFragment.this);
        RecyclerView recyclerView = mainView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusableInTouchMode(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerAdapter());
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    @Override
    public void onResponse(ApiData apiData) {
        mFlvDetail = apiData.getDataObject(FlvDetail.class);
        if (mFlvDetail != null) {
            Log.d(TAG, mFlvDetail.getFlvThumb());
            updateView();
        }
    }

    @Override
    public void onComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    private void loadData() {
        mSwipeRefreshLayout.setRefreshing(true);
        RequestAsyncTask.get("https://www.cool1024.com:8000/flv/detail?id=" + mFlvDetail.getId(),
                FlvDetailFragment.this).execute();
    }

    private void updateView() {
        View mainView = getView();
        if (mainView != null) {
            Glide.with(mainView).load(mFlvDetail.getFlvUpAvatar())
                    .apply(new RequestOptions().circleCrop())
                    .into((ImageView) mainView.findViewById(R.id.flv_up_avatar));
            updateTextView(mainView, R.id.flv_up, mFlvDetail.getFlvUp());
            updateTextView(mainView, R.id.flv_up_fans, mFlvDetail.getFlvUpFans());
            updateTextView(mainView, R.id.flv_title, mFlvDetail.getFlvTitle());
            updateTextView(mainView, R.id.flv_view, mFlvDetail.getFlvView());
            updateTextView(mainView, R.id.flv_comment, mFlvDetail.getFlvComment());
        }
    }

    private void updateTextView(View parent, int id, String text) {
        ((TextView) parent.findViewById(id)).setText(text);
    }

    private void updateTextView(View parent, int id, int num) {
        String text = "";
        if (num >= 10000) {
            text = String.format(Locale.CHINA, "%.1f万", num / 10000.0);
        } else {
            text = String.valueOf(num);
        }
        ((TextView) parent.findViewById(id)).setText(text);
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.flv_item, parent, false);
            return new RecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View view) {
                super(view);
            }
        }
    }
}
