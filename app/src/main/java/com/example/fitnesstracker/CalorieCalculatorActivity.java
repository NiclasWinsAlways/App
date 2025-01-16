package com.example.fitnesstracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalorieCalculatorActivity extends AppCompatActivity {

    private EditText etWeight, etDuration; // Input fields for weight and workout duration
    private Spinner spinnerWorkoutType;   // Dropdown for workout type
    private Button btnCalculate;          // Button to calculate calories
    private TextView tvResult;            // TextView to display the result

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calorie_calculator);

        // Apply edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        etWeight = findViewById(R.id.et_weight);
        etDuration = findViewById(R.id.et_duration);
        spinnerWorkoutType = findViewById(R.id.spinner_workout_type);
        btnCalculate = findViewById(R.id.btn_calculate);
        tvResult = findViewById(R.id.tv_result);

        // Set up click listener for the calculate button
        btnCalculate.setOnClickListener(v -> calculateCalories());
    }

    /**
     * Calculates the estimated calories burned based on weight, duration, and workout type.
     */
    private void calculateCalories() {
        try {
            // Retrieve user inputs
            String weightStr = etWeight.getText().toString().trim();
            String durationStr = etDuration.getText().toString().trim();
            String workoutType = spinnerWorkoutType.getSelectedItem().toString();

            // Validate inputs
            if (weightStr.isEmpty() || durationStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double weight = Double.parseDouble(weightStr); // Weight in kg
            double duration = Double.parseDouble(durationStr); // Duration in minutes
            double caloriesBurned = 0;

            // Calculate calories based on workout type
            switch (workoutType) {
                case "Running":
                    caloriesBurned = 0.0175 * 10 * weight * duration; // MET value = 10
                    break;
                case "Cycling":
                    caloriesBurned = 0.0175 * 8 * weight * duration; // MET value = 8
                    break;
                case "Swimming":
                    caloriesBurned = 0.0175 * 7 * weight * duration; // MET value = 7
                    break;
                case "Walking":
                    caloriesBurned = 0.0175 * 3.8 * weight * duration; // MET value = 3.8
                    break;
                default:
                    Toast.makeText(this, "Invalid workout type selected", Toast.LENGTH_SHORT).show();
                    return;
            }

            // Display the result
            tvResult.setText(String.format("Estimated Calories Burned: %.2f", caloriesBurned));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input. Please enter numeric values.", Toast.LENGTH_SHORT).show();
        }
    }
}
