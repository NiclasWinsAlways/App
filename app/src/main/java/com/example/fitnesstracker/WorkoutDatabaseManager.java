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
    private static final String DATABASE_NAME = "WorkoutTracker.db";
    private static final int DATABASE_VERSION = 2; // Updated version to handle schema changes

    // Table Name and Column Names
    private static final String TABLE_WORKOUTS = "workouts";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_COMPLETED = "completed"; // New column for completion status

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
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_COMPLETED + " INTEGER DEFAULT 0)"; // Default to not completed
        db.execSQL(createTable);
    }

    /**
     * Handles database upgrades.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add 'completed' column during upgrade
            db.execSQL("ALTER TABLE " + TABLE_WORKOUTS + " ADD COLUMN " + COLUMN_COMPLETED + " INTEGER DEFAULT 0");
        }
    }

    /**
     * Adds a new workout to the database.
     */
    public boolean addWorkout(String name, String duration, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_COMPLETED, 0); // Default to not completed

        long result = db.insert(TABLE_WORKOUTS, null, values);
        return result != -1;
    }

    /**
     * Retrieves all workouts from the database.
     */
    public List<String> getAllWorkouts() {
        List<String> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WORKOUTS, null, null, null, null, null, COLUMN_ID + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String workout = "Name: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)) +
                        "\nDuration: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)) +
                        " minutes\nType: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)) +
                        (cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1 ? " (Completed)" : "");
                workouts.add(workout);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workouts;
    }

    /**
     * Retrieves the IDs of all workouts.
     */
    public List<Integer> getWorkoutIds() {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUTS, new String[]{COLUMN_ID}, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return ids;
    }

    /**
     * Deletes all workouts from the database.
     */
    public void deleteAllWorkouts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORKOUTS, null, null);
    }

    /**
     * Deletes a specific workout by ID.
     */
    public boolean deleteWorkoutById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_WORKOUTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsDeleted > 0;
    }

    /**
     * Updates an existing workout in the database.
     */
    public boolean updateWorkout(int id, String name, String duration, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_TYPE, type);

        int rowsUpdated = db.update(TABLE_WORKOUTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsUpdated > 0;
    }

    /**
     * Marks a workout as complete.
     */
    public boolean markWorkoutAsComplete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETED, 1); // Set to completed

        int rowsUpdated = db.update(TABLE_WORKOUTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsUpdated > 0;
    }

    /**
     * Retrieves details of a specific workout.
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
        return null;
    }

    /**
     * Retrieves the total number of workouts.
     */
    public int getTotalWorkouts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_WORKOUTS, null);
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }
        return 0;
    }

    /**
     * Retrieves the total duration of all workouts.
     */
    public int getTotalDuration() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_DURATION + ") FROM " + TABLE_WORKOUTS, null);
        if (cursor != null && cursor.moveToFirst()) {
            int totalDuration = cursor.getInt(0);
            cursor.close();
            return totalDuration;
        }
        return 0;
    }

    /**
     * Retrieves the most frequent workout type.
     */
    public String getMostFrequentWorkoutType() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_TYPE + ", COUNT(" + COLUMN_TYPE + ") AS type_count " +
                        "FROM " + TABLE_WORKOUTS + " GROUP BY " + COLUMN_TYPE +
                        " ORDER BY type_count DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            String type = cursor.getString(0);
            cursor.close();
            return type;
        }
        return "None";
    }
    public List<String> getWorkoutsByType(String type) {
        List<String> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUTS, null, COLUMN_TYPE + " = ?", new String[]{type}, null, null, COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String workout = "Name: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)) +
                        "\nDuration: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)) +
                        " minutes\nType: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                workouts.add(workout);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return workouts;
    }

    public List<String> getWorkoutsByStatus(String status) {
        List<String> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = (status.equals("Completed")) ? COLUMN_COMPLETED + " = 1" : COLUMN_COMPLETED + " = 0";
        Cursor cursor = db.query(TABLE_WORKOUTS, null, selection, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String workout = "Name: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)) +
                        "\nDuration: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)) +
                        " minutes\nType: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)) +
                        (cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1 ? " (Completed)" : "");
                workouts.add(workout);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return workouts;
    }
    // Add this method to WorkoutDatabaseManager
    // Add this method to fetch workout IDs by type
    public List<Integer> getWorkoutIdsByType(String workoutType) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get IDs of workouts by type
        String selection = COLUMN_TYPE + " = ?";
        String[] selectionArgs = new String[]{workoutType};
        Cursor cursor = db.query(TABLE_WORKOUTS, new String[]{COLUMN_ID}, selection, selectionArgs, null, null, COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return ids;
    }

    // Add this method to fetch workout IDs by status
    public List<Integer> getWorkoutIdsByStatus(String status) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Determine if we're filtering by completed or not completed
        String selection = (status.equals("Completed")) ? COLUMN_COMPLETED + " = 1" : COLUMN_COMPLETED + " = 0";
        Cursor cursor = db.query(TABLE_WORKOUTS, new String[]{COLUMN_ID}, selection, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return ids;
    }

}
