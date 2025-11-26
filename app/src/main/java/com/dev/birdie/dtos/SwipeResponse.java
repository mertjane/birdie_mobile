package com.dev.birdie.dtos;

import com.google.gson.annotations.SerializedName;

public class SwipeResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private SwipeResult data;

    public boolean isSuccess() {
        return success;
    }

    public SwipeResult getData() {
        return data;
    }

    public static class SwipeResult {
        @SerializedName("isMatch")
        private boolean isMatch;

        @SerializedName("remainingSwipes")
        private int remainingSwipes;

        public boolean isMatch() {
            return isMatch;
        }

        public int getRemainingSwipes() {
            return remainingSwipes;
        }
    }
}