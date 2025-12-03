package com.example.weighttracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * Adapter to display the list of weight entries in a RecyclerView.
 */
public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.ViewHolder> {

    private final ArrayList<WeightModel> weightList;
    private OnItemClickListener listener;

    /**
     * Interface to handle item clicks.
     */
    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    /**
     * Sets the click listener for the adapter.
     * @param listener The listener to set.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Constructor for the adapter.
     * @param weightList The list of weight entries to display.
     */
    public WeightAdapter(ArrayList<WeightModel> weightList) {
        this.weightList = weightList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the new layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_history_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeightModel model = weightList.get(position);
        // Set the weight and date for the current item
        holder.weightTextView.setText(String.valueOf(model.getWeight()));
        holder.dateTextView.setText(model.getDate());
    }

    @Override
    public int getItemCount() {
        return weightList.size();
    }

    /**
     * ViewHolder to hold the views for each item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView weightTextView, dateTextView;
        Button editButton, deleteButton;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            // Initialize the views
            weightTextView = itemView.findViewById(R.id.weight_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);

            // Set the click listener for the edit button
            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                    }
                }
            });

            // Set the click listener for the delete button
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}