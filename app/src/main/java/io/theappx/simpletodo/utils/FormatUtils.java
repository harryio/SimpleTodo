package io.theappx.simpletodo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    public static final String DAY_FORMAT = "MMMM dd, yyyy";
    public static final String COMPACT_DATE_FORMAT = "MMM d";

    private FormatUtils() {
    }

    /**
     * Format date object in the MMMM dd, yyyy format
     * @param pDate date object to be formatted
     * @return formatted string of date
     */
    public static String getDayStringFromDate(Date pDate) {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(DAY_FORMAT, Locale.getDefault());
        return lSimpleDateFormat.format(pDate);
    }

    /**
     * Format date object in the MMMM d format
     * @param date date object to be formatted
     * @return formatted string of date
     */
    public static String getCompatDateString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(COMPACT_DATE_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}
