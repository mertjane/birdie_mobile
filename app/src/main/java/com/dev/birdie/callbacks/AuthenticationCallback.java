package com.dev.birdie.callbacks;

import com.dev.birdie.models.User;

/**
 * Unified callback interface for authentication operations
 */
public interface AuthenticationCallback {

    /**
     * Called when authentication is successful and user profile is loaded
     */
    void onAuthenticationSuccess(User user);

    /**
     * Called when authentication fails at any stage
     */
    void onAuthenticationFailure(String error);

    /**
     * Called when user profile exists but couldn't be loaded
     */
    void onProfileNotFound(String firebaseUid);

    /**
     * Optional: Called when authentication process starts
     */
    default void onAuthenticationStarted() {
        // Default implementation - override if needed
    }
}