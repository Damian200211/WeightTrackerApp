package com.example.weighttracker;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class WeightTrackerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(theme);
    }
}