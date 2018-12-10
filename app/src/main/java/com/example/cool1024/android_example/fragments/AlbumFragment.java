package com.example.cool1024.android_example.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.classes.Album;
import com.example.cool1024.android_example.http.RequestAsyncTask;
import com.example.cool1024.android_example.http.RequestParam;

public class AlbumFragment extends Fragment {

    private static final String ALBUM_ID = "albumId";

    // TODO: Rename and change types of parameters
    private Album mAlbum;

    public AlbumFragment() {
        // Required empty public constructor
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
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    private void loadData() {
         RequestAsyncTask.get("").execute(new RequestParam("id", mAlbum.getId()));
    }
}
