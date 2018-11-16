package com.example.cool1024.android_example.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cool1024.android_example.DetailActivity;
import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.http.HttpQueue;
import com.example.cool1024.android_example.http.Pagination;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseTabFragment implements Response.Listener<JSONObject>,
        SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "HomeFragment";

    private static final String SAVE_DATA_TAG = "CARD_DATA";

    private static final Pagination page = new Pagination();

    private Activity mParentActivity;

    private Bundle mSavedInstanceState;

    private RecyclerView mRecyclerView;

    private CardAdapter mCardAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RequestQueue mRequestQueue;

    private List<CardData> mCards = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findView(view);
        initView();
        recoverState();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "SAVE_DATA");
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_DATA_TAG, new CardDataList().cardListToJsonString(mCards));
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d(TAG, response.toString());
        try {
            JSONArray rows = response.getJSONArray("rows");
            page.total = response.getInt("total");
            for (int i = 0; i < rows.length(); i++) {
                JSONObject item = rows.getJSONObject(i);
                CardData cardData = new CardData(
                        item.getString("title"),
                        item.getString("body"),
                        item.getString("content"),
                        item.getString("avatarUrl"),
                        item.getString("imageUrl")
                );
                if (page.loadModel == Pagination.LOAD_MORE) {
                    mCards.add(cardData);
                } else {
                    mCards.add(0, cardData);
                }
            }
            updateView();
            showToast("成功加载条" + rows.length() + "数据");
            page.nextPage();
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("接口数据解析失败");
        }
        mSwipeRefreshLayout.setRefreshing(false);
        page.setComplete();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        super.onErrorResponse(error);
        mSwipeRefreshLayout.setRefreshing(Boolean.FALSE);
        page.setComplete();
    }

    @Override
    public void onRefresh() {
        if (page.loading){
            return;
        }
        if (page.hasNext()) {
            page.loadModel =Pagination.REFRESH;
            loadData();
        } else {
            showToast("没有新数据了～");
            mSwipeRefreshLayout.setRefreshing(false);
            updateView();
            Log.d(TAG, "当前数量" + mCards.size());
        }
    }

    /**
     * 数据载入方法
     */
    public void loadData() {
        page.setLoading();
        mRequestQueue.add(new JsonObjectRequest(Request.Method.GET,
                "https://www.cool1024.com:8000/list?" + page.toQueryString(), null,
                HomeFragment.this, HomeFragment.this));
    }

    private void loadMore(){
        if (page.loading){
            return;
        }
        if (page.hasNext()) {
            page.loadModel =Pagination.LOAD_MORE;
            loadData();
        } else {
            showToast("没有更多数据了～");
            updateView();
            Log.d(TAG, "当前数量" + mCards.size());
        }
    }

    private void findView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.home_swipe);
    }

    private void initView() {
        Log.d(TAG, "INIT_VIEW");
        mParentActivity = getActivity();
        if (mParentActivity != null) {
            mRequestQueue = HttpQueue.getInstance(mParentActivity);
        }
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        mCardAdapter = new CardAdapter(mCards);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mCardAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int count = 0;
                int nowShowIndex = 0;
                int visibleThreshold = 1;
                if (linearLayoutManager != null) {
                    count = linearLayoutManager.getItemCount();
                    nowShowIndex = linearLayoutManager.findLastVisibleItemPosition();
                }
                if (count <= (nowShowIndex + visibleThreshold)) {
                    loadMore();
                    Log.d(TAG, "加载更多数据");
                }
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(HomeFragment.this);
    }

    private void updateView() {
        mCardAdapter.notifyDataSetChanged();
    }

    /**
     * 从之前保存的数据中尝试恢复页面状态
     */
    private void recoverState() {
        if (mSavedInstanceState == null) {
            Log.d(TAG, "LOAD_DATA_NEW");
            if (mCards.size() > 0) {
                return;
            }
            Log.d(TAG, "LOAD_CARD_DATA");
            loadData();
        } else {
            Log.d(TAG, "LOAD_DATA_SAVE:");
            String jsonString = mSavedInstanceState.getString(SAVE_DATA_TAG);
            mCards.clear();
            mCards.addAll(new CardDataList().getCardListFromJsonString(jsonString));
            Log.d(TAG, "LOAD_DATA_SAVE:" + mCards.size());
            updateView();
        }
    }

    public class CardDataList {

        List<CardData> getCardListFromJsonString(String jsonString) {
            List<CardData> cards = new ArrayList<>();
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(jsonString).getAsJsonArray();
            for (JsonElement item : array) {
                JsonObject object = item.getAsJsonObject();
                String cardTitle = object.get("cardTitle").getAsString();
                String cardBody = object.get("cardBody").getAsString();
                String cardContent = object.get("cardContent").getAsString();
                String cardAvatarUrl = object.get("cardAvatarUrl").getAsString();
                String cardImageUrl = object.get("cardImageUrl").getAsString();
                cards.add(new CardData(cardTitle, cardBody, cardContent, cardAvatarUrl, cardImageUrl));
            }
            return cards;
        }

        String cardListToJsonString(List<CardData> cards) {
            Gson gson = new Gson();
            return gson.toJson(cards);
        }
    }

    public static class CardData {
        private String cardTitle;
        private String cardBody;
        private String cardContent;
        private String cardAvatarUrl;
        private String cardImageUrl;

        CardData(String title, String body, String content, String avatarUrl, String imageUrl) {
            cardTitle = title;
            cardBody = body;
            cardContent = content;
            cardAvatarUrl = avatarUrl;
            cardImageUrl = imageUrl;
        }

        public String getCardTitle() {
            return cardTitle;
        }

        public String getCardBody() {
            return cardBody;
        }

        public String getCardAvatarUrl() {
            return cardAvatarUrl;
        }

        public String getCardImageUrl() {
            return cardImageUrl;
        }

        public String getCardContent() {
            return cardContent;
        }

        String toJsonString() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }

        public static CardData formJsonString(String jsonString) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
            String cardTitle = jsonObject.get("cardTitle").getAsString();
            String cardBody = jsonObject.get("cardBody").getAsString();
            String cardContent = jsonObject.get("cardContent").getAsString();
            String cardAvatarUrl = jsonObject.get("cardAvatarUrl").getAsString();
            String cardImageUrl = jsonObject.get("cardImageUrl").getAsString();
            return new CardData(cardTitle, cardBody, cardContent, cardAvatarUrl, cardImageUrl);
        }
    }

    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

        private Context mContext;

        private List<CardData> mDataList;

        CardAdapter(List<CardData> dataList) {
            this.mDataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (mContext == null) {
                mContext = parent.getContext();
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            final CardData cardData = mDataList.get(position);
            if (position == 0) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
                Float density = getResources().getDisplayMetrics().density;
                params.topMargin = (int) (getResources().getInteger(R.integer.default_margin_num) * density);
            }
            Glide.with(HomeFragment.this)
                    .load(cardData.getCardAvatarUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(holder.avatarImageView);
            Glide.with(HomeFragment.this)
                    .load(cardData.getCardImageUrl())
                    .into(holder.cardImageView);
            holder.cardImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(mParentActivity, DetailActivity.class);
                    intent.putExtra(DetailActivity.TAG, cardData.toJsonString());
                    startActivity(intent);
                }
            });
            holder.titleTextView.setText(cardData.getCardTitle());
            holder.bodyTextView.setText(cardData.getCardBody());
            holder.contentTextView.setText(cardData.getCardContent());
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            CardView cardView;
            ImageView avatarImageView;
            ImageView cardImageView;
            TextView titleTextView;
            TextView bodyTextView;
            TextView contentTextView;

            ViewHolder(View view) {
                super(view);
                cardView = (CardView) view;
                avatarImageView = view.findViewById(R.id.card_avatar);
                cardImageView = view.findViewById(R.id.card_image);
                titleTextView = view.findViewById(R.id.card_title);
                bodyTextView = view.findViewById(R.id.card_body);
                contentTextView = view.findViewById(R.id.card_content);
            }
        }
    }
}
