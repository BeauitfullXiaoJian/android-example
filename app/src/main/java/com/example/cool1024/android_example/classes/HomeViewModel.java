package com.example.cool1024.android_example.classes;

import com.example.cool1024.android_example.fragments.HomeFragment;
import com.example.cool1024.android_example.http.ApiData;
import com.example.cool1024.android_example.http.Pagination;
import com.example.cool1024.android_example.http.RequestAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel implements RequestAsyncTask.ResponseCallback {

    private MutableLiveData<Pagination> page;
    private MutableLiveData<List<HomeFragment.CardData>> cards;

    public MutableLiveData<List<HomeFragment.CardData>> getCards() {
        if (cards == null) {
            cards = new MutableLiveData<>();
            loadCards();
        }
        return cards;
    }

    public MutableLiveData<Pagination> getPage() {
        if (page == null) {
            page = new MutableLiveData<>();
            page.setValue(new Pagination());
        }
        return page;
    }

    private void loadCards() {
        final Pagination _page = page.getValue();
        if (_page != null && _page.hasNext()) {
            RequestAsyncTask.get("https://www.cool1024.com:8000/list?" + _page.toQueryString()
                    , HomeViewModel.this).execute();
        }
    }

    @Override
    public void onResponse(ApiData apiData) {
        ApiData.PageData pageData = apiData.getPageData();
        JsonArray rows = pageData.getRows();
        final Pagination _page = page.getValue();
        final List<HomeFragment.CardData> _cards = cards.getValue();
        if (_page != null && _cards != null) {
            _page.total = pageData.getTotal();
            for (int i = 0; i < rows.size(); i++) {
                JsonObject item = rows.get(i).getAsJsonObject();
                HomeFragment.CardData cardData = new HomeFragment.CardData(
                        item.get("title").getAsString(),
                        item.get("body").getAsString(),
                        item.get("content").getAsString(),
                        item.get("avatarUrl").getAsString(),
                        item.get("imageUrl").getAsString()
                );
                if (_page.loadModel == Pagination.LOAD_MORE) {
                    _cards.add(cardData);
                } else {
                    _cards.add(0, cardData);
                }
            }
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(String errorMsg) {

    }
}
