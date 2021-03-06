package io.theappx.simpletodo.database;

import android.provider.BaseColumns;

public class TodoContract implements BaseColumns {
    public static final String TABLE_NAME = "TodoItems";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TIME_MILLIS = "time";
    public static final String COLUMN_REMIND = "remind";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_DONE = "done";

    //Added in version 6
    public static final String COLUMN_REPEAT_INTERVAL = "repeatInterval";
}
