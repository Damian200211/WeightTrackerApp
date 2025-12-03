package com.example.weighttracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup themeRadioGroup;
    private RadioButton lightModeRadioButton;
    private RadioButton darkModeRadioButton;
    private Button notificationSettingsButton;

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

        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);
        if (theme == AppCompatDelegate.MODE_NIGHT_NO) {
            lightModeRadioButton.setChecked(true);
        } else {
            darkModeRadioButton.setChecked(true);
        }

        themeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = getSharedPreferences("ThemePrefs", MODE_PRIVATE).edit();
                if (checkedId == R.id.light_mode_radio_button) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);
                } else if (checkedId == R.id.dark_mode_radio_button) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_YES);
                }
                editor.apply();
            }
        });

        notificationSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SmsPermissionActivity.class);
                startActivity(intent);
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