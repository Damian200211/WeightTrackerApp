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

public class AddWeightActivity extends AppCompatActivity {

    private EditText etWeightInput;
    private Button btnSaveWeight;
    private DatabaseHelper databaseHelper;
    private int currentUserId; // To store the ID of the logged-in user
    private int weightEntryId = -1; // -1 if adding new, otherwise ID of entry being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        Toolbar toolbar = findViewById(R.id.toolbar_add_weight);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etWeightInput = findViewById(R.id.etWeightInput);
        btnSaveWeight = findViewById(R.id.btnSaveWeight);

        databaseHelper = new DatabaseHelper(this);

        // Get the USER_ID passed from GridActivity
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if there is a weight value passed to the activity (for editing)
        if (getIntent().hasExtra("weightId") && getIntent().hasExtra("weight")) {
            weightEntryId = getIntent().getIntExtra("weightId", -1);
            String weight = getIntent().getStringExtra("weight");
            etWeightInput.setText(weight);
            getSupportActionBar().setTitle("Edit Weight");
        }

        btnSaveWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightStr = etWeightInput.getText().toString();
                if (!weightStr.isEmpty()) {
                    float weightValue = Float.parseFloat(weightStr);
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    boolean success;
                    if (weightEntryId != -1) {
                        // Update existing weight entry
                        success = databaseHelper.updateWeight(weightEntryId, currentDate, weightValue) > 0;
                        if (success) {
                            Toast.makeText(AddWeightActivity.this, "Weight Updated: " + weightStr, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddWeightActivity.this, "Failed to update weight.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Add new weight entry
                        success = databaseHelper.addWeight(currentDate, weightValue, currentUserId);
                        if (success) {
                            Toast.makeText(AddWeightActivity.this, "Weight Saved: " + weightStr, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddWeightActivity.this, "Failed to save weight.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish(); // Closes activity and returns to previous activity
                } else {
                    Toast.makeText(AddWeightActivity.this, "Please enter a weight value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}