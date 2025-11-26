package com.dev.birdie.dtos;

import com.google.gson.annotations.SerializedName;

public class SwipeRequest {
    @SerializedName("swiperId")
    private int swiperId;

    @SerializedName("swipedId")
    private int swipedId;

    @SerializedName("swipeType")
    private String swipeType; // "LIKE" or "UNLIKE"

    public SwipeRequest(int swiperId, int swipedId, String swipeType) {
        this.swiperId = swiperId;
        this.swipedId = swipedId;
        this.swipeType = swipeType;
    }
}