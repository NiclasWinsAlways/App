package com.example.fitnesstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDatabaseManager extends SQLiteOpenHelper {

    // Database Information
    private static final String DATABASE_NAME = "WorkoutTracker.db"; // Database file name
    private static final int DATABASE_VERSION = 1; // Database version for upgrades

    // Table Name and Column Names
    private static final String TABLE_WORKOUTS = "workouts"; // Table to store workouts
    private static final String COLUMN_ID = "id"; // Unique ID for each workout
    private static final String COLUMN_NAME = "name"; // Name of the workout
    private static final String COLUMN_DURATION = "duration"; // Duration of the workout
    private static final String COLUMN_TYPE = "type"; // Type of workout (e.g., Cardio, Strength)

    // Constructor
    public WorkoutDatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the workouts table when the database is initialized.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_WORKOUTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_DURATION + " TEXT NOT NULL, " +
                COLUMN_TYPE + " TEXT NOT NULL)";
        db.execSQL(createTable); // Execute the SQL command to create the table
    }

    /**
     * Handles database upgrades by recreating the workouts table.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS); // Drop old table if it exists
        onCreate(db); // Recreate the table
    }

    /**
     * Adds a new workout to the database.
     *
     * @param name     The name of the workout
     * @param duration The duration of the workout
     * @param type     The type of workout
     * @return True if the insertion was successful, false otherwise
     */
    public boolean addWorkout(String name, String duration, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_TYPE, type);

        long result = db.insert(TABLE_WORKOUTS, null, values); // Insert new workout
        return result != -1; // Return true if insertion succeeded
    }

    /**
     * Retrieves all workouts from the database.
     *
     * @return A list of formatted workout strings
     */
    public List<String> getAllWorkouts() {
        List<String> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WORKOUTS, null, null, null, null, null, COLUMN_ID + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Format each workout into a string
                String workout = "Name: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)) +
                        "\nDuration: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)) +
                        " minutes\nType: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                workouts.add(workout);
            } while (cursor.moveToNext());
            cursor.close(); // Close the cursor to free resources
        }

        return workouts;
    }

    /**
     * Deletes all workouts from the database.
     */
    public void deleteAllWorkouts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORKOUTS, null, null); // Delete all rows in the table
    }

    /**
     * Gets the total number of workouts logged in the database.
     *
     * @return The total workout count
     */
    public int getTotalWorkouts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_WORKOUTS, null);
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0); // Get the count from the query result
            cursor.close();
            return count;
        }
        return 0; // Return 0 if no workouts are found
    }

    /**
     * Gets the total duration of all workouts combined.
     *
     * @return The total duration in minutes
     */
    public int getTotalDuration() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_DURATION + ") FROM " + TABLE_WORKOUTS, null);
        if (cursor != null && cursor.moveToFirst()) {
            int totalDuration = cursor.getInt(0); // Get the total duration
            cursor.close();
            return totalDuration;
        }
        return 0; // Return 0 if no duration is found
    }

    /**
     * Gets the most frequently logged workout type.
     *
     * @return The most frequent workout type, or null if no workouts exist
     */
    public String getMostFrequentWorkoutType() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_TYPE + ", COUNT(" + COLUMN_TYPE + ") AS type_count " +
                        "FROM " + TABLE_WORKOUTS + " GROUP BY " + COLUMN_TYPE +
                        " ORDER BY type_count DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            String type = cursor.getString(0); // Get the workout type
            cursor.close();
            return type;
        }
        return null; // Return null if no data exists
    }

    /**
     * Retrieves the IDs of all workouts.
     *
     * @return A list of workout IDs
     */
    public List<Integer> getWorkoutIds() {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUTS, new String[]{COLUMN_ID}, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))); // Add each ID to the list
            } while (cursor.moveToNext());
            cursor.close();
        }

        return ids;
    }

    /**
     * Updates an existing workout in the database.
     *
     * @param id       The ID of the workout to update
     * @param name     The new name of the workout
     * @param duration The new duration of the workout
     * @param type     The new type of workout
     * @return True if the update was successful, false otherwise
     */
    public boolean updateWorkout(int id, String name, String duration, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_TYPE, type);

        int rowsUpdated = db.update(TABLE_WORKOUTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsUpdated > 0; // Return true if at least one row was updated
    }

    /**
     * Retrieves the details of a specific workout.
     *
     * @param id The ID of the workout to retrieve
     * @return An array containing the workout's name, duration, and type
     */
    public String[] getWorkoutDetails(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUTS, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
            cursor.close();
            return new String[]{name, duration, type};
        }
        return null; // Return null if no workout is found
    }
}
