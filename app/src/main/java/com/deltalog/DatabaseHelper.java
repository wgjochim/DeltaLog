package com.deltalog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                " ORDER BY s." + COLUMN_WORKOUT_ID + " DESC";

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

    // Deletes Workout
    public void deleteWorkoutSession(int workoutId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete all sets for exercises in this workout
        db.execSQL("DELETE FROM " + TABLE_EXERCISE_SETS + " WHERE " + COLUMN_SET_EXERCISE_ID +
                " IN (SELECT " + COLUMN_EXERCISE_ID + " FROM " + TABLE_EXERCISES +
                " WHERE " + COLUMN_EXERCISE_WORKOUT_ID + " = ?)", new Object[]{workoutId});

        // Delete all exercises for this workout
        db.execSQL("DELETE FROM " + TABLE_EXERCISES +
                " WHERE " + COLUMN_EXERCISE_WORKOUT_ID + " = ?", new Object[]{workoutId});

        // Delete the workout session itself
        db.delete(TABLE_WORKOUT_SESSIONS, COLUMN_WORKOUT_ID + " = ?", new String[]{String.valueOf(workoutId)});
    }

    public Set<LocalDate> getWorkoutDatesForMonth(YearMonth month) {
        Set<LocalDate> dates = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_START_TIME +
                " FROM " + TABLE_WORKOUT_SESSIONS, null);

        if (cursor.moveToFirst()) {
            do {
                String rawDate = cursor.getString(0).split(" ")[0]; // in format DD:MM:YYYY
                try {
                    String[] parts = rawDate.split(":");
                    if (parts.length == 3) {
                        int day = Integer.parseInt(parts[0]);
                        int monthVal = Integer.parseInt(parts[1]);
                        int year = Integer.parseInt(parts[2]);

                        LocalDate parsedDate = LocalDate.of(year, monthVal, day);
                        if (parsedDate.getYear() == month.getYear() && parsedDate.getMonth() == month.getMonth()) {
                            dates.add(parsedDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return dates;
    }


    public List<String> getThisWeeksDurations() {
        List<String> durations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1); // Monday
        LocalDate endOfWeek = startOfWeek.plusDays(6); // Sunday

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_DURATION + ", " + COLUMN_START_TIME +
                " FROM " + TABLE_WORKOUT_SESSIONS, null);

        while (cursor.moveToNext()) {
            String duration = cursor.getString(0); // e.g., "45 min"
            String dateStr = cursor.getString(1).split(" ")[0]; // e.g., "17:06:2025"

            try {
                String[] parts = dateStr.split(":");
                if (parts.length == 3) {
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);
                    LocalDate date = LocalDate.of(year, month, day);

                    if (!date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)) {
                        durations.add(duration);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        return durations;
    }

    public int getLatestWorkoutIdOverall() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COLUMN_WORKOUT_ID + ") FROM " + TABLE_WORKOUT_SESSIONS,
                null
        );

        int latestId = -1;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            latestId = cursor.getInt(0);
        }

        cursor.close();
        return latestId;
    }


    public List<ExerciseComparison> compareAllExercisesWithPrevious(int currentWorkoutId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Step 1: Get workout type
        int workoutType = -1;
        Cursor typeCursor = db.rawQuery(
                "SELECT " + COLUMN_SESSION_WORKOUT_TYPE +
                        " FROM " + TABLE_WORKOUT_SESSIONS +
                        " WHERE " + COLUMN_WORKOUT_ID + " = ?",
                new String[]{String.valueOf(currentWorkoutId)}
        );
        if (typeCursor.moveToFirst()) {
            workoutType = typeCursor.getInt(0);
        }
        typeCursor.close();
        if (workoutType == -1) return null;

        // Step 2: Get previous workout ID of same type
        Cursor prevCursor = db.rawQuery(
                "SELECT " + COLUMN_WORKOUT_ID +
                        " FROM " + TABLE_WORKOUT_SESSIONS +
                        " WHERE " + COLUMN_SESSION_WORKOUT_TYPE + " = ? AND " + COLUMN_WORKOUT_ID + " < ?" +
                        " ORDER BY " + COLUMN_WORKOUT_ID + " DESC LIMIT 1",
                new String[]{String.valueOf(workoutType), String.valueOf(currentWorkoutId)}
        );
        int previousWorkoutId = -1;
        if (prevCursor.moveToFirst()) {
            previousWorkoutId = prevCursor.getInt(0);
        }
        prevCursor.close();
        if (previousWorkoutId == -1) return null;

        // Step 3: Get exercises from both workouts
        List<Exercise> currentExercises = getExercisesForWorkout(currentWorkoutId);
        List<Exercise> previousExercises = getExercisesForWorkout(previousWorkoutId);

        List<ExerciseComparison> results = new ArrayList<>();

        // Step 4: Compare by exercise name
        for (Exercise current : currentExercises) {
            for (Exercise previous : previousExercises) {
                if (current.name.equals(previous.name)) {
                    List<ExerciseSetComparison> comparisons = new ArrayList<>();
                    int setCount = Math.min(current.sets.size(), previous.sets.size());

                    for (int i = 0; i < setCount; i++) {
                        ExerciseSet currSet = current.sets.get(i);
                        ExerciseSet prevSet = previous.sets.get(i);
                        comparisons.add(new ExerciseSetComparison(
                                currSet.reps - prevSet.reps,
                                currSet.weight - prevSet.weight
                        ));
                    }

                    results.add(new ExerciseComparison(current.name, comparisons));
                    break;
                }
            }
        }

        return results;
    }


    public List<Float> getLastTenSetOneWeightsForExercise(String exerciseName) {
        List<Float> weights = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get last 10 exercises of this name ordered by exercise_id (latest first)
        Cursor cursor = db.rawQuery(
                "SELECT e." + COLUMN_EXERCISE_ID +
                        " FROM " + TABLE_EXERCISES + " e" +
                        " WHERE e." + COLUMN_EXERCISE_NAME + " = ?" +
                        " ORDER BY e." + COLUMN_EXERCISE_ID + " DESC LIMIT 10",
                new String[]{exerciseName}
        );

        List<Long> exerciseIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            exerciseIds.add(cursor.getLong(0));
        }
        cursor.close();

        // Now for each exercise_id, get the weight where set_number = 1
        for (long exerciseId : exerciseIds) {
            Cursor setCursor = db.rawQuery(
                    "SELECT " + COLUMN_WEIGHT +
                            " FROM " + TABLE_EXERCISE_SETS +
                            " WHERE " + COLUMN_SET_EXERCISE_ID + " = ? AND " + COLUMN_SET_NUMBER + " = 1",
                    new String[]{String.valueOf(exerciseId)}
            );

            if (setCursor.moveToFirst()) {
                weights.add(setCursor.getFloat(0));
            }
            setCursor.close();
        }

        return weights;
    }

    public List<Integer> getLastTenSetOneRepsForExercise(String exerciseName) {
        List<Integer> repsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT e." + COLUMN_EXERCISE_ID +
                        " FROM " + TABLE_EXERCISES + " e" +
                        " WHERE e." + COLUMN_EXERCISE_NAME + " = ?" +
                        " ORDER BY e." + COLUMN_EXERCISE_ID + " DESC LIMIT 10",
                new String[]{exerciseName}
        );

        List<Long> exerciseIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            exerciseIds.add(cursor.getLong(0));
        }
        cursor.close();

        for (long exerciseId : exerciseIds) {
            Cursor setCursor = db.rawQuery(
                    "SELECT " + COLUMN_REPS +
                            " FROM " + TABLE_EXERCISE_SETS +
                            " WHERE " + COLUMN_SET_EXERCISE_ID + " = ? AND " + COLUMN_SET_NUMBER + " = 1",
                    new String[]{String.valueOf(exerciseId)}
            );

            if (setCursor.moveToFirst()) {
                repsList.add(setCursor.getInt(0));
            }
            setCursor.close();
        }

        return repsList;
    }



    public List<String> getAllUniqueExerciseNames() {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT " + COLUMN_EXERCISE_NAME +
                        " FROM " + TABLE_EXERCISES +
                        " ORDER BY " + COLUMN_EXERCISE_NAME + " ASC",
                null
        );

        while (cursor.moveToNext()) {
            names.add(cursor.getString(0));
        }
        cursor.close();
        return names;
    }


}
