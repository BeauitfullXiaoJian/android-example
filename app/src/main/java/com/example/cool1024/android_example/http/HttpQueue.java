package com.example.cool1024.android_example.http;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class HttpQueue {

    private static RequestQueue instance;

    public static RequestQueue getInstance(Context context) {
        if (instance == null) {
            // Instantiate the cache

            Cache cache = new DiskBasedCache(context.getExternalCacheDir(),
                    1024 * 1024);

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            instance = new RequestQueue(cache, network);

            // Start the queue
            instance.start();
        }
        return instance;
    }
}
