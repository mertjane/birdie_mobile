package com.dev.birdie.network;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client;
    private static Gson gson;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        gson = new GsonBuilder()
                .setLenient()
                .create();
    }

    public static OkHttpClient getClient() {
        return client;
    }

    public static Gson getGson() {
        return gson;
    }

    public static RequestBody createJsonRequestBody(Object object) {
        String json = gson.toJson(object);
        Log.d(TAG, "Request JSON: " + json);
        return RequestBody.create(json, JSON);
    }
}
