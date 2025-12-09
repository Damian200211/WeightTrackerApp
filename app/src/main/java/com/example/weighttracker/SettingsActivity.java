package com.example.weighttracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup themeRadioGroup;
    private RadioButton lightModeRadioButton;
    private RadioButton darkModeRadioButton;
    private Button notificationSettingsButton;
    private EditText goalWeightEditText;
    private Button saveGoalWeightButton;
    private DatabaseHelper databaseHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        themeRadioGroup = findViewById(R.id.theme_radio_group);
        lightModeRadioButton = findViewById(R.id.light_mode_radio_button);
        darkModeRadioButton = findViewById(R.id.dark_mode_radio_button);
        notificationSettingsButton = findViewById(R.id.notification_settings_button);
        goalWeightEditText = findViewById(R.id.goal_weight_edit_text);
        saveGoalWeightButton = findViewById(R.id.save_goal_weight_button);

        databaseHelper = new DatabaseHelper(this);

        // Get the USER_ID passed from GridActivity
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // --- Theme Selection Logic ---
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);
        if (theme == AppCompatDelegate.MODE_NIGHT_NO) {
            lightModeRadioButton.setChecked(true);
        } else {
            darkModeRadioButton.setChecked(true);
        }

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = getSharedPreferences("ThemePrefs", MODE_PRIVATE).edit();
            if (checkedId == R.id.light_mode_radio_button) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.dark_mode_radio_button) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_YES);
            }
            editor.apply();
        });

        // --- Goal Weight Logic ---
        loadGoalWeight();

        saveGoalWeightButton.setOnClickListener(v -> {
            String goalWeightStr = goalWeightEditText.getText().toString();
            if (!goalWeightStr.isEmpty()) {
                float goalWeight = Float.parseFloat(goalWeightStr);
                if (databaseHelper.updateGoalWeight(currentUserId, goalWeight) > 0) {
                    Toast.makeText(SettingsActivity.this, "Goal weight saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Failed to save goal weight.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SettingsActivity.this, "Please enter a goal weight.", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Notification Settings Button ---
        notificationSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, SmsPermissionActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Loads the current goal weight from the database and displays it.
     */
    private void loadGoalWeight() {
        float goal = databaseHelper.getGoalWeight(currentUserId);
        if (goal > 0) {
            goalWeightEditText.setText(String.valueOf(goal));
        }
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