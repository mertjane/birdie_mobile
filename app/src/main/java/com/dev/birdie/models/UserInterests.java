package com.dev.birdie.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserInterests {
    @SerializedName("interest_id")
    private int interestId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("interest_name")
    private String interestName;

    @SerializedName("created_at")
    private Date createdAt;

    // Constructors
    public UserInterests() {
    }

    public UserInterests(int userId, String interestName) {
        this.userId = userId;
        this.interestName = interestName;
    }

    public UserInterests(int interestId, int userId, String interestName, Date createdAt) {
        this.interestId = interestId;
        this.userId = userId;
        this.interestName = interestName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getInterestId() {
        return interestId;
    }

    public void setInterestId(int interestId) {
        this.interestId = interestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserInterests{" +
                "interestId=" + interestId +
                ", userId=" + userId +
                ", interestName='" + interestName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}