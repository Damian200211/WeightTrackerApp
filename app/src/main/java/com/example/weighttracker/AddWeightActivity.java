package com.example.weighttracker;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity for adding new weight entries or editing existing ones.
 * It interacts with the {@link DatabaseHelper} to persist weight data.
 */
public class AddWeightActivity extends AppCompatActivity {

    private EditText etWeightInput;
    private Button btnSaveWeight;
    private DatabaseHelper databaseHelper;
    private int currentUserId; // Stores the ID of the currently logged-in user
    private int weightEntryId = -1; // -1 if adding new, otherwise the ID of the entry being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        // Set up the toolbar with a back button
        Toolbar toolbar = findViewById(R.id.toolbar_add_weight);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize UI elements
        etWeightInput = findViewById(R.id.etWeightInput);
        btnSaveWeight = findViewById(R.id.btnSaveWeight);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve the USER_ID passed from the calling activity (e.g., GridActivity or WeightHistoryActivity)
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if user ID is missing
            return;
        }

        // Check if the activity was launched to edit an existing weight entry
        if (getIntent().hasExtra("weightId") && getIntent().hasExtra("weight")) {
            weightEntryId = getIntent().getIntExtra("weightId", -1);
            String weight = getIntent().getStringExtra("weight");
            etWeightInput.setText(weight);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Weight"); // Change toolbar title for edit mode
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Weight"); // Default title for add mode
            }
        }

        // Set OnClickListener for the Save Weight button
        btnSaveWeight.setOnClickListener(v -> {
            String weightStr = etWeightInput.getText().toString();
            if (!weightStr.isEmpty()) {
                float weightValue = Float.parseFloat(weightStr);
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                boolean success;
                if (weightEntryId != -1) {
                    // Update existing weight entry in the database
                    success = databaseHelper.updateWeight(weightEntryId, currentDate, weightValue) > 0;
                    if (success) {
                        Toast.makeText(AddWeightActivity.this, "Weight Updated: " + weightStr, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddWeightActivity.this, "Failed to update weight.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Add new weight entry to the database
                    success = databaseHelper.addWeight(currentDate, weightValue, currentUserId);
                    if (success) {
                        Toast.makeText(AddWeightActivity.this, "Weight Saved: " + weightStr, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddWeightActivity.this, "Failed to save weight.", Toast.LENGTH_SHORT).show();
                    }
                }
                finish(); // Close activity and return to the previous screen
            } else {
                Toast.makeText(AddWeightActivity.this, "Please enter a weight value", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button click in the toolbar
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the current activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}