package com.dev.birdie.helpers;

import android.app.Activity;
import android.content.Intent;

import com.dev.birdie.GreetingActivity;
import com.dev.birdie.LoginActivity;
import com.dev.birdie.MainActivity;
import com.dev.birdie.RegisterActivity;
import com.dev.birdie.UserDetails1Activity;
import com.dev.birdie.UserDetails2Activity;
import com.dev.birdie.UserDetails3Activity;
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
     * Navigates to UserDetails2Activity
     */
    public void navigateToUserDetails2() {
        Intent intent = new Intent(activity, UserDetails2Activity.class);
        activity.startActivity(intent);
    }

    /**
     * Navigates to UserDetails1Activity (Back from step2 to 1)
     */
    public void navigateTo2To1() {
        Intent intent = new Intent(activity, UserDetails1Activity.class);
        activity.startActivity(intent);
    }

    /**
     * Navigates to UserDetails3Activity
     */
    public void navigateToUserDetails3() {
        Intent intent = new Intent(activity, UserDetails3Activity.class);
        activity.startActivity(intent);
    }

    /**
     * Navigates to UserDetails2Activity (Back from step3 to 2)
     */
    public void navigateTo3To2() {
        Intent intent = new Intent(activity, UserDetails2Activity.class);
        activity.startActivity(intent);
    }

    /**
     * Navigates to MainActivity (shows HomeFragment by default) after successful onboarding
     * Clears the back stack to prevent returning to onboarding screens
     */
    public void navigateToMainActivity() {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * Navigates to MainActivity with HomeFragment explicitly
     * Same as navigateToMainActivity() but with clear intent
     */
    public void navigateToHome() {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // MainActivity should default to HomeFragment
        activity.startActivity(intent);
        activity.finish();
    }


    /**
     * Navigates to Main Activity without user data
     *
     * @deprecated Use navigateToMainActivity() instead
     */
    @Deprecated
    public void navigateToMain() {
        Intent intent = new Intent(activity, GreetingActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     *
     */

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