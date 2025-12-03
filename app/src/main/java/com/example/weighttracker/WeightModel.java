package com.example.weighttracker;

public class WeightModel {
    private int id;
    private String date;
    private float weight;

    // Constructor
    public WeightModel(int id, String date, float weight) {
        this.id = id;
        this.date = date;
        this.weight = weight;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }
}