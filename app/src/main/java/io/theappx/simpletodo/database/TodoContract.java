package io.theappx.simpletodo.database;

import android.provider.BaseColumns;

public class TodoContract implements BaseColumns {
    public static final String TABLE_NAME = "TodoItems";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_REMIND = "remind";
}
