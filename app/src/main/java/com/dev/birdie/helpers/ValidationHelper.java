package com.dev.birdie.helpers;

import android.text.TextUtils;

/**
 * Helper class for input validation
 */
public class ValidationHelper {

    /**
     * Validates email and password for login
     *
     * @return Error message if validation fails, null if validation passes
     */
    public static String validateLoginCredentials(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            return "Please enter your email";
        }

        if (TextUtils.isEmpty(password)) {
            return "Password cannot be empty";
        }

        if (!isValidEmail(email)) {
            return "Please enter a valid email address";
        }

        return null; // Validation passed
    }

    /**
     * Validates registration credentials
     *
     * @return Error message if validation fails, null if validation passes
     */
    public static String validateRegistrationCredentials(String name, String email,
                                                         String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            return "Please enter your name";
        }

        if (TextUtils.isEmpty(email)) {
            return "Please enter your email";
        }

        if (!isValidEmail(email)) {
            return "Please enter a valid email address";
        }

        if (TextUtils.isEmpty(password)) {
            return "Password cannot be empty";
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            return "Please confirm your password";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        return null; // Validation passed
    }

    /**
     * Validates user details form (date of birth, horoscope, gender, postcode)
     *
     * @return Error message if validation fails, null if validation passes
     */
    public static String validateUserDetails(String dateOfBirth, int horoscopePosition,
                                             int genderId, String postcode) {
        if (TextUtils.isEmpty(dateOfBirth)) {
            return "Please select your date of birth";
        }

        if (horoscopePosition == 0) { // 0 is placeholder
            return "Please select your horoscope";
        }

        if (genderId == -1) { // -1 means no selection
            return "Please select your gender";
        }

        if (TextUtils.isEmpty(postcode)) {
            return "Please enter your postcode";
        }

        return null; // Validation passed
    }

    /**
     * Validates if email format is correct
     */
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validates password strength (optional - can be enhanced)
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    /**
     * Validates postcode format (basic validation)
     */
    public static boolean isValidPostcode(String postcode) {
        return !TextUtils.isEmpty(postcode) && postcode.trim().length() >= 3;
    }
}