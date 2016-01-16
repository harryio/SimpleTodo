package io.theappx.simpletodo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    public static final String DAY_FORMAT = "M D, yyyy";
    public static final String TIME_FORMAT = "K:mm AA";
    public static final String DATE_FORMAT = DAY_FORMAT + " " + TIME_FORMAT;

    private FormatUtils(){}

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

    public static String getDayStringFromDate(Date pDate) {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(DAY_FORMAT, Locale.getDefault());
        return lSimpleDateFormat.format(pDate);
    }

    public static String getStringFromDate(Date pDate) {
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return lSimpleDateFormat.format(pDate);
    }
}
