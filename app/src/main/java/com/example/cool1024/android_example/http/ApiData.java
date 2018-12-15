package com.example.cool1024.android_example.http;

import android.util.Base64;
import android.util.Log;

import com.example.cool1024.android_example.classes.Album;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApiData {
    private final static String TAG = "ApiDataLog";

    private boolean result;
    private String message;
    private JsonElement data;

    public static ApiData errorApiData(String message) {
        return new ApiData(Boolean.FALSE, message);
    }

    public ApiData(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public ApiData(String responseString) {
        Log.d(TAG, "响应消息解析：" + responseString);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(responseString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        try {
            result = jsonObject.get("result").getAsBoolean();
            message = jsonObject.get("message").getAsString();
            data = jsonObject.get("data");
        } catch (Exception e) {
            e.printStackTrace();
            message = e.toString();
        }
    }

    public boolean getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public JsonElement getData() {
        return data;
    }

    public <T> T getDataObject(Class<T> classOfT) {
        return new Gson().fromJson(data.toString(), classOfT);
    }
}
