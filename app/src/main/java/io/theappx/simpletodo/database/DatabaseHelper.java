package io.theappx.simpletodo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.List;

import io.theappx.simpletodo.utils.DbUtils;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "TODO";
    private static final int DATABASE_VERSION = 6;

    final String SQL_CREATE_TODO_TABLE_V5 =
            "CREATE TABLE " + TodoContract.TABLE_NAME + "(" +
                    TodoContract.COLUMN_ID + " TEXT NOT NULL," +
                    TodoContract.COLUMN_TITLE + " TEXT NOT NULL," +
                    TodoContract.COLUMN_DESCRIPTION + " TEXT," +
                    TodoContract.COLUMN_TIME_MILLIS + " INTEGER," +
                    TodoContract.COLUMN_REMIND + " INTEGER DEFAULT 0," +
                    TodoContract.COLUMN_COLOR + " INTEGER," +
                    TodoContract.COLUMN_DONE + " INTEGER DEFAULT 0," +
                    "UNIQUE (" + TodoContract.COLUMN_ID + ") ON CONFLICT IGNORE);";

    final String SQL_CREATE_TODO_TABLE_V6 =
            "CREATE TABLE " + TodoContract.TABLE_NAME + "(" +
                    TodoContract.COLUMN_ID + " TEXT NOT NULL," +
                    TodoContract.COLUMN_TITLE + " TEXT NOT NULL," +
                    TodoContract.COLUMN_DESCRIPTION + " TEXT," +
                    TodoContract.COLUMN_TIME_MILLIS + " INTEGER," +
                    TodoContract.COLUMN_REMIND + " INTEGER DEFAULT 0," +
                    TodoContract.COLUMN_COLOR + " INTEGER," +
                    TodoContract.COLUMN_DONE + " INTEGER DEFAULT 0," +
                    TodoContract.COLUMN_REPEAT_INTERVAL + " TEXT," +
                    "UNIQUE (" + TodoContract.COLUMN_ID + ") ON CONFLICT IGNORE);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TODO_TABLE_V6);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 5) {
            upgradeToVersion6(db);
        }
    }

    private void upgradeToVersion6(SQLiteDatabase db) {
        List<String> columns = DbUtils.getColumns(db, TodoContract.TABLE_NAME);
        db.execSQL("ALTER TABLE " + TodoContract.TABLE_NAME + " RENAME TO "
                + TodoContract.TABLE_NAME + "_Backup;");
        createTodoTable6(db);
        columns.retainAll(DbUtils.getColumns(db, TodoContract.TABLE_NAME));
        String cols = TextUtils.join(",", columns);
        db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM %s_Backup",
                TodoContract.TABLE_NAME, cols, cols, TodoContract.TABLE_NAME));
        db.execSQL("DROP TABLE " + TodoContract.TABLE_NAME + "_Backup");
    }

    private void createTodoTable6(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TODO_TABLE_V6);
    }
}