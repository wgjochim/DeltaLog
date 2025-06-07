package com.deltalog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "DeltaLog.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_WORKOUT_TYPES = "WorkoutTypes";
    public static final String TABLE_WORKOUT_SESSIONS = "WorkoutSessions";
    public static final String TABLE_EXERCISES = "Exercises";
    public static final String TABLE_EXERCISE_SETS = "ExerciseSets";

    // WorkoutTypes Columns
    public static final String COLUMN_WORKOUT_TYPE_ID = "workout_type"; // PK
    public static final String COLUMN_WORKOUT_NAME = "name";

    // WorkoutSessions Columns
    public static final String COLUMN_WORKOUT_ID = "workout_id"; // PK
    public static final String COLUMN_SESSION_WORKOUT_TYPE = "workout_type"; // FK
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_DURATION = "duration";

    // Exercises Columns
    public static final String COLUMN_EXERCISE_ID = "exercise_id"; // PK
    public static final String COLUMN_EXERCISE_NAME = "exercise_name";
    public static final String COLUMN_EXERCISE_WORKOUT_ID = "workout_id"; // FK

    // ExerciseSets Columns
    public static final String COLUMN_SET_ID = "set_id"; // PK
    public static final String COLUMN_SET_EXERCISE_ID = "exercise_id"; // FK
    public static final String COLUMN_SET_NUMBER = "set_number";
    public static final String COLUMN_REPS = "reps";
    public static final String COLUMN_WEIGHT = "weight";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // WorkoutTypes Table
        String CREATE_WORKOUT_TYPES = "CREATE TABLE " + TABLE_WORKOUT_TYPES + " (" +
                COLUMN_WORKOUT_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORKOUT_NAME + " TEXT NOT NULL" +
                ");";

        // WorkoutSessions Table
        String CREATE_WORKOUT_SESSIONS = "CREATE TABLE " + TABLE_WORKOUT_SESSIONS + " (" +
                COLUMN_WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SESSION_WORKOUT_TYPE + " INTEGER, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_END_TIME + " TEXT, " +
                COLUMN_DURATION + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_SESSION_WORKOUT_TYPE + ") REFERENCES " +
                TABLE_WORKOUT_TYPES + "(" + COLUMN_WORKOUT_TYPE_ID + ")" +
                ");";

        // Exercises Table
        String CREATE_EXERCISES = "CREATE TABLE " + TABLE_EXERCISES + " (" +
                COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXERCISE_NAME + " TEXT NOT NULL, " +
                COLUMN_EXERCISE_WORKOUT_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_EXERCISE_WORKOUT_ID + ") REFERENCES " +
                TABLE_WORKOUT_SESSIONS + "(" + COLUMN_WORKOUT_ID + ")" +
                ");";

        // ExerciseSets Table
        String CREATE_EXERCISE_SETS = "CREATE TABLE " + TABLE_EXERCISE_SETS + " (" +
                COLUMN_SET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SET_EXERCISE_ID + " INTEGER, " +
                COLUMN_SET_NUMBER + " INTEGER, " +
                COLUMN_REPS + " INTEGER, " +
                COLUMN_WEIGHT + " REAL, " +
                "FOREIGN KEY(" + COLUMN_SET_EXERCISE_ID + ") REFERENCES " +
                TABLE_EXERCISES + "(" + COLUMN_EXERCISE_ID + ")" +
                ");";

        // Execute creation statements
        db.execSQL(CREATE_WORKOUT_TYPES);
        db.execSQL(CREATE_WORKOUT_SESSIONS);
        db.execSQL(CREATE_EXERCISES);
        db.execSQL(CREATE_EXERCISE_SETS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Inserting a new Workout Name when creating a Workout in Workout Selector
    public long insertWorkoutType(String workoutName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WORKOUT_NAME, workoutName);

        return db.insert(TABLE_WORKOUT_TYPES, null, values); // returns new row ID
    }


    // All Workout Names
    public Cursor getAllWorkoutTypes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_WORKOUT_TYPES + " ORDER BY " + COLUMN_WORKOUT_TYPE_ID + " ASC", null);
    }


    // New Workout Entry
    public long insertWorkoutSession(int workoutTypeId, String startTime, String endTime, String duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_WORKOUT_TYPE, workoutTypeId);
        values.put(COLUMN_START_TIME, startTime);
        values.put(COLUMN_END_TIME, endTime);
        values.put(COLUMN_DURATION, duration);
        return db.insert(TABLE_WORKOUT_SESSIONS, null, values); // Returns new workout id
    }

    // Saving Exercise Name to a given Workout Id
    public long insertExercise(String exerciseName, long workoutId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXERCISE_NAME, exerciseName);
        values.put(COLUMN_EXERCISE_WORKOUT_ID, workoutId);
        return db.insert(TABLE_EXERCISES, null, values); // return new exercise_id
    }

    // Saving Set, Weight and Reps to a given Exercise Id
    public long insertExerciseSet(long exerciseId, int setNumber, int reps, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SET_EXERCISE_ID, exerciseId);
        values.put(COLUMN_SET_NUMBER, setNumber);
        values.put(COLUMN_REPS, reps);
        values.put(COLUMN_WEIGHT, weight);
        return db.insert(TABLE_EXERCISE_SETS, null, values);
    }

    // find last workout with the same workout_type
    public int getLatestWorkoutId(int workoutType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COLUMN_WORKOUT_ID + ") FROM " + TABLE_WORKOUT_SESSIONS + " WHERE " + COLUMN_SESSION_WORKOUT_TYPE + " = ?",
                new String[]{String.valueOf(workoutType)}
        );
        if (cursor.moveToFirst()) {
            int latestId = cursor.getInt(0);
            cursor.close();
            return latestId;
        } else {
            cursor.close();
            return -1; // no previous workout found
        }
    }

    // find all exercise names and exercise ids from a workout id
    public List<Exercise> getExercisesForWorkout(long workoutId) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT exercise_id, exercise_name FROM exercises WHERE workout_id = ?",
                new String[]{String.valueOf(workoutId)}
        );

        while (cursor.moveToNext()) {
            long exerciseId = cursor.getLong(0);
            String name = cursor.getString(1);
            List<ExerciseSet> sets = getExerciseSets(exerciseId);
            exercises.add(new Exercise(exerciseId, name, sets));
        }

        cursor.close();
        return exercises;
    }

    // find all sets, weights and reps from an exercise id
    public List<ExerciseSet> getExerciseSets(long exerciseId) {
        List<ExerciseSet> sets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_REPS + ", " + COLUMN_WEIGHT +
                        " FROM " + TABLE_EXERCISE_SETS +
                        " WHERE " + COLUMN_SET_EXERCISE_ID + " = ?" +
                        " ORDER BY " + COLUMN_SET_NUMBER + " ASC",
                new String[]{String.valueOf(exerciseId)}
        );

        while (cursor.moveToNext()) {
            int reps = cursor.getInt(0);
            float weight = cursor.getFloat(1);
            sets.add(new ExerciseSet(reps, weight));
        }

        cursor.close();
        return sets;
    }

    // Update Workout Names
    public int updateWorkoutTypeName(int workoutTypeId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORKOUT_NAME, newName);

        return db.update(TABLE_WORKOUT_TYPES, values, COLUMN_WORKOUT_TYPE_ID + " = ?", new String[]{String.valueOf(workoutTypeId)});
    }

    // Get all past Workouts and their date, name and duration
    public List<WorkoutSession> getWorkoutHistory() {
        List<WorkoutSession> sessions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s." + COLUMN_WORKOUT_ID + ", t." + COLUMN_WORKOUT_NAME + ", s." + COLUMN_START_TIME + ", s." + COLUMN_DURATION +
                " FROM " + TABLE_WORKOUT_SESSIONS + " s" +
                " JOIN " + TABLE_WORKOUT_TYPES + " t ON s." + COLUMN_SESSION_WORKOUT_TYPE + " = t." + COLUMN_WORKOUT_TYPE_ID +
                " ORDER BY s." + COLUMN_START_TIME + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int workoutId = cursor.getInt(0);
            String workoutName = cursor.getString(1);
            String startTime = cursor.getString(2);
            String duration = cursor.getString(3);

            sessions.add(new WorkoutSession(workoutId, workoutName, startTime, duration));
        }
        cursor.close();
        return sessions;
    }

}
