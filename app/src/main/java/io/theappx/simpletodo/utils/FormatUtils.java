package io.theappx.simpletodo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    public static final String DAY_FORMAT = "MMMM dd, yyyy";
    public static final String TIME_FORMAT = "K:mm a";
    public static final String TIME_FORMAT_24_HOUR = "k:mm";
    public static final String COMPACT_DATE_FORMAT = "MMM d";

    private FormatUtils() {
    }

    public static String getTimeStringFromDate(Date pDate) {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        return lSimpleDateFormat.format(pDate);
    }

    public static String get24HourTimeStringFromDate(Date pDate) {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(TIME_FORMAT_24_HOUR, Locale.getDefault());
        return lSimpleDateFormat.format(pDate);
    }

    public static String getDayStringFromDate(Date pDate) {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(DAY_FORMAT, Locale.getDefault());
        return lSimpleDateFormat.format(pDate);
    }

    public static String getCompatDateString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(COMPACT_DATE_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}
