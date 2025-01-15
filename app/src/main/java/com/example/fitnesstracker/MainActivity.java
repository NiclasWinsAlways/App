package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // UI components for navigation
    private Button btnAddWorkout, btnViewHistory, btnDeleteHistory, btnSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        btnAddWorkout = findViewById(R.id.btn_add_workout); // Button to navigate to Add Workout screen
        btnViewHistory = findViewById(R.id.btn_view_history); // Button to view workout history
        btnDeleteHistory = findViewById(R.id.btn_delete_history); // Button to delete all workout history
        btnSummary = findViewById(R.id.btn_summary); // Button to navigate to Summary screen

        // Set up click listeners for buttons
        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AddWorkoutActivity
                Intent intent = new Intent(MainActivity.this, AddWorkoutActivity.class);
                startActivity(intent);
            }
        });

        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ViewHistoryActivity
                Intent intent = new Intent(MainActivity.this, ViewHistoryActivity.class);
                startActivity(intent);
            }
        });

        btnDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog before deleting history
                showDeleteConfirmationDialog();
            }
        });

        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SummaryActivity
                Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Displays a confirmation dialog to delete all workout history.
     * If confirmed, deletes all records from the database.
     */
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete History") // Dialog title
                .setMessage("Are you sure you want to delete all workout history?") // Dialog message
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform deletion from the database
                    WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);
                    dbManager.deleteAllWorkouts();

                    // Show success message
                    new AlertDialog.Builder(this)
                            .setTitle("Success")
                            .setMessage("All workouts have been deleted.")
                            .setPositiveButton("OK", null)
                            .show();
                })
                .setNegativeButton("No", null) // Dismiss dialog if "No" is clicked
                .show();
    }
}
