package com.example.fitnesstracker;

import android.os.Bundle;
import android.view.MenuItem; // Import for the "Up" button functionality
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddWorkoutActivity extends AppCompatActivity {

    // UI components for user input and interaction
    private EditText etWorkoutName, etDuration; // Input fields for workout name and duration
    private Spinner spinnerWorkoutType; // Dropdown menu for selecting workout type
    private Button btnSaveWorkout; // Button for saving the workout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        // Enable "Up" button in the app bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        etWorkoutName = findViewById(R.id.et_workout_name);
        etDuration = findViewById(R.id.et_duration);
        spinnerWorkoutType = findViewById(R.id.spinner_workout_type);
        btnSaveWorkout = findViewById(R.id.btn_save_workout);

        // Populate the workout type dropdown (Spinner) with predefined options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.workout_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkoutType.setAdapter(adapter);

        // Set up button listener for saving workout
        btnSaveWorkout.setOnClickListener(v -> saveWorkout());
    }

    /**
     * Enables navigation back to the previous screen using the "Up" button.
     *
     * @param item The selected menu item
     * @return True if the action was handled, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Handle "Up" button click
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves the workout data entered by the user into the database.
     * Handles errors and ensures all required fields are filled before saving.
     */
    private void saveWorkout() {
        try {
            // Get user input
            String name = etWorkoutName.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();
            String type = spinnerWorkoutType.getSelectedItem().toString();

            // Validate input fields
            if (name.isEmpty() || duration.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save workout to the database
            WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);
            boolean success = dbManager.addWorkout(name, duration, type);

            // Provide feedback to the user
            if (success) {
                Toast.makeText(this, "Workout saved!", Toast.LENGTH_SHORT).show();

                // Clear input fields
                etWorkoutName.setText("");
                etDuration.setText("");
                spinnerWorkoutType.setSelection(0);
            } else {
                Toast.makeText(this, "Error saving workout!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle unexpected errors and display an error message
            Toast.makeText(this, "An unexpected error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace(); // Log the error for debugging purposes
        }
    }
}
