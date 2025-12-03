package com.example.weighttracker;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddWeightActivity extends AppCompatActivity {

    private EditText etWeightInput;
    private Button btnSaveWeight;

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

        // Check if there is a weight value passed to the activity
        if (getIntent().hasExtra("weight")) {
            String weight = getIntent().getStringExtra("weight");
            etWeightInput.setText(weight);
        }

        btnSaveWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightStr = etWeightInput.getText().toString();
                if (!weightStr.isEmpty()) {
                    // For Project Two, we just simulate saving and go back
                    Toast.makeText(AddWeightActivity.this, "Weight Saved: " + weightStr, Toast.LENGTH_SHORT).show();
                    finish(); // Closes activity and returns to GridActivity
                } else {
                    Toast.makeText(AddWeightActivity.this, "Please enter a value", Toast.LENGTH_SHORT).show();
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