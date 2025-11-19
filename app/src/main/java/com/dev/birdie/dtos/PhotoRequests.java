package com.dev.birdie.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class PhotoRequests {

    /**
     * Request body for adding a photo
     */
    public static class AddPhotoRequest {
        @SerializedName("base64_image")  // Changed from photo_url
        private String base64Image;

        @SerializedName("upload_order")
        private Integer uploadOrder;

        @SerializedName("is_primary")
        private Boolean isPrimary;

        public AddPhotoRequest() {
        }

        public AddPhotoRequest(String base64Image, Integer uploadOrder, Boolean isPrimary) {
            this.base64Image = base64Image;
            this.uploadOrder = uploadOrder;
            this.isPrimary = isPrimary;
        }

        public String getBase64Image() {
            return base64Image;
        }

        public void setBase64Image(String base64Image) {
            this.base64Image = base64Image;
        }

        public Integer getUploadOrder() {
            return uploadOrder;
        }

        public void setUploadOrder(Integer uploadOrder) {
            this.uploadOrder = uploadOrder;
        }

        public Boolean getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
        }
    }

    /**
     * Request body for updating a photo
     */
    public static class UpdatePhotoRequest {
        @SerializedName("photo_url")
        private String photoUrl;

        @SerializedName("is_primary")
        private Boolean isPrimary;

        public UpdatePhotoRequest() {
        }

        public UpdatePhotoRequest(String photoUrl, Boolean isPrimary) {
            this.photoUrl = photoUrl;
            this.isPrimary = isPrimary;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public Boolean getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
        }
    }

    /**
     * Request body for reordering photos
     */
    public static class ReorderPhotosRequest {
        @SerializedName("photo_orders")
        private Map<String, Integer> photoOrders;

        public ReorderPhotosRequest() {
        }

        public ReorderPhotosRequest(Map<String, Integer> photoOrders) {
            this.photoOrders = photoOrders;
        }

        public Map<String, Integer> getPhotoOrders() {
            return photoOrders;
        }

        public void setPhotoOrders(Map<String, Integer> photoOrders) {
            this.photoOrders = photoOrders;
        }
    }

    /**
     * Response for photo count
     */
    public static class PhotoCountResponse {
        @SerializedName("count")
        private Integer count;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
