package com.example.licenta;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.GradesViewHolder> {
    private List<GradeSubject> subjects;
    private OnItemClickListener listener;

    private OnItemLongClickListener longClickListener;
    public interface OnItemClickListener {
        void onItemClick(GradeSubject subject);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(GradeSubject subject);
    }

    public GradesAdapter(List<GradeSubject> subjects) {
        this.subjects = subjects;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener)
    {
        this.longClickListener=longClickListener;
    }

    @NonNull
    @Override
    public GradesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_item, parent, false);
        return new GradesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradesViewHolder holder, int position) {
        GradeSubject subject = subjects.get(position);
        holder.subjectName.setText(subject.getName());
        holder.subjectAverage.setText(String.format("Average: %.2f", subject.getAverage()));

        // Set background color based on average
        int color;
        double average = subject.getAverage();
        Log.d("GradesAdapter", "Subject: " + subject.getName() + ", Average: " + average);
        if (average >= 9.0) {
            color = Color.rgb(136, 160, 247); // Light blue for excellent
        } else if (average >= 8.0) {
            color = Color.rgb(220, 255, 220); // Lighter green for good
        } else if (average >= 7.0) {
            color = Color.rgb(255, 255, 200); // Light yellow for ok
        } else if (average >= 6.0) {
            color = Color.rgb(255, 230, 200); // Light orange for passing
        } else {
            color = Color.rgb(255, 200, 200); // Light red for failing
        }

        holder.cardView.setCardBackgroundColor(color);
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(subject);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public class GradesViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName;
        TextView subjectAverage;
        CardView cardView;

        public GradesViewHolder(@NonNull View itemView) {
            super(itemView);
            // Update these to match the actual IDs in your subject_item.xml layout
            subjectName = itemView.findViewById(R.id.subjectName);
            subjectAverage = itemView.findViewById(R.id.subjectAverage);
            cardView = itemView.findViewById(R.id.cardView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(subjects.get(position));
                }
            });
        }
    }
}