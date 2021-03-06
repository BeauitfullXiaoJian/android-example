package com.example.cool1024.android_example.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ApiData {
    private final static String TAG = "ApiDataLog";

    private boolean result;
    private int requestCode;
    private String message;
    private JsonElement data;
    private JSONObject pageData;

    static ApiData errorApiData(String message) {
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
            data = jsonObject.has("data") ? jsonObject.get("data") : jsonObject.get("data" + "s");
        } catch (Exception e) {
            e.printStackTrace();
            message = "响应解析失败，响应内容格式错误";
        }
    }

    public boolean getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }


    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public JsonElement getData() {
        return data;
    }

    public PageData getPageData() {
        PageData pageData = new PageData();
        if (this.data != null) {
            JsonObject object = this.data.getAsJsonObject();
            pageData.setTotal(object.get("total").getAsInt());
            pageData.setRows(object.get("rows").getAsJsonArray());
        } else {
            pageData.setTotal(0);
            pageData.setRows(new JsonArray());
        }
        return pageData;
    }

    public <T> T getDataObject(Class<T> classOfT) {
        T object = null;
        try {
            object = new Gson().fromJson(data, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public class PageData {
        private int total;
        private JsonArray rows;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public JsonArray getRows() {
            return rows;
        }

        public void setRows(JsonArray rows) {
            this.rows = rows;
        }
    }
}
