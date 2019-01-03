package com.example.cool1024.android_example.http;

import android.os.AsyncTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RequestAsyncTask extends AsyncTask<RequestParam, Integer, ApiData> {

    private static OkHttpClient sOkHttpClient;
    private static final String GET = "GET";
    private static final String DELETE = "DELETE";
    private static final String POST = "POST";
    private static final String PUT = "PUT";

    private String mRequestUrl;
    private String mRequestMethod;
    private ResponseCallback mResponseCallback;

    public static RequestAsyncTask get(String requestUrl, ResponseCallback responseCallback) {
        return new RequestAsyncTask(requestUrl, GET, responseCallback);
    }

    public static RequestAsyncTask post(String requestUrl, ResponseCallback responseCallback) {
        return new RequestAsyncTask(requestUrl, POST, responseCallback);
    }

    public static RequestAsyncTask put(String requestUrl, ResponseCallback responseCallback) {
        return new RequestAsyncTask(requestUrl, PUT, responseCallback);
    }

    public static RequestAsyncTask delete(String requestUrl, ResponseCallback responseCallback) {
        return new RequestAsyncTask(requestUrl, DELETE, responseCallback);
    }

    private static OkHttpClient getRequestClient() {
        if (sOkHttpClient == null) {
            sOkHttpClient = new OkHttpClient();
        }
        return sOkHttpClient;
    }

    private RequestAsyncTask(String requestUrl, String requestMethod,
                             ResponseCallback responseCallback) {
        mRequestUrl = requestUrl;
        mRequestMethod = requestMethod;
        mResponseCallback = responseCallback;
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

    @Override
    protected void onPostExecute(ApiData apiData) {
        if (apiData != null) {
            if (apiData.getResult()) {
                mResponseCallback.onResponse(apiData);
            } else {
                mResponseCallback.onError(apiData.getMessage());
            }
        } else {
            mResponseCallback.onError("数据请求失败");
        }
        mResponseCallback.onComplete();
    }


    public interface ResponseCallback {
        void onResponse(ApiData apiData);

        void onError(String errorMsg);

        void onComplete();
    }
}
