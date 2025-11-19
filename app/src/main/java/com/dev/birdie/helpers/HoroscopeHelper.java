package com.dev.birdie.helpers;

import android.widget.Spinner;

import com.dev.birdie.adapters.HoroscopeSpinnerAdapter;

/**
 * Helper class for horoscope-related operations
 */
public class HoroscopeHelper {

    /**
     * Calculates horoscope sign based on birth date
     *
     * @param month 0-indexed month (0 = January)
     * @param day   Day of month
     * @return Horoscope sign name
     */
    public static String getHoroscopeSign(int month, int day) {
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

    /**
     * Sets horoscope selection in spinner based on date
     *
     * @param spinner Horoscope spinner
     * @param month   0-indexed month
     * @param day     Day of month
     */
    public static void setHoroscopeInSpinner(Spinner spinner, int month, int day) {
        String horoscope = getHoroscopeSign(month, day);

        HoroscopeSpinnerAdapter adapter = (HoroscopeSpinnerAdapter) spinner.getAdapter();
        if (adapter == null) return;

        // Find and select the horoscope in spinner (skip index 0 which is placeholder)
        for (int i = 1; i < adapter.getCount(); i++) {
            String item = adapter.getItem(i).toString();
            if (item.toLowerCase().contains(horoscope.toLowerCase())) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    /**
     * Gets horoscope emoji based on sign name
     */
    public static String getHoroscopeEmoji(String horoscope) {
        switch (horoscope.toLowerCase()) {
            case "aries":
                return "♈";
            case "taurus":
                return "♉";
            case "gemini":
                return "♊";
            case "cancer":
                return "♋";
            case "leo":
                return "♌";
            case "virgo":
                return "♍";
            case "libra":
                return "♎";
            case "scorpio":
                return "♏";
            case "sagittarius":
                return "♐";
            case "capricorn":
                return "♑";
            case "aquarius":
                return "♒";
            case "pisces":
                return "♓";
            default:
                return "";
        }
    }
}