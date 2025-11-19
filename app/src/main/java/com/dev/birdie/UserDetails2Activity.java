package com.dev.birdie;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.birdie.helpers.NavigationHelper;
import com.dev.birdie.helpers.ValidationHelper;
import com.dev.birdie.models.User;
import com.dev.birdie.models.UserInterests;
import com.dev.birdie.repositories.UserInterestsRepository;
import com.dev.birdie.repositories.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class UserDetails2Activity extends AppCompatActivity {

    private static final String TAG = "UserDetails2Activity";

    // UI Components - Interests (FlexboxLayout with RadioButtons)
    private RadioButton radioMusic, radioFitness, radioTravel, radioCooking;
    private RadioButton radioMovies, radioArt, radioFashion, radioGaming;

    // UI Components - Relationship Preference
    private RadioGroup radioGroupRelationship;

    // UI Components - Age Range
    private RadioGroup radioGroupAgeRange;

    // UI Components - Looking For (Goal)
    private RadioButton radioDate, radioFriendship, radioLongTerm, radioShortTerm;

    // Buttons
    private MaterialButton btnNext;
    private MaterialButton btnBack;

    // Managers
    private NavigationHelper navigationHelper;

    // Current user data
    private int currentUserId;
    private String currentFirebaseUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_details2);

        initializeViews();
        initializeManagers();
        getCurrentUser();
        setupClickListeners();
    }

    /**
     * Initialize all view references
     */
    private void initializeViews() {
        // Interest RadioButtons
        radioMusic = findViewById(R.id.radioMusic);
        radioFitness = findViewById(R.id.radioFitness);
        radioTravel = findViewById(R.id.radioTravel);
        radioCooking = findViewById(R.id.radioCooking);
        radioMovies = findViewById(R.id.radioMovies);
        radioArt = findViewById(R.id.radioArt);
        radioFashion = findViewById(R.id.radioFashion);
        radioGaming = findViewById(R.id.radioGaming);

        // Relationship Preference RadioGroup
        radioGroupRelationship = findViewById(R.id.radioGroupRelationship);

        // Age Range RadioGroup
        radioGroupAgeRange = findViewById(R.id.radioGroupAgeRange);

        // Looking For RadioButtons
        radioDate = findViewById(R.id.radioDate);
        radioFriendship = findViewById(R.id.radioFriendship);
        radioLongTerm = findViewById(R.id.radioLongTerm);
        radioShortTerm = findViewById(R.id.radioShortTerm);

        // Buttons
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
    }

    /**
     * Initialize managers
     */
    private void initializeManagers() {
        navigationHelper = new NavigationHelper(this);
    }

    /**
     * Get current user's Firebase UID and fetch user_id
     */
    private void getCurrentUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            navigationHelper.navigateToLoginAndFinish();
            return;
        }

        currentFirebaseUid = firebaseUser.getUid();

        // Fetch user to get user_id
        UserRepository.getUserByFirebaseUid(currentFirebaseUid, this,
                new UserRepository.OnUserFetchListener() {
                    @Override
                    public void onSuccess(User user) {
                        currentUserId = user.getUserId();
                        Log.d(TAG, "Current user_id: " + currentUserId);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to fetch user: " + error);
                        Toast.makeText(UserDetails2Activity.this,
                                "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> navigationHelper.navigateTo2To1());
        btnNext.setOnClickListener(v -> handleNextButton());
    }

    /**
     * Handle next button click
     */
    private void handleNextButton() {
        // Get selected interests
        List<String> selectedInterests = getSelectedInterests();

        // Get relationship preference
        String relationshipPreference = getSelectedRelationshipPreference();

        // Get age range
        AgeRange ageRange = getSelectedAgeRange();

        // Get looking for (goal)
        String lookingFor = getSelectedLookingFor();

        // Validate selections
        String validationError = validateSelections(
                selectedInterests, relationshipPreference, ageRange, lookingFor);

        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data
        saveUserDetails(selectedInterests, relationshipPreference, ageRange, lookingFor);
    }

    /**
     * Get selected interests from RadioButtons
     */
    private List<String> getSelectedInterests() {
        List<String> interests = new ArrayList<>();

        if (radioMusic.isChecked()) interests.add("Music");
        if (radioFitness.isChecked()) interests.add("Fitness");
        if (radioTravel.isChecked()) interests.add("Travel");
        if (radioCooking.isChecked()) interests.add("Cooking");
        if (radioMovies.isChecked()) interests.add("Movies");
        if (radioArt.isChecked()) interests.add("Art");
        if (radioFashion.isChecked()) interests.add("Fashion");
        if (radioGaming.isChecked()) interests.add("Gaming");

        return interests;
    }

    /**
     * Get selected relationship preference
     */
    private String getSelectedRelationshipPreference() {
        int selectedId = radioGroupRelationship.getCheckedRadioButtonId();

        if (selectedId == R.id.radioStraight) return "Straight";
        if (selectedId == R.id.radioGay) return "Gay";
        if (selectedId == R.id.radioLesbian) return "Lesbian";
        if (selectedId == R.id.radioOther) return "Other";

        return null;
    }

    /**
     * Get selected age range
     */
    private AgeRange getSelectedAgeRange() {
        int selectedId = radioGroupAgeRange.getCheckedRadioButtonId();

        if (selectedId == R.id.radio1822) return new AgeRange(18, 22);
        if (selectedId == R.id.radio2333) return new AgeRange(23, 33);
        if (selectedId == R.id.radio3448) return new AgeRange(34, 48);
        if (selectedId == R.id.radio48Plus) return new AgeRange(48, 99);

        return null;
    }

    /**
     * Get selected looking for (goal)
     */
    private String getSelectedLookingFor() {
        if (radioDate.isChecked()) return "Date";
        if (radioFriendship.isChecked()) return "Friendship";
        if (radioLongTerm.isChecked()) return "Long-term relationship";
        if (radioShortTerm.isChecked()) return "Short-term relationship";

        return null;
    }

    /**
     * Validate all selections
     */
    private String validateSelections(List<String> interests, String relationshipPreference,
                                      AgeRange ageRange, String lookingFor) {
        if (interests.isEmpty()) {
            return "Please select at least one interest";
        }

        if (relationshipPreference == null) {
            return "Please select your relationship preference";
        }

        if (ageRange == null) {
            return "Please select your preferred age range";
        }

        if (lookingFor == null) {
            return "Please select what you're looking for";
        }

        return null; // All valid
    }

    /**
     * Save user details to database
     * - Interests → user_interests table (via UserInterestRepository)
     * - Other fields → users table (via UserRepository)
     */
    private void saveUserDetails(List<String> interests, String relationshipPreference,
                                 AgeRange ageRange, String lookingFor) {
        // Disable button and show loading
        btnNext.setEnabled(false);
        btnNext.setText("Saving...");

        // Step 1: Save interests to user_interests table
        UserInterestsRepository.addMultipleUserInterests(currentUserId, interests, this,
                new UserInterestsRepository.OnMultipleInterestsListener() {
                    @Override
                    public void onSuccess(List<UserInterests> addedInterests, String message) {
                        Log.d(TAG, "Interests saved: " + addedInterests.size());

                        // Step 2: Update user table with other fields
                        updateUserTable(relationshipPreference, ageRange, lookingFor);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to save interests: " + error);
                        resetButton();
                        Toast.makeText(UserDetails2Activity.this,
                                "Failed to save interests: " + error,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Update user table with relationship preference, age range, and looking for
     */
    private void updateUserTable(String relationshipPreference, AgeRange ageRange,
                                 String lookingFor) {
        // Create User object with fields to update
        User user = new User();
        user.setRelationshipPreference(relationshipPreference);
        user.setPreferredAgeMin(ageRange.min);
        user.setPreferredAgeMax(ageRange.max);
        user.setLookingFor(lookingFor);
        user.setOnboardingStep(2); // Mark step 2 as completed

        // Log data
        Log.d(TAG, "========== USER DETAILS (Step 2) ==========");
        Log.d(TAG, "Firebase UID: " + currentFirebaseUid);
        Log.d(TAG, "User ID: " + currentUserId);
        Log.d(TAG, "Relationship Preference: " + relationshipPreference);
        Log.d(TAG, "Age Range: " + ageRange.min + "-" + ageRange.max);
        Log.d(TAG, "Looking For: " + lookingFor);
        Log.d(TAG, "Onboarding Step: 2");
        Log.d(TAG, "==========================================");

        // Update user in database
        UserRepository.updateUserDetails(currentFirebaseUid, user, this,
                new UserRepository.OnUserUpdateListener() {  // CHANGED: Removed 'new UserRepository()'
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "User details updated successfully!");
                        resetButton();

                        Toast.makeText(UserDetails2Activity.this,
                                "Details saved successfully!",
                                Toast.LENGTH_SHORT).show();

                        // Navigate to UserDetails3Activity (or main app)
                        navigationHelper.navigateToUserDetails3();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to update user: " + error);
                        resetButton();

                        Toast.makeText(UserDetails2Activity.this,
                                "Failed to save: " + error,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Reset button to default state
     */
    private void resetButton() {
        btnNext.setEnabled(true);
        btnNext.setText("Next");
    }

    /**
     * Helper class for age range
     */
    private static class AgeRange {
        int min;
        int max;

        AgeRange(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }
}