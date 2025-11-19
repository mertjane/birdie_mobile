package com.dev.birdie.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Photo {
    @SerializedName("photo_id")
    private Integer photoId;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("photo_url")
    private String photoUrl;

    @SerializedName("is_primary")
    private Boolean isPrimary;

    @SerializedName("upload_order")
    private Integer uploadOrder;

    @SerializedName("uploaded_at")
    private Date uploadedAt;

    // Constructors
    public Photo() {
    }

    public Photo(Integer userId, String photoUrl, Integer uploadOrder, Boolean isPrimary) {
        this.userId = userId;
        this.photoUrl = photoUrl;
        this.uploadOrder = uploadOrder;
        this.isPrimary = isPrimary;
    }

    // Getters
    public Integer getPhotoId() {
        return photoId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public Integer getUploadOrder() {
        return uploadOrder;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }

    // Setters
    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public void setUploadOrder(Integer uploadOrder) {
        this.uploadOrder = uploadOrder;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "photoId=" + photoId +
                ", userId=" + userId +
                ", photoUrl='" + photoUrl + '\'' +
                ", isPrimary=" + isPrimary +
                ", uploadOrder=" + uploadOrder +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}