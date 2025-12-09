package com.example.weighttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name and Version
    public static final String DATABASE_NAME = "WeightTracker.db";
    public static final int DATABASE_VERSION = 2; // Increment database version

    // User Table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_GOAL_WEIGHT = "goal_weight"; // New column for goal weight

    // Weight Table
    public static final String TABLE_WEIGHTS = "weights";
    public static final String COL_WEIGHT_ID = "id";
    public static final String COL_WEIGHT_DATE = "date";
    public static final String COL_WEIGHT_VALUE = "weight";
    public static final String COL_WEIGHT_USER_ID = "user_id"; // Foreign key to link weights to users

    // SQL to create the Users table
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
            COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL_USERNAME + " TEXT UNIQUE," +
            COL_PASSWORD + " TEXT," +
            COL_GOAL_WEIGHT + " REAL DEFAULT 0.0" + // Add new column with a default value
            ")";

    // SQL to create the Weights table
    private static final String CREATE_WEIGHTS_TABLE = "CREATE TABLE " + TABLE_WEIGHTS + " (" +
            COL_WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL_WEIGHT_DATE + " TEXT," +
            COL_WEIGHT_VALUE + " REAL," +
            COL_WEIGHT_USER_ID + " INTEGER," +
            "FOREIGN KEY(" + COL_WEIGHT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE" +
            ")";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_WEIGHTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For development, simply drop and recreate tables. In production, use ALTER TABLE.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /**
     * Adds a new user to the database.
     * @param username The username for the new user.
     * @param password The password for the new user.
     * @return True if the user was added successfully, false otherwise (e.g., username already exists).
     */
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PASSWORD, password);
        // Default goal weight when a user is added
        contentValues.put(COL_GOAL_WEIGHT, 0.0f);

        long result = db.insert(TABLE_USERS, null, contentValues);
        db.close();
        return result != -1;
    }

    /**
     * Authenticates a user.
     * @param username The username to check.
     * @param password The password to check.
     * @return The user ID if credentials are correct, -1 otherwise.
     */
    public int checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID}, COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

    /**
     * Adds a new weight entry to the database for a specific user.
     * @param date The date of the weight entry.
     * @param weight The weight value.
     * @param userId The ID of the user who owns this weight entry.
     * @return True if the weight was added successfully, false otherwise.
     */
    public boolean addWeight(String date, float weight, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_WEIGHT_DATE, date);
        contentValues.put(COL_WEIGHT_VALUE, weight);
        contentValues.put(COL_WEIGHT_USER_ID, userId);

        long result = db.insert(TABLE_WEIGHTS, null, contentValues);
        db.close();
        return result != -1;
    }

    /**
     * Retrieves all weight entries for a specific user from the database.
     * @param userId The ID of the user.
     * @return A list of WeightModel objects for the specified user.
     */
    public List<WeightModel> getAllWeights(int userId) {
        List<WeightModel> weightList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_WEIGHT_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_WEIGHTS, null, selection, selectionArgs, null, null, COL_WEIGHT_DATE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_WEIGHT_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_WEIGHT_DATE));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_WEIGHT_VALUE));
                weightList.add(new WeightModel(id, date, weight));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return weightList;
    }

    /**
     * Retrieves the latest weight entry for a specific user from the database.
     * @param userId The ID of the user.
     * @return The latest WeightModel object, or null if no entries exist.
     */
    public WeightModel getLatestWeight(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        WeightModel latestWeight = null;
        String selection = COL_WEIGHT_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        // Order by date descending and limit to 1 to get the latest
        Cursor cursor = db.query(TABLE_WEIGHTS, null, selection, selectionArgs, null, null, COL_WEIGHT_DATE + " DESC", "1");

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_WEIGHT_ID));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_WEIGHT_DATE));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_WEIGHT_VALUE));
            latestWeight = new WeightModel(id, date, weight);
        }
        cursor.close();
        db.close();
        return latestWeight;
    }

    /**
     * Updates an existing weight entry in the database.
     * @param weightId The ID of the weight entry to update.
     * @param newDate The new date for the entry.
     * @param newWeight The new weight value for the entry.
     * @return The number of rows affected (1 if successful, 0 otherwise).
     */
    public int updateWeight(int weightId, String newDate, float newWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_WEIGHT_DATE, newDate);
        contentValues.put(COL_WEIGHT_VALUE, newWeight);

        int result = db.update(TABLE_WEIGHTS, contentValues, COL_WEIGHT_ID + " = ?", new String[]{String.valueOf(weightId)});
        db.close();
        return result;
    }

    /**
     * Deletes a weight entry from the database.
     * @param weightId The ID of the weight entry to delete.
     * @return The number of rows affected (1 if successful, 0 otherwise).
     */
    public int deleteWeight(int weightId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_WEIGHTS, COL_WEIGHT_ID + " = ?", new String[]{String.valueOf(weightId)});
        db.close();
        return result;
    }

    /**
     * Retrieves the goal weight for a specific user.
     * @param userId The ID of the user.
     * @return The user's goal weight, or 0.0f if not set/found.
     */
    public float getGoalWeight(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_GOAL_WEIGHT}, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        float goalWeight = 0.0f;
        if (cursor.moveToFirst()) {
            goalWeight = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_GOAL_WEIGHT));
        }
        cursor.close();
        db.close();
        return goalWeight;
    }

    /**
     * Updates the goal weight for a specific user.
     * @param userId The ID of the user.
     * @param goalWeight The new goal weight.
     * @return The number of rows affected (1 if successful, 0 otherwise).
     */
    public int updateGoalWeight(int userId, float goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GOAL_WEIGHT, goalWeight);

        int result = db.update(TABLE_USERS, contentValues, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result;
    }
}
