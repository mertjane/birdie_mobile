package com.dev.birdie.repositories;

import com.dev.birdie.dtos.FeedResponse;
import com.dev.birdie.dtos.SwipeRequest;
import com.dev.birdie.dtos.SwipeResponse;
import com.dev.birdie.models.User;
import com.dev.birdie.network.RetrofitClient;
import com.dev.birdie.services.HomeService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {
    private static HomeService homeService;

    private static HomeService getService() {
        if (homeService == null) {
            homeService = RetrofitClient.getInstance().createService(HomeService.class);
        }
        return homeService;
    }

    public interface OnFeedLoadedListener {
        void onSuccess(List<User> users);

        void onFailure(String error);
    }

    public interface OnSwipeResultListener {
        void onSuccess(boolean isMatch, int remainingSwipes);

        void onFailure(String error);
    }

    public static void getFeed(int userId, OnFeedLoadedListener listener) {
        Call<FeedResponse> call = getService().getFeed(userId);
        call.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    listener.onSuccess(response.body().getData());
                } else {
                    listener.onFailure("Failed to load feed");
                }
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                listener.onFailure(t.getMessage());
            }
        });
    }

    public static void swipeUser(int swiperId, int swipedId, String type, OnSwipeResultListener listener) {
        SwipeRequest request = new SwipeRequest(swiperId, swipedId, type);
        Call<SwipeResponse> call = getService().swipeUser(request);

        call.enqueue(new Callback<SwipeResponse>() {
            @Override
            public void onResponse(Call<SwipeResponse> call, Response<SwipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SwipeResponse.SwipeResult result = response.body().getData();
                    listener.onSuccess(result.isMatch(), result.getRemainingSwipes());
                } else if (response.code() == 403) {
                    listener.onFailure("Daily swipe limit reached");
                } else {
                    listener.onFailure("Swipe failed");
                }
            }

            @Override
            public void onFailure(Call<SwipeResponse> call, Throwable t) {
                listener.onFailure(t.getMessage());
            }
        });
    }
}