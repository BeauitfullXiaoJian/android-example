package com.example.cool1024.android_example.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.cool1024.android_example.CardDetailActivity;
import com.example.cool1024.android_example.GlideApp;
import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.classes.HomeViewModel;
import com.example.cool1024.android_example.http.ApiData;
import com.example.cool1024.android_example.http.Pagination;
import com.example.cool1024.android_example.http.RequestAsyncTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "HomeFragment";
    private static final String SAVE_DATA_TAG = "CARD_DATA";
    private static final Pagination page = new Pagination();

    private Bundle mSavedInstanceState;
    private RecyclerView mRecyclerView;
    private CardAdapter mCardAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<CardData> mCards = new ArrayList<>();

    private HomeViewModel mHomeViewModel;

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
        findView(view);
        initView();
        recoverState();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "SAVE_DATA");
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_DATA_TAG, new CardDataList().cardListToJsonString(mCards));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == Boolean.FALSE) {
            Log.d(TAG, "显示HOME");
        }
    }

    @Override
    public void onResponse(ApiData apiData) {
        Log.d(TAG, apiData.toString());
        ApiData.PageData pageData = apiData.getPageData();
        JsonArray rows = pageData.getRows();
        page.total = pageData.getTotal();
        for (int i = 0; i < rows.size(); i++) {
            JsonObject item = rows.get(i).getAsJsonObject();
            CardData cardData = new CardData(
                    item.get("title").getAsString(),
                    item.get("body").getAsString(),
                    item.get("content").getAsString(),
                    item.get("avatarUrl").getAsString(),
                    item.get("imageUrl").getAsString()
            );
            if (page.loadModel == Pagination.LOAD_MORE) {
                mCards.add(cardData);
            } else {
                mCards.add(0, cardData);
            }
        }
        showToast("成功加载条" + rows.size() + "数据");
        page.nextPage();
        updateView();
    }

    @Override
    public void onComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        page.setComplete();
    }

    @Override
    public void onRefresh() {
        if (page.loading) {
            return;
        }
        if (page.hasNext()) {
            page.loadModel = Pagination.REFRESH;
            loadData();
        } else {
            showToast("没有新数据了～");
            mSwipeRefreshLayout.setRefreshing(false);
            Log.d(TAG, "当前数量" + mCards.size());
        }
    }

    /**
     * 数据载入方法
     */
    private void loadData() {
        page.setLoading();
        RequestAsyncTask.get("https://www.cool1024.com:8000/list?" + page.toQueryString()
                , HomeFragment.this).execute();
    }

    private void loadMore() {
        if (page.loading) {
            return;
        }
        if (page.hasNext()) {
            page.loadModel = Pagination.LOAD_MORE;
            loadData();
        } else {
            showToast("没有更多数据了～");
            Log.d(TAG, "当前数量" + mCards.size());
        }
    }

    private void findView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.home_swipe);
    }

    private void initView() {
        Log.d(TAG, "INIT_VIEW");
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
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
        // mCardAdapter.notifyDataSetChanged();
        mCardAdapter.notifyItemRangeChanged(page.updateStart(), page.updateCount());
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

    public static class CardData implements Serializable {
        private String cardTitle;
        private String cardBody;
        private String cardContent;
        private String cardAvatarUrl;
        private String cardImageUrl;

        public CardData(String title, String body, String content, String avatarUrl, String imageUrl) {
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
            View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_home_card, parent,
                    false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            final CardData cardData = mDataList.get(position);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            float density = getResources().getDisplayMetrics().density;
            params.topMargin = (int) (getResources().getInteger(R.integer.space_sm_num) * density);
            params.bottomMargin = params.topMargin;
            params.leftMargin = (int) (getResources().getInteger(R.integer.space_sm_num) * density);
            params.rightMargin = params.leftMargin;
            holder.cardView.setLayoutParams(params);
            Log.d(TAG, holder.cardImageView.getWidth() + "图片宽度");
            GlideApp.with(HomeFragment.this)
                    .load(cardData.getCardAvatarUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(holder.avatarImageView);
            GlideApp.with(HomeFragment.this)
                    .load(cardData.getCardImageUrl())
                    .placeholder(R.drawable.bg)
                    .error(R.drawable.bg)
                    .into(holder.cardImageView);

            holder.cardItem.setOnClickListener((v) -> {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra(CardDetailActivity.TAG, cardData);
                startActivity(intent);
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
            View cardItem;
            ImageView avatarImageView;
            ImageView cardImageView;
            TextView titleTextView;
            TextView bodyTextView;
            TextView contentTextView;

            ViewHolder(View view) {
                super(view);
                cardView = (CardView) view;
                cardItem = view.findViewById(R.id.card_item);
                avatarImageView = view.findViewById(R.id.card_avatar);
                cardImageView = view.findViewById(R.id.card_image);
                titleTextView = view.findViewById(R.id.card_title);
                bodyTextView = view.findViewById(R.id.card_body);
                contentTextView = view.findViewById(R.id.card_content);
            }
        }
    }
}
