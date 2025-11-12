package com.dev.birdie.models;

public class User {
    private String firebaseUid;
    private String email;
    private String fullName;
    private String dateOfBirth;
    private Integer age;
    private String horoscope;
    private String gender;
    private String locationPostcode;
    private Double latitude;
    private Double longitude;
    private String relationshipPreference;
    private String lookingFor;
    private Integer preferredAgeMin;
    private Integer preferredAgeMax;

    public User() {
    }

    // Getters
    public String getFirebaseUid() { return firebaseUid; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public Integer getAge() { return age; }
    public String getHoroscope() { return horoscope; }
    public String getGender() { return gender; }
    public String getLocationPostcode() { return locationPostcode; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getRelationshipPreference() { return relationshipPreference; }
    public String getLookingFor() { return lookingFor; }
    public Integer getPreferredAgeMin() { return preferredAgeMin; }
    public Integer getPreferredAgeMax() { return preferredAgeMax; }

    // Setters
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setAge(Integer age) { this.age = age; }
    public void setHoroscope(String horoscope) { this.horoscope = horoscope; }
    public void setGender(String gender) { this.gender = gender; }
    public void setLocationPostcode(String locationPostcode) { this.locationPostcode = locationPostcode; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setRelationshipPreference(String relationshipPreference) {
        this.relationshipPreference = relationshipPreference;
    }
    public void setLookingFor(String lookingFor) { this.lookingFor = lookingFor; }
    public void setPreferredAgeMin(Integer preferredAgeMin) { this.preferredAgeMin = preferredAgeMin; }
    public void setPreferredAgeMax(Integer preferredAgeMax) { this.preferredAgeMax = preferredAgeMax; }
}
