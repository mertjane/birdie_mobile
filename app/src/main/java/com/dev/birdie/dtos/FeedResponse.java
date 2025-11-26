package com.dev.birdie.dtos;

import com.dev.birdie.models.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<User> data;

    public boolean isSuccess() {
        return success;
    }

    public List<User> getData() {
        return data;
    }
}