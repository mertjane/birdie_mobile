package com.dev.birdie.helpers;

import android.app.Activity;
import android.content.Intent;

import com.dev.birdie.GreetingActivity;
import com.dev.birdie.LoginActivity;
import com.dev.birdie.RegisterActivity;
import com.dev.birdie.models.User;

/**
 * Helper class to manage navigation between activities
 */
public class NavigationHelper {

    private final Activity activity;

    public NavigationHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * Navigates to Register Activity
     */
    public void navigateToRegister() {
        Intent intent = new Intent(activity, RegisterActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Navigates to Login Activity
     */
    public void navigateToLogin() {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Navigates to Login Activity and finishes current
     */
    public void navigateToLoginAndFinish() {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * Navigates to Greeting/Main Activity after successful login
     */
    public void navigateToMain(User user) {
        Intent intent = new Intent(activity, GreetingActivity.class);
        // Optional: Pass user data if needed
        // intent.putExtra("user_id", user.getId());
        // intent.putExtra("user_name", user.getFullName());
        activity.startActivity(intent);
        activity.finish(); // Prevent going back to login
    }

    /**
     * Navigates to Main Activity without user data
     */
    public void navigateToMain() {
        Intent intent = new Intent(activity, GreetingActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * Generic navigation helper
     */
    public void navigateTo(Class<?> targetActivity, boolean finishCurrent) {
        Intent intent = new Intent(activity, targetActivity);
        activity.startActivity(intent);
        if (finishCurrent) {
            activity.finish();
        }
    }
}