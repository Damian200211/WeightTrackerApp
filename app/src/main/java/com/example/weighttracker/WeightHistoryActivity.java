package com.example.weighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * Activity to display the full history of weight entries.
 */
public class WeightHistoryActivity extends AppCompatActivity implements WeightAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private WeightAdapter adapter;
    private ArrayList<WeightModel> weightList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_history);

        // Set up the toolbar with a back button
        Toolbar toolbar = findViewById(R.id.toolbar_weight_history);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.weight_history_recycler_view);

        // --- Dummy Data ---
        // This section populates the list with dummy data for demonstration purposes.
        weightList = new ArrayList<>();
        weightList.add(new WeightModel(1, "2024-01-01", 180.5f));
        weightList.add(new WeightModel(2, "2024-01-02", 179.8f));
        weightList.add(new WeightModel(3, "2024-01-03", 179.0f));
        weightList.add(new WeightModel(4, "2024-01-04", 155.0f));
        weightList.add(new WeightModel(5, "2024-01-05", 154.0f));
        weightList.add(new WeightModel(6, "2024-01-06", 154.5f));
        weightList.add(new WeightModel(7, "2024-01-07", 153.0f));
        weightList.add(new WeightModel(8, "2024-01-08", 152.4f));
        weightList.add(new WeightModel(9, "2024-01-09", 152.0f));
        weightList.add(new WeightModel(10, "2024-01-10", 151.0f));
        weightList.add(new WeightModel(11, "2024-01-11", 150.0f));

        // Set up the adapter and RecyclerView
        adapter = new WeightAdapter(weightList);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
        // Launch the AddWeightActivity with the selected weight entry
        WeightModel item = weightList.get(position);
        Intent intent = new Intent(WeightHistoryActivity.this, AddWeightActivity.class);
        intent.putExtra("weight", String.valueOf(item.getWeight()));
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        // Remove the item from the list and notify the adapter
        weightList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}