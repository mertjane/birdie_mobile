package com.dev.birdie.managers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Manages location permissions and retrieval
 */
public class LocationManager {

    private static final String TAG = "LocationManager";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;

    public LocationManager(Activity activity) {
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    /**
     * Callback interface for location operations
     */
    public interface LocationCallback {
        void onLocationReceived(String postcode, double latitude, double longitude);

        void onLocationError(String error);

        void onPermissionDenied();
    }

    /**
     * Checks if location permission is granted
     */
    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests location permission
     */
    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    /**
     * Gets current location and converts to postcode
     */
    public void getCurrentLocationPostcode(LocationCallback callback) {
        if (!hasLocationPermission()) {
            callback.onPermissionDenied();
            return;
        }

        // Request fresh location instead of using cached getLastLocation()
        com.google.android.gms.location.LocationRequest locationRequest =
                new com.google.android.gms.location.LocationRequest.Builder(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 1000)
                        .setMaxUpdates(1)
                        .build();

        com.google.android.gms.location.LocationCallback locationCallback =
                new com.google.android.gms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(com.google.android.gms.location.LocationResult locationResult) {
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            Location location = locationResult.getLastLocation();
                            Log.d(TAG, "Fresh location received - Lat: " + location.getLatitude() +
                                    ", Lng: " + location.getLongitude());
                            convertLocationToPostcode(location, callback);
                            fusedLocationClient.removeLocationUpdates(this);
                        } else {
                            callback.onLocationError("Unable to get current location");
                        }
                    }
                };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error requesting location updates", e);
                    callback.onLocationError("Error getting location");
                });
    }

    /**
     * Converts location to postcode using Geocoder
     */
    private void convertLocationToPostcode(Location location, LocationCallback callback) {
        Geocoder geocoder = new Geocoder(activity, Locale.UK); // Use UK locale

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Log all address components for debugging
                Log.d(TAG, "Address found:");
                Log.d(TAG, "  Country: " + address.getCountryName());
                Log.d(TAG, "  Admin Area: " + address.getAdminArea());
                Log.d(TAG, "  Locality: " + address.getLocality());
                Log.d(TAG, "  SubLocality: " + address.getSubLocality());
                Log.d(TAG, "  Postal Code: " + address.getPostalCode());
                Log.d(TAG, "  Feature Name: " + address.getFeatureName());
                Log.d(TAG, "  Thoroughfare: " + address.getThoroughfare());

                String postcode = extractPostcode(address);

                if (postcode != null && !postcode.isEmpty()) {
                    Log.d(TAG, "Extracted postcode: " + postcode);
                    callback.onLocationReceived(postcode, latitude, longitude);
                } else {
                    callback.onLocationError("Postcode not available for this location");
                }
            } else {
                Log.w(TAG, "No addresses found for location");
                callback.onLocationError("Unable to determine postcode");
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error", e);
            callback.onLocationError("Error retrieving postcode");
        }
    }

    /**
     * Extracts postcode from Address object, prioritizing UK format
     */
    private String extractPostcode(Address address) {
        // Try postal code first
        String postcode = address.getPostalCode();

        if (postcode != null && !postcode.isEmpty()) {
            // Check if it looks like a UK postcode (starts with letters)
            if (postcode.matches("^[A-Z]{1,2}[0-9].*")) {
                return postcode;
            }
        }

        // Fallback: Try to build from locality/admin area for UK
        if ("United Kingdom".equals(address.getCountryName()) ||
                "GB".equals(address.getCountryCode())) {

            // For UK, try locality first
            String locality = address.getLocality();
            if (locality != null && !locality.isEmpty()) {
                Log.d(TAG, "Using locality as fallback: " + locality);
                return locality;
            }

            // Then try sub-locality
            String subLocality = address.getSubLocality();
            if (subLocality != null && !subLocality.isEmpty()) {
                Log.d(TAG, "Using sub-locality as fallback: " + subLocality);
                return subLocality;
            }

            // Finally admin area
            String adminArea = address.getAdminArea();
            if (adminArea != null && !adminArea.isEmpty()) {
                Log.d(TAG, "Using admin area as fallback: " + adminArea);
                return adminArea;
            }
        }

        return postcode; // Return whatever we have, even if null
    }
}