package com.example.fitnesstracker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TimerActivity extends AppCompatActivity {

    private Chronometer chronometer; // Timer widget
    private Button btnStart, btnStop, btnReset; // Timer control buttons
    private long pauseOffset = 0; // Tracks the time offset when paused
    private boolean isRunning = false; // Timer state
    private TimerTask timerTask; // AsyncTask for managing the timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // Enable "Up" button for navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        chronometer = findViewById(R.id.chronometer);
        btnStart = findViewById(R.id.btn_start_timer);
        btnStop = findViewById(R.id.btn_stop_timer);
        btnReset = findViewById(R.id.btn_reset_timer);

        // Start the timer
        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                isRunning = true;
                timerTask = new TimerTask();
                timerTask.execute(); // Start AsyncTask
                Toast.makeText(this, "Timer started", Toast.LENGTH_SHORT).show();
            }
        });

        // Stop the timer
        btnStop.setOnClickListener(v -> {
            if (isRunning) {
                isRunning = false;
                if (timerTask != null) {
                    timerTask.cancel(true); // Stop AsyncTask
                }
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                Toast.makeText(this, "Timer stopped", Toast.LENGTH_SHORT).show();
            }
        });

        // Reset the timer
        btnReset.setOnClickListener(v -> {
            if (timerTask != null) {
                timerTask.cancel(true); // Stop AsyncTask if running
            }
            chronometer.setBase(SystemClock.elapsedRealtime());
            pauseOffset = 0;
            isRunning = false;
            Toast.makeText(this, "Timer reset", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * AsyncTask to simulate background timer updates.
     */
    private class TimerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start(); // Start the Chronometer
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Keep running while not cancelled
            while (isRunning && !isCancelled()) {
                try {
                    Thread.sleep(1000); // Sleep for a second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            chronometer.stop(); // Stop the Chronometer
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            chronometer.stop(); // Ensure the timer stops if AsyncTask ends
        }
    }

    // Handle "Up" button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
