package com.example.fitnesstracker;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem; // Import for the "Up" button functionality
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddWorkoutActivity extends AppCompatActivity {

    // UI components for user input and interaction
    private EditText etWorkoutName, etDuration; // Input fields for workout name and duration
    private Spinner spinnerWorkoutType; // Dropdown menu for selecting workout type
    private Button btnSaveWorkout, btnStartTimer, btnStopTimer, btnResetTimer; // Buttons for saving workout and controlling timer
    private Chronometer chronometer; // Timer widget for tracking workout duration
    private long pauseOffset; // Tracks the time offset when the timer is paused
    private boolean isRunning; // Indicates if the timer is currently running

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
        btnStartTimer = findViewById(R.id.btn_start_timer);
        btnStopTimer = findViewById(R.id.btn_stop_timer);
        btnResetTimer = findViewById(R.id.btn_reset_timer);
        chronometer = findViewById(R.id.tv_timer);

        // Populate the workout type dropdown (Spinner) with predefined options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.workout_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkoutType.setAdapter(adapter);

        // Set up button listeners for timer controls
        btnStartTimer.setOnClickListener(v -> startTimer());
        btnStopTimer.setOnClickListener(v -> stopTimer());
        btnResetTimer.setOnClickListener(v -> resetTimer());

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
     * Starts the timer. If the timer is paused, it resumes from the last paused time.
     */
    private void startTimer() {
        if (!isRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            isRunning = true;
            btnStopTimer.setEnabled(true);
            btnResetTimer.setEnabled(true);
        }
    }

    /**
     * Stops the timer and calculates the elapsed time in minutes.
     * The elapsed time is automatically added to the duration input field.
     */
    private void stopTimer() {
        if (isRunning) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            isRunning = false;

            // Calculate elapsed minutes and update the duration field
            int elapsedSeconds = (int) (pauseOffset / 1000);
            int elapsedMinutes = elapsedSeconds / 60;
            etDuration.setText(String.valueOf(elapsedMinutes));
        }
    }

    /**
     * Resets the timer to 00:00 and clears the duration field.
     */
    private void resetTimer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        isRunning = false;
        btnStopTimer.setEnabled(false);
        btnResetTimer.setEnabled(false);
        etDuration.setText(""); // Clear the duration field
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

                // Clear input fields and reset the timer
                etWorkoutName.setText("");
                etDuration.setText("");
                spinnerWorkoutType.setSelection(0);
                resetTimer();
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
