package com.example.licenta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<CalendarSlot> calendarSlots;

    public CalendarAdapter(List<CalendarSlot> calendarSlots) {
        this.calendarSlots = calendarSlots;
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_slot_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        CalendarSlot slot = calendarSlots.get(position);
        holder.dayTextView.setText(slot.getDay());
        holder.hourTextView.setText(slot.getHour());
        holder.subjectTextView.setText(slot.getSubject() != null ? slot.getSubject() : "Nicio materie");
    }

    @Override
    public int getItemCount() {
        return calendarSlots.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {

        TextView dayTextView;
        TextView hourTextView;
        TextView subjectTextView;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            hourTextView = itemView.findViewById(R.id.hourTextView);
            subjectTextView = itemView.findViewById(R.id.subjectTextView);
        }
    }
}
