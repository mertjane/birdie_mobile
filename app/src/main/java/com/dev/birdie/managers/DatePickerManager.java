package com.dev.birdie.managers;

import android.app.DatePickerDialog;
import android.content.Context;

import java.util.Calendar;
import java.util.Locale;

/**
 * Manages date picker dialog and date formatting
 */
public class DatePickerManager {

    private final Context context;
    private Calendar selectedDate;

    public DatePickerManager(Context context) {
        this.context = context;
        this.selectedDate = Calendar.getInstance();
    }

    /**
     * Callback interface for date selection
     */
    public interface DateSelectedCallback {
        void onDateSelected(String formattedDate, int year, int month, int day);
    }

    /**
     * Shows date picker dialog
     */
    public void showDatePicker(DateSelectedCallback callback) {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    String formattedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                    callback.onDateSelected(formattedDate, selectedYear, selectedMonth, selectedDay);
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
    }

    /**
     * Formats date as DD/MM/YYYY
     */
    public String formatDate(int year, int month, int day) {
        return String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year);
    }

    /**
     * Calculates age from birth date
     */
    public static int calculateAge(int birthYear, int birthMonth, int birthDay) {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birthYear;

        // Check if birthday hasn't occurred yet this year
        int currentMonth = today.get(Calendar.MONTH);
        int currentDay = today.get(Calendar.DAY_OF_MONTH);

        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--;
        }

        return age;
    }

    /**
     * Gets selected date calendar
     */
    public Calendar getSelectedDate() {
        return selectedDate;
    }
}