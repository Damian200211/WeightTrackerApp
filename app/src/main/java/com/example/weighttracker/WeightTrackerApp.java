package com.example.weighttracker;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Custom Application class to set the default theme (light/dark mode)
 * when the app starts, based on user preferences.
 */
public class WeightTrackerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Retrieve the saved theme preference and apply it across the app
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(theme);
    }
}