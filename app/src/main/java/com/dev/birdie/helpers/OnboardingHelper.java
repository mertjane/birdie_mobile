package com.dev.birdie.helpers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.dev.birdie.MainActivity;
import com.dev.birdie.UserDetails1Activity;
import com.dev.birdie.UserDetails2Activity;
import com.dev.birdie.UserDetails3Activity;
import com.dev.birdie.models.User;

/**
 * Helper class to manage onboarding flow and routing
 */
public class OnboardingHelper {

    private static final String TAG = "OnboardingHelper";

    // Onboarding step constants
    public static final int STEP_NOT_STARTED = 0;
    public static final int STEP_1_COMPLETED = 1;  // Basic details (DOB, gender, location)
    public static final int STEP_2_COMPLETED = 2;  // Interests and preferences
    public static final int ONBOARDING_COMPLETED = 3;  // All steps completed (photos uploaded)

    /**
     * Routes user to appropriate activity based on onboarding step
     *
     * @param activity Current activity
     * @param user     User object with onboarding_step
     */
    public static void routeUserBasedOnOnboardingStep(Activity activity, User user) {
        Integer onboardingStep = user.getOnboardingStep();

        // Default to step 0 if null
        if (onboardingStep == null) {
            onboardingStep = STEP_NOT_STARTED;
        }

        Log.d(TAG, "Routing user based on onboarding step: " + onboardingStep);

        Intent intent;

        switch (onboardingStep) {
            case STEP_NOT_STARTED:
                // User hasn't started onboarding - go to UserDetails1
                Log.d(TAG, "Routing to UserDetails1Activity");
                intent = new Intent(activity, UserDetails1Activity.class);
                break;

            case STEP_1_COMPLETED:
                // User completed step 1 - go to UserDetails2
                Log.d(TAG, "Routing to UserDetails2Activity");
                intent = new Intent(activity, UserDetails2Activity.class);
                break;

            case STEP_2_COMPLETED:
                // User completed step 2 - go to UserDetails3
                Log.d(TAG, "Routing to UserDetails3Activity");
                intent = new Intent(activity, UserDetails3Activity.class);
                break;

            case ONBOARDING_COMPLETED:
                // User completed onboarding - go to main app (MainActivity with HomeFragment)
                Log.d(TAG, "Routing to MainActivity (main app with HomeFragment)");
                intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;

            default:
                // Unknown step - default to UserDetails1
                Log.w(TAG, "Unknown onboarding step: " + onboardingStep + ", defaulting to UserDetails1");
                intent = new Intent(activity, UserDetails1Activity.class);
                break;
        }

        activity.startActivity(intent);
        activity.finish(); // Prevent going back
    }

    /**
     * Checks if user has completed onboarding
     */
    public static boolean hasCompletedOnboarding(User user) {
        Integer step = user.getOnboardingStep();
        return step != null && step >= ONBOARDING_COMPLETED;
    }

    /**
     * Gets user-friendly description of onboarding step
     */
    public static String getStepDescription(int step) {
        switch (step) {
            case STEP_NOT_STARTED:
                return "Not started";
            case STEP_1_COMPLETED:
                return "Basic details completed";
            case STEP_2_COMPLETED:
                return "Preferences completed";
            case ONBOARDING_COMPLETED:
                return "Onboarding completed";
            default:
                return "Unknown step";
        }
    }
}