package com.example.cool1024.android_example.http;

import android.os.AsyncTask;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RequestAsyncTask extends AsyncTask<RequestParam, Integer, ApiData> {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient sOkHttpClient;
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String POST = "POST";
    public static final String PUT = "PUT";

    private String mRequestUrl;
    private String mRequestMethod;

    private static OkHttpClient getRequestClient() {
        if (sOkHttpClient == null) {
            sOkHttpClient = new OkHttpClient();
        }
        return sOkHttpClient;
    }

    public static RequestAsyncTask get(String requestUrl) {
        return new RequestAsyncTask(requestUrl, GET);
    }

    public static RequestAsyncTask post(String requestUrl) {
        return new RequestAsyncTask(requestUrl, POST);
    }

    public static RequestAsyncTask put(String requestUrl) {
        return new RequestAsyncTask(requestUrl, PUT);
    }

    public static RequestAsyncTask delete(String requestUrl) {
        return new RequestAsyncTask(requestUrl, DELETE);
    }

    public RequestAsyncTask(String requestUrl, String requestMethod) {
        mRequestUrl = requestUrl;
        mRequestMethod = requestMethod;
    }

    @Override
    protected ApiData doInBackground(RequestParam... params) {
        ApiData apiData = null;
        try {
            OkHttpClient httpClient = RequestAsyncTask.getRequestClient();
            FormBody.Builder builder = new FormBody.Builder();
            for (RequestParam param : params) {
                builder = builder.add(param.getName(), param.getValue());
            }
            RequestBody body = builder.build();
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder = requestBuilder.url(mRequestUrl);
            switch (mRequestMethod) {
                case GET:
                    requestBuilder = requestBuilder.get();
                    break;
                case POST:
                    requestBuilder = requestBuilder.post(body);
                    break;
                case PUT:
                    requestBuilder = requestBuilder.put(body);
                    break;
                case DELETE:
                    requestBuilder = requestBuilder.delete(body);
                    break;
            }
            Response response = httpClient.newCall(requestBuilder.build()).execute();
            ResponseBody responseBody = response.body();
            apiData = responseBody != null
                    ? new ApiData(responseBody.string())
                    : ApiData.errorApiData("响应消息为空");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiData;
    }
}
