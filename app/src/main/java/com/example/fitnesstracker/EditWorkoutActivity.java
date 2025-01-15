package com.example.fitnesstracker;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditWorkoutActivity extends AppCompatActivity {

    // UI components for editing workout details
    private EditText etWorkoutName, etDuration; // Input fields for workout name and duration
    private Spinner spinnerWorkoutType; // Dropdown menu for selecting workout type
    private Button btnUpdateWorkout; // Button to save the updated workout
    private int workoutId; // ID of the workout being edited, passed via intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);

        // Enable the "Up" button in the app bar for navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        etWorkoutName = findViewById(R.id.et_workout_name);
        etDuration = findViewById(R.id.et_duration);
        spinnerWorkoutType = findViewById(R.id.spinner_workout_type);
        btnUpdateWorkout = findViewById(R.id.btn_update_workout);

        // Populate the workout type spinner with predefined options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.workout_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkoutType.setAdapter(adapter);

        // Get the workout ID from the intent
        workoutId = getIntent().getIntExtra("WORKOUT_ID", -1);

        // Load workout details if a valid ID is provided
        if (workoutId != -1) {
            loadWorkoutDetails(workoutId);
        }

        // Set up the button listener for updating the workout
        btnUpdateWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWorkout();
            }
        });
    }

    /**
     * Loads the details of the workout being edited.
     * Retrieves the data from the database and populates the UI fields.
     *
     * @param id The ID of the workout to be loaded
     */
    private void loadWorkoutDetails(int id) {
        WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);
        String[] details = dbManager.getWorkoutDetails(id);

        if (details != null) {
            // Populate the input fields with the workout details
            etWorkoutName.setText(details[0]);
            etDuration.setText(details[1]);

            // Set the spinner to the correct workout type
            String type = details[2];
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerWorkoutType.getAdapter();
            int position = adapter.getPosition(type);
            spinnerWorkoutType.setSelection(position);
        }
    }

    /**
     * Updates the workout in the database with the new details provided by the user.
     * Validates input fields before saving changes.
     */
    private void updateWorkout() {
        // Retrieve user input
        String name = etWorkoutName.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        String type = spinnerWorkoutType.getSelectedItem().toString();

        // Validate input fields
        if (name.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the workout in the database
        WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);
        boolean success = dbManager.updateWorkout(workoutId, name, duration, type);

        // Provide feedback to the user
        if (success) {
            Toast.makeText(this, "Workout updated successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to the previous screen
        } else {
            Toast.makeText(this, "Error updating workout!", Toast.LENGTH_SHORT).show();
        }
    }
}
