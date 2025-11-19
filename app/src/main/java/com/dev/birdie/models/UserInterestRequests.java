package com.dev.birdie.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Request models for UserInterests API calls
 */
public class UserInterestRequests {

    /**
     * Request body for adding a single interest
     */
    public static class AddInterestRequest {
        @SerializedName("interest_name")
        private String interestName;

        public AddInterestRequest() {
        }

        public AddInterestRequest(String interestName) {
            this.interestName = interestName;
        }

        public String getInterestName() {
            return interestName;
        }

        public void setInterestName(String interestName) {
            this.interestName = interestName;
        }
    }

    /**
     * Request body for adding multiple interests
     */
    public static class AddMultipleInterestsRequest {
        @SerializedName("interests")
        private List<String> interests;

        public AddMultipleInterestsRequest() {
        }

        public AddMultipleInterestsRequest(List<String> interests) {
            this.interests = interests;
        }

        public List<String> getInterests() {
            return interests;
        }

        public void setInterests(List<String> interests) {
            this.interests = interests;
        }
    }

    /**
     * Request body for updating (replacing) all interests
     */
    public static class UpdateInterestsRequest {
        @SerializedName("interests")
        private List<String> interests;

        public UpdateInterestsRequest() {
        }

        public UpdateInterestsRequest(List<String> interests) {
            this.interests = interests;
        }

        public List<String> getInterests() {
            return interests;
        }

        public void setInterests(List<String> interests) {
            this.interests = interests;
        }
    }
}