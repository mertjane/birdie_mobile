package com.dev.birdie;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dev.birdie.adapters.HoroscopeSpinnerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UserDetails1Activity extends AppCompatActivity {

    private static final String TAG = "UserDetails1Activity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    // Views
    private EditText etDateOfBirth;
    private Spinner spinnerHoroscope;
    private RadioGroup radioGroupGender;
    private EditText etPostcode;
    private MaterialButton btnUseLocation;
    private MaterialButton btnNext;

    // Location
    private FusedLocationProviderClient fusedLocationClient;

    // Date variables
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details1);

        // Initialize views
        initializeViews();

        // Setup horoscope spinner
        setupHoroscopeSpinner();

        // Setup date picker
        setupDatePicker();

        // Setup location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnUseLocation.setOnClickListener(v -> requestLocationPermissionAndGetLocation());

        // Setup next button
        btnNext.setOnClickListener(v -> validateAndProceed());
    }

    private void initializeViews() {
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        spinnerHoroscope = findViewById(R.id.spinnerHoroscope);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        etPostcode = findViewById(R.id.etPostcode);
        btnUseLocation = findViewById(R.id.btnUseLocation);
        btnNext = findViewById(R.id.btnNext);
    }

    private void setupHoroscopeSpinner() {
        // Get horoscope array from resources
        CharSequence[] horoscopeArray = getResources().getTextArray(R.array.horoscope_array);

        // Create custom adapter with placeholder handling
        HoroscopeSpinnerAdapter adapter = new HoroscopeSpinnerAdapter(
                this,
                R.layout.spinner_item,
                horoscopeArray
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerHoroscope.setAdapter(adapter);

        // IMPORTANT: Set selection AFTER setting adapter
        spinnerHoroscope.setSelection(0, false);

        // Force refresh to show placeholder
        spinnerHoroscope.post(() -> spinnerHoroscope.setSelection(0));
    }

    private void setupDatePicker() {
        selectedDate = Calendar.getInstance();

        etDateOfBirth.setOnClickListener(v -> {
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH);
            int day = selectedDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    UserDetails1Activity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        updateDateOfBirthField(selectedYear, selectedMonth, selectedDay);
                        updateHoroscopeBasedOnDate(selectedMonth, selectedDay);
                    },
                    year, month, day
            );

            // Set max date to today (user must be born in the past)
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            // Optional: Set min date (e.g., 100 years ago)
            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.YEAR, -100);
            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

            datePickerDialog.show();
        });
    }

    private void updateDateOfBirthField(int year, int month, int day) {
        String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year);
        etDateOfBirth.setText(formattedDate);
    }

    private void updateHoroscopeBasedOnDate(int month, int day) {
        String horoscope = getHoroscopeSign(month, day);

        // Find and select the horoscope in spinner (skip index 0 which is placeholder)
        HoroscopeSpinnerAdapter adapter = (HoroscopeSpinnerAdapter) spinnerHoroscope.getAdapter();
        for (int i = 1; i < adapter.getCount(); i++) {  // Start from 1 to skip placeholder
            String item = adapter.getItem(i).toString();
            if (item.toLowerCase().contains(horoscope.toLowerCase())) {
                spinnerHoroscope.setSelection(i);
                break;
            }
        }
    }

    private String getHoroscopeSign(int month, int day) {
        // Month is 0-indexed (0 = January)
        if ((month == 0 && day >= 20) || (month == 1 && day <= 18)) return "Aquarius";
        if ((month == 1 && day >= 19) || (month == 2 && day <= 20)) return "Pisces";
        if ((month == 2 && day >= 21) || (month == 3 && day <= 19)) return "Aries";
        if ((month == 3 && day >= 20) || (month == 4 && day <= 20)) return "Taurus";
        if ((month == 4 && day >= 21) || (month == 5 && day <= 20)) return "Gemini";
        if ((month == 5 && day >= 21) || (month == 6 && day <= 22)) return "Cancer";
        if ((month == 6 && day >= 23) || (month == 7 && day <= 22)) return "Leo";
        if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) return "Virgo";
        if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) return "Libra";
        if ((month == 9 && day >= 23) || (month == 10 && day <= 21)) return "Scorpio";
        if ((month == 10 && day >= 22) || (month == 11 && day <= 21)) return "Sagittarius";
        return "Capricorn"; // Dec 22 - Jan 19
    }

    private void requestLocationPermissionAndGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        btnUseLocation.setEnabled(false);
        btnUseLocation.setText("Getting location...");

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    btnUseLocation.setEnabled(true);
                    btnUseLocation.setText("Use location");

                    if (location != null) {
                        getPostcodeFromLocation(location);
                    } else {
                        Toast.makeText(this, "Unable to get location. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    btnUseLocation.setEnabled(true);
                    btnUseLocation.setText("Use location");
                    Log.e(TAG, "Error getting location", e);
                    Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
                });
    }

    private void getPostcodeFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String postcode = address.getPostalCode();

                if (postcode != null && !postcode.isEmpty()) {
                    etPostcode.setText(postcode);
                    Toast.makeText(this, "Location found!", Toast.LENGTH_SHORT).show();
                } else {
                    // Try to get area/locality instead
                    String locality = address.getLocality();
                    if (locality != null) {
                        etPostcode.setText(locality);
                    } else {
                        Toast.makeText(this, "Postcode not available for this location",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Unable to determine postcode", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error", e);
            Toast.makeText(this, "Error retrieving postcode", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateAndProceed() {
        // Validate date of birth
        if (etDateOfBirth.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate horoscope
        if (spinnerHoroscope.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select your horoscope", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate gender
        if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate postcode
        if (etPostcode.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your postcode", Toast.LENGTH_SHORT).show();
            return;
        }

        // All validations passed - proceed to next screen
        proceedToNextScreen();
    }

    private void proceedToNextScreen() {
        // Get all the data
        String dateOfBirth = etDateOfBirth.getText().toString();
        String horoscope = spinnerHoroscope.getSelectedItem().toString();

        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        String gender = "";
        if (selectedGenderId == R.id.radioMale) {
            gender = "Male";
        } else if (selectedGenderId == R.id.radioFemale) {
            gender = "Female";
        } else if (selectedGenderId == R.id.radioOther) {
            gender = "Other";
        }

        String postcode = etPostcode.getText().toString();

        Log.d(TAG, "Date of Birth: " + dateOfBirth);
        Log.d(TAG, "Horoscope: " + horoscope);
        Log.d(TAG, "Gender: " + gender);
        Log.d(TAG, "Postcode: " + postcode);

        // TODO: Save data and navigate to UserDetails2Activity
        Toast.makeText(this, "Proceeding to next screen...", Toast.LENGTH_SHORT).show();

        // Intent intent = new Intent(this, UserDetails2Activity.class);
        // intent.putExtra("date_of_birth", dateOfBirth);
        // intent.putExtra("horoscope", horoscope);
        // intent.putExtra("gender", gender);
        // intent.putExtra("postcode", postcode);
        // startActivity(intent);
    }
}

