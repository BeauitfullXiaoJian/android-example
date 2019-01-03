package com.example.cool1024.android_example.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.classes.Album;
import com.example.cool1024.android_example.http.ApiData;
import com.example.cool1024.android_example.http.RequestAsyncTask;

public class AlbumFragment extends BaseTabFragment {

    private static final String TAG = "AlbumFragmentLog";
    private static final String ALBUM_ID = "albumId";

    private View mMainView;
    private Album mAlbum;

    public AlbumFragment() {

    }

    /**
     * 创建一个相册列表Fragment
     *
     * @param albumId 相册编号
     * @return 新的相册列表Fragment
     */
    public static AlbumFragment newInstance(int albumId) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putInt(ALBUM_ID, albumId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAlbum = new Album();
            mAlbum.setId(getArguments().getInt(ALBUM_ID));
            loadData();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_album, container, false);
        return mMainView;
    }

    @Override
    public void onResponse(ApiData apiData) {
        Log.d(TAG, "获取卡片详情");
        mAlbum = apiData.getDataObject(Album.class);
        Log.d(TAG, "更新视图");
        updateView();
    }

    private void loadData() {
        RequestAsyncTask.get("https://www.cool1024.com:8000/album?id=" + mAlbum.getId(),
                AlbumFragment.this).execute();
    }

    private void updateView() {
        final ImageView albumThumb = mMainView.findViewById(R.id.album_thumb);
        final TextView albumTitle = mMainView.findViewById(R.id.album_title);
        final TextView albumNum = mMainView.findViewById(R.id.album_num);
        Glide.with(AlbumFragment.this)
                .load(mAlbum.getAlbumThumbUrl())
                .into(albumThumb);
        albumTitle.setText(mAlbum.getAlbumTitle());
        albumNum.setText(String.format("共张%d图", mAlbum.getPictureNum()));
    }
}
