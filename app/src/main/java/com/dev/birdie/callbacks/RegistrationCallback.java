package com.dev.birdie.callbacks;

/**
 * Unified callback interface for registration operations
 */
public interface RegistrationCallback {

    /**
     * Called when registration is successful
     */
    void onRegistrationSuccess();

    /**
     * Called when registration fails
     */
    void onRegistrationFailure(String error);

    /**
     * Called when user already exists (Google Sign-In scenario)
     */
    void onUserAlreadyExists();

    /**
     * Optional: Called when registration process starts
     */
    default void onRegistrationStarted() {
        // Default implementation - override if needed
    }
}