package com.example.weighttracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * The main screen of the app, displaying the current weight, a graph of recent progress,
 * and navigation to other screens.
 */
public class GridActivity extends AppCompatActivity {

    private FloatingActionButton addWeightFab;
    private GraphView graphView;
    private ProgressBar progressBar;
    private TextView currentWeightText;
    private TextView goalText;
    private BottomNavigationView bottomNavigationView;

    private int currentUserId; // To store the ID of the logged-in user
    private DatabaseHelper databaseHelper; // Database helper instance

    // Flag to ensure SMS is sent only once per goal achievement
    private boolean hasGoalBeenMet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        // Get the USER_ID passed from LoginActivity
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            // If no user ID is passed, something went wrong, redirect to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the UI elements
        addWeightFab = findViewById(R.id.add_weight_fab);
        graphView = findViewById(R.id.graph);
        progressBar = findViewById(R.id.progress_bar);
        currentWeightText = findViewById(R.id.current_weight_text);
        goalText = findViewById(R.id.goal_text);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the FloatingActionButton to launch the AddWeightActivity
        addWeightFab.setOnClickListener(v -> {
            Intent intent = new Intent(GridActivity.this, AddWeightActivity.class);
            intent.putExtra("USER_ID", currentUserId); // Pass user ID
            startActivity(intent);
        });

        // Set up the bottom navigation to switch between screens
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_graph) {
                Intent intent = new Intent(GridActivity.this, WeightHistoryActivity.class);
                intent.putExtra("USER_ID", currentUserId); // Pass user ID
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.action_settings) {
                Intent intent = new Intent(GridActivity.this, SettingsActivity.class);
                intent.putExtra("USER_ID", currentUserId); // Pass user ID
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Load user-specific data and update the UI
        updateUIWithUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh UI when returning from other activities (e.g., AddWeightActivity or SettingsActivity)
        updateUIWithUserData();
    }

    /**
     * Loads weight data for the current user from the database and updates the UI.
     */
    private void updateUIWithUserData() {
        List<WeightModel> userWeights = databaseHelper.getAllWeights(currentUserId);
        float goalWeight = databaseHelper.getGoalWeight(currentUserId);

        if (!userWeights.isEmpty()) {
            float currentWeight = userWeights.get(userWeights.size() - 1).getWeight();
            currentWeightText.setText(String.format("%.1f lbs", currentWeight));

            if (goalWeight > 0) {
                goalText.setText(String.format("Goal: %.1f lbs", goalWeight));

                // Progress calculation for progress bar
                int progress = 0;
                if (userWeights.size() == 1) {
                    // If only one entry, progress is 100% if goal is met, else 0%
                    if (currentWeight <= goalWeight) { // Assuming losing weight or maintaining
                        progress = 100; // Goal met with the first entry
                    } else {
                        // If first entry is above goal, progress starts at 0% towards goal
                        progress = 0; 
                    }
                } else { 
                    // For multiple entries, calculate progress relative to the first entry and goal
                    float startWeight = userWeights.get(0).getWeight();

                    if (startWeight == goalWeight) {
                        progress = 100; // Already at goal initially
                    } else if (startWeight > goalWeight) { // User is trying to lose weight
                        // Avoid division by zero if startWeight == goalWeight in this branch
                        if (startWeight - goalWeight > 0) {
                             progress = (int) (((startWeight - currentWeight) / (startWeight - goalWeight)) * 100);
                        } else {
                             progress = currentWeight <= goalWeight ? 100 : 0;
                        }
                    } else { // startWeight < goalWeight, User is trying to gain weight
                        // Avoid division by zero if goalWeight == startWeight in this branch
                        if (goalWeight - startWeight > 0) {
                            progress = (int) (((currentWeight - startWeight) / (goalWeight - startWeight)) * 100);
                        } else {
                            progress = currentWeight >= goalWeight ? 100 : 0;
                        }
                    }
                }

                // Cap progress at 100% and ensure it's not negative
                if (progress < 0) progress = 0;
                if (progress > 100) progress = 100;

                progressBar.setProgress(progress);

                // Check if goal is met and send SMS
                if (currentWeight <= goalWeight && !hasGoalBeenMet) { // Assuming goal is to reach or go below goalWeight
                    sendGoalReachedSms(currentWeight, goalWeight);
                    hasGoalBeenMet = true; // Set flag to prevent repeated messages
                } else if (currentWeight > goalWeight) {
                    hasGoalBeenMet = false; // Reset flag if moved away from goal
                }

            } else {
                goalText.setText("Goal: Not Set");
                progressBar.setProgress(0);
                hasGoalBeenMet = false; // Reset flag if no goal is set
            }

            // Update graph
            graphView.removeAllSeries();
            DataPoint[] dataPoints = new DataPoint[userWeights.size()];
            for (int i = 0; i < userWeights.size(); i++) {
                dataPoints[i] = new DataPoint(i, userWeights.get(i).getWeight());
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
            // Ensure data points are drawn so a single point is visible
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(10f);
            graphView.addSeries(series);
            customizeGraph(userWeights);
        } else {
            currentWeightText.setText("No Weight Logged");
            goalText.setText(String.format("Goal: %.1f lbs", goalWeight > 0 ? goalWeight : 0.0f));
            progressBar.setProgress(0);
            graphView.removeAllSeries();
            hasGoalBeenMet = false; // Reset flag if no weights are logged
        }
    }

    /**
     * Customizes the appearance of the graph.
     * @param userWeights The list of weight models to determine graph bounds.
     */
    private void customizeGraph(List<WeightModel> userWeights) {
        // Set the bounds of the graph's viewport
        graphView.getViewport().setXAxisBoundsManual(true);

        if (userWeights.size() > 1) {
            graphView.getViewport().setMinX(0);
            graphView.getViewport().setMaxX(userWeights.size() - 1);
        } else if (userWeights.size() == 1) {
            // For a single point, center it and give a small range for visibility
            graphView.getViewport().setMinX(-0.5);
            graphView.getViewport().setMaxX(0.5);
        } else {
            // Default range if no data
            graphView.getViewport().setMinX(0);
            graphView.getViewport().setMaxX(1);
        }

        graphView.getViewport().setYAxisBoundsManual(true);
        // Dynamically set min/max Y based on actual data to make the graph scale appropriately
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        if (!userWeights.isEmpty()) {
            for (WeightModel weight : userWeights) {
                if (weight.getWeight() < minY) minY = weight.getWeight();
                if (weight.getWeight() > maxY) maxY = weight.getWeight();
            }
            // Add some padding to min/max Y for better visualization
            minY -= 5; 
            maxY += 5;
            graphView.getViewport().setMinY(minY);
            graphView.getViewport().setMaxY(maxY);
        } else {
            // Default range if no data
            graphView.getViewport().setMinY(100);
            graphView.getViewport().setMaxY(200);
        }

        // Hide the labels on the graph's axes
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
    }

    /**
     * Checks if SMS permission is granted.
     * @return True if permission is granted, false otherwise.
     */
    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Sends an SMS notification when the goal weight is reached.
     * @param currentWeight The user's current weight.
     * @param goalWeight The user's goal weight.
     */
    private void sendGoalReachedSms(float currentWeight, float goalWeight) {
        if (checkSmsPermission()) {
            // In a real app, you might get the phone number from user settings
            String phoneNumber = "5551234567"; // Placeholder for testing on emulator
            String message = String.format("Weight Tracker: Congratulations! You've reached your goal weight of %.1f lbs with a current weight of %.1f lbs!", goalWeight, currentWeight);

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, "Goal Reached SMS sent to " + phoneNumber, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send Goal Reached SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "SMS permission not granted. Cannot send goal notification.", Toast.LENGTH_LONG).show();
        }
    }
}