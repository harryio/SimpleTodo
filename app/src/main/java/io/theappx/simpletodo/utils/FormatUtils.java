package io.theappx.simpletodo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    public static final String DAY_FORMAT = "MMMM dd, yyyy";
    public static final String TIME_FORMAT = "K:mm a";
    public static final String TIME_FORMAT_24_HOUR = "k:mm";
    public static final String DATE_FORMAT = DAY_FORMAT + " " + TIME_FORMAT;
    public static final String COMPACT_DATE_FORMAT = "MMM d, ''yy";
    public static final String COMPACT_DATE_FORMAT_24_HOUR = "MMM D, yy";

    private FormatUtils() {
    }

    public static Date getDateFromString(String lString) {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            return lSimpleDateFormat.parse(lString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
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

    public static String getStringFromDate(Date pDate) {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return lSimpleDateFormat.format(pDate);
    }

    public static String getCompactStringFromDate(Date date, boolean is24Hour) {
        if (DateUtils.isToday(date)) return "Today";
        if (DateUtils.isWithinDaysFuture(date, 1)) return "Tomorrow";
        SimpleDateFormat simpleDateFormat = is24Hour ?
                new SimpleDateFormat(COMPACT_DATE_FORMAT_24_HOUR, Locale.getDefault()) :
                new SimpleDateFormat(COMPACT_DATE_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}
