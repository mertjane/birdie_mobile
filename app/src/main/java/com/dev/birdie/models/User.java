package com.dev.birdie.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("firebase_uid")
    private String firebaseUid;

    private String email;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("date_of_birth")
    private String dateOfBirth;

    private Integer age;
    private String horoscope;
    private String gender;

    @SerializedName("location_postcode")
    private String locationPostcode;

    private Double latitude;
    private Double longitude;

    @SerializedName("relationship_preference")
    private String relationshipPreference;

    @SerializedName("looking_for")
    private String lookingFor;

    @SerializedName("preferred_age_min")
    private Integer preferredAgeMin;

    @SerializedName("preferred_age_max")
    private Integer preferredAgeMax;

    @SerializedName("is_active")
    private Boolean isActive;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public User() {
    }

    // Getters
    public Integer getUserId() {
        return userId;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public String getHoroscope() {
        return horoscope;
    }

    public String getGender() {
        return gender;
    }

    public String getLocationPostcode() {
        return locationPostcode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getRelationshipPreference() {
        return relationshipPreference;
    }

    public String getLookingFor() {
        return lookingFor;
    }

    public Integer getPreferredAgeMin() {
        return preferredAgeMin;
    }

    public Integer getPreferredAgeMax() {
        return preferredAgeMax;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setHoroscope(String horoscope) {
        this.horoscope = horoscope;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLocationPostcode(String locationPostcode) {
        this.locationPostcode = locationPostcode;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setRelationshipPreference(String relationshipPreference) {
        this.relationshipPreference = relationshipPreference;
    }

    public void setLookingFor(String lookingFor) {
        this.lookingFor = lookingFor;
    }

    public void setPreferredAgeMin(Integer preferredAgeMin) {
        this.preferredAgeMin = preferredAgeMin;
    }

    public void setPreferredAgeMax(Integer preferredAgeMax) {
        this.preferredAgeMax = preferredAgeMax;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}