package com.dev.birdie.network;

public class ApiConfig {
    // RENDER URL AFTER DEPLOYMENT
    // public static final String BASE_URL = "https://birdie-backend-mb4a.onrender.com";

    // For local testing:
    // For Android Emulator:
    public static final String BASE_URL = "http://10.0.2.2:3000/";

    // For Real Device (replace with computer's IP):
    // public static final String BASE_URL = "http://192.168.1.XXX:3000";

    public static final String USERS_ENDPOINT = "api/users";
    public static final String USER_INTERESTS_ENDPOINT = "api/user-interests";
    public static final String PHOTOS_ENDPOINT = "api/photos";
    public static final String FEEDS_ENDPOINT = "api/home";

    public static final int CONNECT_TIMEOUT = 30; // seconds
    public static final int READ_TIMEOUT = 30; // seconds
    public static final int WRITE_TIMEOUT = 30; // seconds

}
