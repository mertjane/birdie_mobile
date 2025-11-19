package com.dev.birdie;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.birdie.adapters.HoroscopeSpinnerAdapter;
import com.dev.birdie.helpers.HoroscopeHelper;
import com.dev.birdie.helpers.NavigationHelper;
import com.dev.birdie.helpers.ValidationHelper;
import com.dev.birdie.managers.DatePickerManager;
import com.dev.birdie.managers.LocationManager;
import com.dev.birdie.models.User;
import com.dev.birdie.repositories.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Refactored UserDetails1Activity with separated concerns
 */
public class UserDetails1Activity extends AppCompatActivity {

    private static final String TAG = "UserDetails1Activity";

    // UI Components
    private EditText etDateOfBirth;
    private Spinner spinnerHoroscope;
    private RadioGroup radioGroupGender;
    private EditText etPostcode;
    private MaterialButton btnUseLocation;
    private MaterialButton btnNext;

    // Managers
    private LocationManager locationManager;
    private NavigationHelper navigationHelper;
    private DatePickerManager datePickerManager;

    // Store location data
    private Double currentLatitude;
    private Double currentLongitude;

    // Store birth date components for age calculation
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details1);

        initializeViews();
        initializeManagers();
        setupHoroscopeSpinner();
        setupClickListeners();
    }

    /**
     * Initialize all view references
     */
    private void initializeViews() {
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        spinnerHoroscope = findViewById(R.id.spinnerHoroscope);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        etPostcode = findViewById(R.id.etPostcode);
        btnUseLocation = findViewById(R.id.btnUseLocation);
        btnNext = findViewById(R.id.btnNext);
    }

    /**
     * Initialize managers
     */
    private void initializeManagers() {
        locationManager = new LocationManager(this);
        datePickerManager = new DatePickerManager(this);
        navigationHelper = new NavigationHelper(this);
    }

    /**
     * Setup horoscope spinner with custom adapter
     */
    private void setupHoroscopeSpinner() {
        CharSequence[] horoscopeArray = getResources().getTextArray(R.array.horoscope_array);

        HoroscopeSpinnerAdapter adapter = new HoroscopeSpinnerAdapter(
                this,
                R.layout.spinner_item,
                horoscopeArray
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerHoroscope.setAdapter(adapter);

        spinnerHoroscope.setSelection(0, false);
        spinnerHoroscope.post(() -> spinnerHoroscope.setSelection(0));
    }

    /**
     * Setup all click listeners
     */
    private void setupClickListeners() {
        // Date picker
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Location button
        btnUseLocation.setOnClickListener(v -> handleLocationRequest());

        // Next button
        btnNext.setOnClickListener(v -> handleNextButton());
    }

    /**
     * Shows date picker dialog
     */
    private void showDatePicker() {
        datePickerManager.showDatePicker((formattedDate, year, month, day) -> {
            etDateOfBirth.setText(formattedDate);
            HoroscopeHelper.setHoroscopeInSpinner(spinnerHoroscope, month, day);

            // Store birth date components for age calculation
            selectedYear = year;
            selectedMonth = month;
            selectedDay = day;
        });
    }

    /**
     * Handles location button click
     */
    private void handleLocationRequest() {
        if (!locationManager.hasLocationPermission()) {
            locationManager.requestLocationPermission();
            return;
        }

        // Show loading state
        btnUseLocation.setEnabled(false);
        btnUseLocation.setText("Getting location...");

        locationManager.getCurrentLocationPostcode(new LocationManager.LocationCallback() {
            @Override
            public void onLocationReceived(String postcode, double latitude, double longitude) {
                resetLocationButton();
                etPostcode.setText(postcode);

                // Store coordinates
                currentLatitude = latitude;
                currentLongitude = longitude;

                // Log coordinates
                Log.d(TAG, "Location coordinates - Latitude: " + latitude + ", Longitude: " + longitude);
                Log.d(TAG, "Postcode: " + postcode);

                Toast.makeText(UserDetails1Activity.this,
                        "Location found!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationError(String error) {
                resetLocationButton();
                Log.e(TAG, "Location error: " + error);
                Toast.makeText(UserDetails1Activity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied() {
                resetLocationButton();
                Toast.makeText(UserDetails1Activity.this,
                        "Location permission required", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Resets location button to default state
     */
    private void resetLocationButton() {
        btnUseLocation.setEnabled(true);
        btnUseLocation.setText("Use location");
    }

    /**
     * Handles permission request result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LocationManager.LOCATION_PERMISSION_REQUEST_CODE) {
            if (locationManager.hasLocationPermission()) {
                handleLocationRequest();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handles next button click
     */
    private void handleNextButton() {
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        int horoscopePosition = spinnerHoroscope.getSelectedItemPosition();
        int genderId = radioGroupGender.getCheckedRadioButtonId();
        String postcode = etPostcode.getText().toString().trim();

        // Validate all fields
        String validationError = ValidationHelper.validateUserDetails(
                dateOfBirth, horoscopePosition, genderId, postcode);

        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show();
            return;
        }

        // All validations passed - save to database
        saveUserDetails(dateOfBirth, horoscopePosition, genderId, postcode);
    }

    /**
     * Saves user details to database
     */
    private void saveUserDetails(String dateOfBirth, int horoscopePosition,
                                 int genderId, String postcode) {
        // Get current user's Firebase UID
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String firebaseUid = firebaseUser.getUid();

        // Calculate age
        int age = DatePickerManager.calculateAge(selectedYear, selectedMonth, selectedDay);

        // Create User object with collected data
        User user = new User();
        user.setDateOfBirth(dateOfBirth);
        user.setAge(age);
        user.setHoroscope(spinnerHoroscope.getSelectedItem().toString());
        user.setGender(getGenderString(genderId));
        user.setLocationPostcode(postcode);
        user.setLatitude(currentLatitude);
        user.setLongitude(currentLongitude);
        user.setOnboardingStep(1);

        // Log all data
        Log.d(TAG, "========== USER DETAILS ==========");
        Log.d(TAG, "Firebase UID: " + firebaseUid);
        Log.d(TAG, "Date of Birth: " + user.getDateOfBirth());
        Log.d(TAG, "Age: " + user.getAge());
        Log.d(TAG, "Horoscope: " + user.getHoroscope());
        Log.d(TAG, "Gender: " + user.getGender());
        Log.d(TAG, "Postcode: " + user.getLocationPostcode());
        Log.d(TAG, "Latitude: " + user.getLatitude());
        Log.d(TAG, "Longitude: " + user.getLongitude());
        Log.d(TAG, "Onboarding Step: " + user.getOnboardingStep());
        Log.d(TAG, "==================================");

        // Show loading
        btnNext.setEnabled(false);
        btnNext.setText("Saving...");

        // Update user in database
        UserRepository.updateUserDetails(firebaseUid, user, this,
                new UserRepository.OnUserUpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "User details saved successfully to database!");

                        // Reset button
                        btnNext.setEnabled(true);
                        btnNext.setText("Next");

                        Toast.makeText(UserDetails1Activity.this,
                                "Details saved successfully!",
                                Toast.LENGTH_SHORT).show();

                        // Navigate to UserDetails2Activity
                        navigationHelper.navigateToUserDetails2();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to save user details: " + error);

                        // Reset button
                        btnNext.setEnabled(true);
                        btnNext.setText("Next");

                        Toast.makeText(UserDetails1Activity.this,
                                "Failed to save: " + error,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Proceeds to next screen with collected data

     private void proceedToNextScreen(String dateOfBirth, int horoscopePosition,
     int genderId, String postcode) {
     // Create User object with collected data
     User user = new User();
     user.setDateOfBirth(dateOfBirth);
     user.setHoroscope(spinnerHoroscope.getSelectedItem().toString());
     user.setGender(getGenderString(genderId));
     user.setLocationPostcode(postcode);

     Log.d(TAG, "User Details - DOB: " + user.getDateOfBirth() +
     ", Horoscope: " + user.getHoroscope() +
     ", Gender: " + user.getGender() +
     ", Postcode: " + user.getLocationPostcode());

     Toast.makeText(this, "Proceeding to next screen...", Toast.LENGTH_SHORT).show();

     // TODO: Navigate to UserDetails2Activity
     // Intent intent = new Intent(this, UserDetails2Activity.class);
     // intent.putExtra("date_of_birth", user.getDateOfBirth());
     // intent.putExtra("horoscope", user.getHoroscope());
     // intent.putExtra("gender", user.getGender());
     // intent.putExtra("postcode", user.getLocationPostcode());
     // startActivity(intent);
     }
     */

    /**
     * Converts gender radio button ID to string
     */
    private String getGenderString(int genderId) {
        if (genderId == R.id.radioMale) {
            return "Male";
        } else if (genderId == R.id.radioFemale) {
            return "Female";
        } else if (genderId == R.id.radioOther) {
            return "Other";
        }
        return "";
    }
}