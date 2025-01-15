package com.example.fitnesstracker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SummaryActivity extends AppCompatActivity {

    // UI components to display summary information
    private TextView tvTotalWorkouts; // TextView for total workouts
    private TextView tvTotalDuration; // TextView for total duration
    private TextView tvMostFrequentType; // TextView for most frequent workout type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Enable the "Up" button in the app bar for navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        tvTotalWorkouts = findViewById(R.id.tv_total_workouts);
        tvTotalDuration = findViewById(R.id.tv_total_duration);
        tvMostFrequentType = findViewById(R.id.tv_most_frequent_type);

        // Load and display summary data
        loadSummary();
    }

    /**
     * Loads summary data from the database and updates the UI.
     * Displays total workouts, total duration, and the most frequent workout type.
     */
    private void loadSummary() {
        // Access the database to retrieve summary information
        WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);

        int totalWorkouts = dbManager.getTotalWorkouts(); // Get total number of workouts
        int totalDuration = dbManager.getTotalDuration(); // Get total workout duration in minutes
        String mostFrequentType = dbManager.getMostFrequentWorkoutType(); // Get the most frequent workout type

        // Update the TextViews with the retrieved data
        tvTotalWorkouts.setText("Total Workouts: " + totalWorkouts);
        tvTotalDuration.setText("Total Duration: " + totalDuration + " minutes");
        tvMostFrequentType.setText("Most Frequent Type: " + (mostFrequentType != null ? mostFrequentType : "None"));
    }

    /**
     * Handles the "Up" button click in the app bar.
     * Navigates back to the previous screen.
     *
     * @param item The menu item that was clicked
     * @return True if the action was handled, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Navigate back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
