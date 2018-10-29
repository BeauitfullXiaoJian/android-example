package com.example.cool1024.android_example.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseTabFragment {

    public static final String TAG = "HomeFragment";

    private static final String SAVE_DATA_TAG = "CARD_DATA";

    private List<CardData> mCards = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (savedInstanceState == null) {
            Log.d(TAG, "LOAD_DATA_NEW");
            loadCardData();
        } else {
            Log.d(TAG, "LOAD_DATA_SAVE");
            loadCardDataFromSave(savedInstanceState);
        }
        initView(view);
        onHiddenChanged(false);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "SAVE_DATA");
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_DATA_TAG, new CardDataList().cardListToJsonString(mCards));
    }

    private void loadCardDataFromSave(Bundle savedInstanceState) {
        String jsonString = savedInstanceState.getString(SAVE_DATA_TAG);
        mCards = new CardDataList().getCardListFromJsonString(jsonString);
    }

    private void loadCardData() {
        if (mCards.size() > 0) {
            return;
        }
        Log.d(TAG, "LOAD_CARD_DATA");
        mCards.add(new CardData("Marty McFly",
                "November 5,1955",
                "Wait a minute. Wait a minute, Doc. Uh... Are you telling me that you built a time machine... out of a DeLorean?!Whoa. This is heavy.",
                "https://www.cool1024.com/ng/assets/images/avatar/0.jpg",
                "https://picsum.photos/600/400?10"));
        mCards.add(new CardData("Sarah Connor",
                "May 12,1984",
                "I face the unknown future, with a sense of hope. Because if a machine, a Terminator, can learn the value ofhuman life, maybe we can too.",
                "https://www.cool1024.com/ng/assets/images/avatar/2.jpg",
                "https://picsum.photos/600/400?20"));
        mCards.add(new CardData("Dr.lan Malcolm",
                "June 28,1990",
                "Your scientists were so preoccupied with whether or not they could, that they didn't stop to think if they should",
                "https://www.cool1024.com/ng/assets/images/avatar/3.jpg",
                "https://picsum.photos/600/400?30"));
    }

    private void initView(View view) {
        Log.d(TAG, "INIT_VIEW");
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 1);
        CardAdapter adapter = new CardAdapter(mCards);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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

    public class CardData {
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

        String getCardTitle() {
            return cardTitle;
        }

        String getCardBody() {
            return cardBody;
        }

        String getCardAvatarUrl() {
            return cardAvatarUrl;
        }

        String getCardImageUrl() {
            return cardImageUrl;
        }

        String getCardContent() {
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
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position == 0) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
                Float density = getResources().getDisplayMetrics().density;
                params.topMargin = (int) (getResources().getInteger(R.integer.default_margin_num) * density);
            }
            CardData cardData = mDataList.get(position);
            Glide.with(HomeFragment.this)
                    .load(cardData.getCardAvatarUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(holder.avatarImageView);
            Glide.with(HomeFragment.this)
                    .load(cardData.getCardImageUrl())
                    .into(holder.cardImageView);
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
