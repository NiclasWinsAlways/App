package com.example.fitnesstracker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditWorkoutActivity extends AppCompatActivity {

    // UI components for editing workout details
    private EditText etWorkoutName, etDuration;
    private Spinner spinnerWorkoutType;
    private Button btnUpdateWorkout;
    private int workoutId; // ID of the workout being edited, passed via intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);

        // Enable the "Up" button in the app bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        etWorkoutName = findViewById(R.id.et_workout_name);
        etDuration = findViewById(R.id.et_duration);
        spinnerWorkoutType = findViewById(R.id.spinner_workout_type);
        btnUpdateWorkout = findViewById(R.id.btn_update_workout);

        // Populate the workout type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.workout_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkoutType.setAdapter(adapter);

        // Get the workout ID from the intent
        workoutId = getIntent().getIntExtra("WORKOUT_ID", -1);

        // Validate and load workout details
        if (workoutId == -1) {
            showErrorAndExit("Invalid workout ID");
        } else {
            loadWorkoutDetails(workoutId);
        }

        // Set up the button listener for updating the workout
        btnUpdateWorkout.setOnClickListener(v -> updateWorkout());
    }

    /**
     * Loads the details of the workout being edited.
     *
     * @param id The ID of the workout to be loaded
     */
    private void loadWorkoutDetails(int id) {
        WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);
        String[] details = dbManager.getWorkoutDetails(id);

        if (details == null) {
            showErrorAndExit("Workout not found");
            return;
        }

        // Populate input fields
        etWorkoutName.setText(details[0]);
        etDuration.setText(details[1]);

        // Set the spinner to the correct workout type
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerWorkoutType.getAdapter();
        int position = adapter.getPosition(details[2]);
        if (position >= 0) {
            spinnerWorkoutType.setSelection(position);
        } else {
            // Set default value if type not found
            spinnerWorkoutType.setSelection(0);
        }
    }

    /**
     * Updates the workout in the database with the new details provided by the user.
     */
    private void updateWorkout() {
        String name = etWorkoutName.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        String type = spinnerWorkoutType.getSelectedItem().toString();

        if (name.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        WorkoutDatabaseManager dbManager = new WorkoutDatabaseManager(this);
        boolean success = dbManager.updateWorkout(workoutId, name, duration, type);

        if (success) {
            Toast.makeText(this, "Workout updated successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error updating workout!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays an error message and exits the activity.
     *
     * @param message The error message to display
     */
    private void showErrorAndExit(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        setResult(RESULT_CANCELED);
        finish();
    }
}
