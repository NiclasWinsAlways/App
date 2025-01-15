package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewHistoryActivity extends AppCompatActivity {

    // UI components and data lists
    private ListView lvWorkoutHistory; // ListView to display workout history
    private List<String> workoutList; // Stores the display data for workouts
    private List<Integer> workoutIds; // Stores the IDs of the workouts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        // Enable the "Up" button in the app bar for navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize the ListView
        lvWorkoutHistory = findViewById(R.id.lv_workout_history);

        // Load workout history from the database
        loadWorkoutHistory();

        // Set up item click listener for editing workouts
        lvWorkoutHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected workout's ID
                int workoutId = workoutIds.get(position);

                // Navigate to EditWorkoutActivity, passing the selected workout ID
                Intent intent = new Intent(ViewHistoryActivity.this, EditWorkoutActivity.class);
                intent.putExtra("WORKOUT_ID", workoutId);
                startActivity(intent);
            }
        });
    }

    /**
     * Loads the workout history from the database.
     * Populates the ListView with the workout details and their corresponding IDs.
     */
    private void loadWorkoutHistory() {
        // Access the database to retrieve workout details and IDs
        WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);
        workoutList = dbManager.getAllWorkouts(); // Fetch workout details (name, duration, type)
        workoutIds = dbManager.getWorkoutIds(); // Fetch corresponding workout IDs

        // If there are no workouts, show a default message
        if (workoutList.isEmpty()) {
            workoutList.add("No workouts found.");
        }

        // Set up the ListView with the retrieved data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workoutList);
        lvWorkoutHistory.setAdapter(adapter);
    }

    /**
     * Handles the "Up" button click in the app bar.
     * Navigates back to the previous screen.
     *
     * @param item The menu item that was clicked
     * @return True if the action was handled, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Navigate back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
