package io.theappx.simpletodo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "TODO";
    private static final int DATABASE_VERSION = 5;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TODO_TABLE =
                "CREATE TABLE " + TodoContract.TABLE_NAME + "(" +
                        TodoContract.COLUMN_ID + " TEXT NOT NULL," +
                        TodoContract.COLUMN_TITLE + " TEXT NOT NULL," +
                        TodoContract.COLUMN_DESCRIPTION + " TEXT," +
                        TodoContract.COLUMN_TIME_MILLIS + " INTEGER," +
                        TodoContract.COLUMN_REMIND + " INTEGER DEFAULT 0," +
                        TodoContract.COLUMN_COLOR + " INTEGER," +
                        TodoContract.COLUMN_DONE + " INTEGER DEFAULT 0," +
                        "UNIQUE (" + TodoContract.COLUMN_ID + ") ON CONFLICT IGNORE);";

        db.execSQL(SQL_CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TodoContract.TABLE_NAME);
        onCreate(db);
    }
}
