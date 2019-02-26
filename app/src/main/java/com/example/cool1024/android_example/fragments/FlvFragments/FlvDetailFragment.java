package com.example.cool1024.android_example.fragments.FlvFragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cool1024.android_example.DevActivity;
import com.example.cool1024.android_example.GlideApp;
import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.classes.FlvDetail;
import com.example.cool1024.android_example.fragments.BaseFragment;
import com.example.cool1024.android_example.http.ApiData;
import com.example.cool1024.android_example.http.RequestAsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class FlvDetailFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "BaseTabFragmentLog";
    private static final String FLV_DETAIL_PARAM = "FLV_DETAIL_PARAM";
    private static final int FLV_DETAIL_REQUEST_CODE = 1;
    private static final int FLV_LIST_REQUEST_CODE = 2;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerAdapter mRecyclerAdapter;
    private FlvDetail mFlvDetail;
    private List<FlvDetail> mFlvRecommends = new ArrayList<>();

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
        return inflater.inflate(R.layout.fragment_flv_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "视频详情，视图创建完成");
        mSwipeRefreshLayout = view.findViewById(R.id.flv_detail_swipe);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(FlvDetailFragment.this);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusableInTouchMode(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new RecyclerAdapter();
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    @Override
    public void onResponse(ApiData apiData) {
        if (apiData.getRequestCode() == FLV_DETAIL_REQUEST_CODE) {
            mFlvDetail = apiData.getDataObject(FlvDetail.class);
        }
        if (apiData.getRequestCode() == FLV_LIST_REQUEST_CODE) {
            FlvDetail[] list = apiData.getDataObject(FlvDetail[].class);
            mFlvRecommends = Arrays.asList(list);
        }
        if (mFlvDetail != null && mFlvRecommends.size() > 0) {
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
        RequestAsyncTask.get("https://www.cool1024.com/detail.json?id=" + mFlvDetail.getId(),
                FlvDetailFragment.this).setRequestCode(FLV_DETAIL_REQUEST_CODE).execute();
        mSwipeRefreshLayout.setRefreshing(true);
        RequestAsyncTask.get("https://www.cool1024.com/list.json?id=" + mFlvDetail.getId(),
                FlvDetailFragment.this).setRequestCode(FLV_LIST_REQUEST_CODE).execute();
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
            updateTextView(mainView, R.id.like_num, mFlvDetail.getFlvGood());
            updateTextView(mainView, R.id.dislike_num, mFlvDetail.getFlvBad());
            updateTextView(mainView, R.id.coin_num, mFlvDetail.getFlvHeart());
            updateTextView(mainView, R.id.collect_num, mFlvDetail.getFlvStar());
            updateTextView(mainView, R.id.share_num, mFlvDetail.getFlvShare());
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void updateTextView(View parent, int id, String text) {
        ((TextView) parent.findViewById(id)).setText(text);
    }

    private void updateTextView(View parent, int id, int num) {
        String text;
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
            Log.d(TAG, "刷新列表");
            FlvDetail flvDetail = mFlvRecommends.get(i);
            viewHolder.titleView.setText(flvDetail.getFlvTitle());
            viewHolder.upView.setText(flvDetail.getFlvUp());
            viewHolder.viewView.setText(String.valueOf(flvDetail.getFlvView()));
            viewHolder.commentView.setText(String.valueOf(flvDetail.getFlvComment()));
            GlideApp.with(FlvDetailFragment.this)
                    .load(flvDetail.getFlvThumb())
                    .placeholder(R.drawable.bg)
                    .into(viewHolder.thumbView);
            viewHolder.cardView.setOnClickListener((view) -> {
                Activity activity = getActivity();
                if (activity != null) {
                    Intent intent = new Intent(activity, DevActivity.class);
                    intent.putExtra(DevActivity.FRAGMENT_NAME_PARAM, FlvFragment.TAG);
                    getActivity().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFlvRecommends.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView titleView;
            TextView upView;
            TextView viewView;
            TextView commentView;
            ImageView thumbView;
            CardView cardView;

            ViewHolder(View view) {
                super(view);
                cardView = (CardView) view;
                titleView = view.findViewById(R.id.flv_item_title);
                upView = view.findViewById(R.id.flv_item_up);
                viewView = view.findViewById(R.id.flv_item_view);
                commentView = view.findViewById(R.id.flv_item_comment);
                thumbView = view.findViewById(R.id.flv_item_thumb);
            }
        }
    }
}
