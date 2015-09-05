package com.example.barolog;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by xDens on 7/22/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "barolog.db";
    public static final String DATABASE_TABLE = "pressure_values";
    private static final int DATABASE_VERSION = 1;
    public static final String PRESSURE_VALUE_COLUMN = "pressure_value";
    public static final String PRESSURE_MEASUREMENT_TIME_COLUMN = "pressure_measurement_time";

    private static final String DATABASE_CREATE_SCRIPT = "create table if not exists "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + PRESSURE_MEASUREMENT_TIME_COLUMN
            + " datetime not null, " + PRESSURE_VALUE_COLUMN + " real not null);";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Log the action
        Log.w("SQLite", "Update DB from version: " + oldVersion + " to version " + newVersion);

        // Remove old table and create new one
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        // Create new table again
        onCreate(db);
    }

}
