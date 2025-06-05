package com.deltalog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "DeltaLog.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_WORKOUT_TYPES = "workout_types";
    private static final String TABLE_WORKOUTS = "workouts";
    private static final String TABLE_EXERCISES = "exercises";
    private static final String TABLE_WORKOUT_EXERCISES = "workout_exercises";
    private static final String TABLE_SETS = "sets";

    // Columns for workout_types
    private static final String COLUMN_TYPE_ID = "id";
    private static final String COLUMN_TYPE_NAME = "name";

    // Columns for workouts
    private static final String COLUMN_WORKOUT_ID = "id";
    private static final String COLUMN_WORKOUT_START = "start_time";
    private static final String COLUMN_WORKOUT_END = "end_time";
    private static final String COLUMN_WORKOUT_DURATION = "duration";
    private static final String COLUMN_WORKOUT_TYPE_ID = "workout_type_id";

    // Columns for exercises
    private static final String COLUMN_EXERCISE_ID = "id";
    private static final String COLUMN_EXERCISE_NAME = "name";

    // Columns for workout_exercises
    private static final String COLUMN_WORKOUT_EXERCISE_ID = "id";
    private static final String COLUMN_WORKOUT_EXERCISE_WORKOUT_ID = "workout_id";
    private static final String COLUMN_WORKOUT_EXERCISE_EXERCISE_ID = "exercise_id";
    private static final String COLUMN_WORKOUT_EXERCISE_NAME = "exercise_name";

    // Columns for sets
    private static final String COLUMN_SET_ID = "id";
    private static final String COLUMN_SET_WORKOUT_EXERCISE_ID = "workout_exercise_id";
    private static final String COLUMN_SET_NUMBER = "set_number";
    private static final String COLUMN_SET_REPS = "reps";
    private static final String COLUMN_SET_WEIGHT = "weight";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_WORKOUT_TYPES = "CREATE TABLE " + TABLE_WORKOUT_TYPES + " (" +
                COLUMN_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE_NAME + " TEXT NOT NULL);";

        String CREATE_TABLE_WORKOUTS = "CREATE TABLE " + TABLE_WORKOUTS + " (" +
                COLUMN_WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORKOUT_START + " DATETIME NOT NULL, " +
                COLUMN_WORKOUT_END + " DATETIME, " +
                COLUMN_WORKOUT_DURATION + " INTEGER, " +
                COLUMN_WORKOUT_TYPE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_WORKOUT_TYPE_ID + ") REFERENCES " + TABLE_WORKOUT_TYPES + "(" + COLUMN_TYPE_ID + "));";

        String CREATE_TABLE_EXERCISES = "CREATE TABLE " + TABLE_EXERCISES + " (" +
                COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXERCISE_NAME + " TEXT NOT NULL);";

        String CREATE_TABLE_WORKOUT_EXERCISES = "CREATE TABLE " + TABLE_WORKOUT_EXERCISES + " (" +
                COLUMN_WORKOUT_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORKOUT_EXERCISE_WORKOUT_ID + " INTEGER NOT NULL, " +
                COLUMN_WORKOUT_EXERCISE_EXERCISE_ID + " INTEGER NOT NULL, " +
                COLUMN_WORKOUT_EXERCISE_NAME + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_WORKOUT_EXERCISE_WORKOUT_ID + ") REFERENCES " + TABLE_WORKOUTS + "(" + COLUMN_WORKOUT_ID + "), " +
                "FOREIGN KEY(" + COLUMN_WORKOUT_EXERCISE_EXERCISE_ID + ") REFERENCES " + TABLE_EXERCISES + "(" + COLUMN_EXERCISE_ID + "));";

        String CREATE_TABLE_SETS = "CREATE TABLE " + TABLE_SETS + " (" +
                COLUMN_SET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SET_WORKOUT_EXERCISE_ID + " INTEGER NOT NULL, " +
                COLUMN_SET_NUMBER + " INTEGER NOT NULL, " +
                COLUMN_SET_REPS + " INTEGER NOT NULL, " +
                COLUMN_SET_WEIGHT + " REAL NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_SET_WORKOUT_EXERCISE_ID + ") REFERENCES " + TABLE_WORKOUT_EXERCISES + "(" + COLUMN_WORKOUT_EXERCISE_ID + "));";

        // Execute all CREATE statements
        db.execSQL(CREATE_TABLE_WORKOUT_TYPES);
        db.execSQL(CREATE_TABLE_WORKOUTS);
        db.execSQL(CREATE_TABLE_EXERCISES);
        db.execSQL(CREATE_TABLE_WORKOUT_EXERCISES);
        db.execSQL(CREATE_TABLE_SETS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
