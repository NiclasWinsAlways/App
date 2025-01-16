package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;  // Add this import

import java.util.List;

public class ViewHistoryActivity extends AppCompatActivity {

    private ListView lvWorkoutHistory; // ListView to display workout history
    private List<String> workoutList; // Stores the display data for workouts
    private List<Integer> workoutIds; // Stores the IDs of the workouts
    private Spinner spinnerFilterType, spinnerFilterStatus; // Spinners for filtering workouts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        // Enable the "Up" button in the app bar for navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        lvWorkoutHistory = findViewById(R.id.lv_workout_history);
        spinnerFilterType = findViewById(R.id.spinner_filter_type);
        spinnerFilterStatus = findViewById(R.id.spinner_filter_status);

        // Populate the workout type filter spinner
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.workout_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterType.setAdapter(typeAdapter);

        // Populate the completion status filter spinner
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.completion_status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterStatus.setAdapter(statusAdapter);

        // Set up item listeners for filters
        spinnerFilterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedType = parentView.getItemAtPosition(position).toString();
                loadFilteredWorkoutHistory(selectedType, null);  // Filter by workout type
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                loadWorkoutHistory();  // Load all workouts if no filter is selected
            }
        });

        spinnerFilterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedStatus = parentView.getItemAtPosition(position).toString();
                loadFilteredWorkoutHistory(null, selectedStatus);  // Filter by completion status
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                loadWorkoutHistory();  // Load all workouts if no filter is selected
            }
        });

        // Load workout history from the database
        loadWorkoutHistory();

        // Set up item click listener for ListView items
        lvWorkoutHistory.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected workout's ID
            int workoutId = workoutIds.get(position);

            // Show a dialog with options to edit, delete, or mark as complete
            showWorkoutOptionsDialog(workoutId, position);
        });
    }

    private void loadWorkoutHistory() {
        WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);
        workoutList = dbManager.getAllWorkouts(); // Fetch workout details
        workoutIds = dbManager.getWorkoutIds(); // Fetch workout IDs

        if (workoutList.isEmpty()) {
            workoutList.add("No workouts found.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workoutList);
        lvWorkoutHistory.setAdapter(adapter);
    }

    private void loadFilteredWorkoutHistory(String workoutType, String status) {
        WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);

        if (workoutType != null && !workoutType.isEmpty()) {
            workoutList = dbManager.getWorkoutsByType(workoutType); // Fetch workouts by type
            workoutIds = dbManager.getWorkoutIdsByType(workoutType); // Fetch workout IDs by type
        } else if (status != null && !status.isEmpty()) {
            workoutList = dbManager.getWorkoutsByStatus(status); // Fetch workouts by completion status
            workoutIds = dbManager.getWorkoutIdsByStatus(status); // Fetch workout IDs by status
        } else {
            loadWorkoutHistory();  // Load all workouts if no filter is applied
            return;
        }

        if (workoutList.isEmpty()) {
            workoutList.add("No workouts found.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workoutList);
        lvWorkoutHistory.setAdapter(adapter);
    }

    private void showWorkoutOptionsDialog(int workoutId, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Workout Options")
                .setMessage("What would you like to do?")
                .setPositiveButton("Edit", (dialog, which) -> {
                    // Navigate to EditWorkoutActivity
                    Intent intent = new Intent(ViewHistoryActivity.this, EditWorkoutActivity.class);
                    intent.putExtra("WORKOUT_ID", workoutId);
                    startActivityForResult(intent, 1); // Start activity for result
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    // Confirm deletion
                    confirmDeleteWorkout(workoutId, position);
                })
                .setNeutralButton("Mark as Complete", (dialog, which) -> {
                    // Mark the workout as complete
                    markWorkoutAsComplete(workoutId, position);
                })
                .show();
    }

    private void confirmDeleteWorkout(int workoutId, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to delete this workout?")
                .setPositiveButton("Yes", (dialog, which) -> deleteWorkout(workoutId, position))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteWorkout(int workoutId, int position) {
        WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);
        boolean success = dbManager.deleteWorkoutById(workoutId);

        if (success) {
            Toast.makeText(this, "Workout deleted successfully!", Toast.LENGTH_SHORT).show();
            workoutList.remove(position);
            workoutIds.remove(position);
            ((ArrayAdapter) lvWorkoutHistory.getAdapter()).notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Failed to delete workout.", Toast.LENGTH_SHORT).show();
        }
    }

    private void markWorkoutAsComplete(int workoutId, int position) {
        WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);
        boolean success = dbManager.markWorkoutAsComplete(workoutId);

        if (success) {
            Toast.makeText(this, "Workout marked as complete!", Toast.LENGTH_SHORT).show();
            workoutList.set(position, workoutList.get(position) + " (Completed)");
            ((ArrayAdapter) lvWorkoutHistory.getAdapter()).notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Failed to mark workout as complete.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadWorkoutHistory(); // Reload the workout history
        }
    }
}
