package com.example.weighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

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
            startActivity(intent);
        });

        // Set up the bottom navigation to switch between screens
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_graph) {
                Intent intent = new Intent(GridActivity.this, WeightHistoryActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.action_settings) {
                Intent intent = new Intent(GridActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // --- Dummy Data ---
        // This section populates the UI with dummy data for demonstration purposes.
        // In a real app, this data would be loaded from a database.
        currentWeightText.setText("152.4 lbs");
        goalText.setText("Goal: 145 lbs");
        progressBar.setProgress(75); // Example progress

        // Populate the graph with dummy data
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 155),
                new DataPoint(1, 154),
                new DataPoint(2, 154.5),
                new DataPoint(3, 153),
                new DataPoint(4, 152.4),
                new DataPoint(5, 152),
                new DataPoint(6, 151),
                new DataPoint(7, 150)
        });
        graphView.addSeries(series);

        // Customize the graph's appearance to match the sketch
        customizeGraph();
    }

    /**
     * Customizes the appearance of the graph.
     */
    private void customizeGraph() {
        // Set the bounds of the graph's viewport
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(7);

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(145);
        graphView.getViewport().setMaxY(160);

        // Hide the labels on the graph's axes
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
    }
}