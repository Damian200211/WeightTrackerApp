package com.example.weighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast; // Import statement added
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display the full history of weight entries.
 */
public class WeightHistoryActivity extends AppCompatActivity implements WeightAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private WeightAdapter adapter;
    private ArrayList<WeightModel> weightList;
    private DatabaseHelper databaseHelper;
    private int currentUserId; // To store the ID of the logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_history);

        // Set up the toolbar with a back button
        Toolbar toolbar = findViewById(R.id.toolbar_weight_history);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize the RecyclerView and DatabaseHelper
        recyclerView = findViewById(R.id.weight_history_recycler_view);
        databaseHelper = new DatabaseHelper(this);

        // Get the USER_ID passed from GridActivity
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            // If no user ID is passed, something went wrong, redirect to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Set up the adapter and RecyclerView (data will be loaded in onResume)
        weightList = new ArrayList<>();
        adapter = new WeightAdapter(weightList);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load and display the latest weight data for the current user
        loadWeightData();
    }

    /**
     * Loads weight data for the current user from the database and updates the RecyclerView.
     */
    private void loadWeightData() {
        List<WeightModel> loadedWeights = databaseHelper.getAllWeights(currentUserId);
        weightList.clear();
        weightList.addAll(loadedWeights);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the back button click
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(int position) {
        // Launch the AddWeightActivity with the selected weight entry for editing
        WeightModel item = weightList.get(position);
        Intent intent = new Intent(WeightHistoryActivity.this, AddWeightActivity.class);
        intent.putExtra("USER_ID", currentUserId); // Pass user ID
        intent.putExtra("weightId", item.getId()); // Pass weight ID for editing
        intent.putExtra("weight", String.valueOf(item.getWeight())); // Pass current weight value
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        // Delete the item from the database and update the list
        WeightModel item = weightList.get(position);
        int result = databaseHelper.deleteWeight(item.getId());
        if (result > 0) {
            loadWeightData(); // Refresh data after deletion
            Toast.makeText(this, "Weight entry deleted.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete weight entry.", Toast.LENGTH_SHORT).show();
        }
    }
}