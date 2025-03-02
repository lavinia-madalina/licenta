package com.example.licenta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<CalendarSlot> calendarList;

    public CalendarAdapter(List<CalendarSlot> calendarList) {
        this.calendarList = calendarList;
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_slot_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        CalendarSlot slot = calendarList.get(position);

        // Make sure the views are not null
        if (holder.textViewDay != null && holder.textViewHour != null) {
            holder.textViewDay.setText(slot.getDay());
            holder.textViewHour.setText(slot.getHour());
        }
    }

    @Override
    public int getItemCount() {
        return calendarList.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDay;
        TextView textViewHour;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            textViewDay = itemView.findViewById(R.id.textViewDay);  // Ensure these are correct IDs
            textViewHour = itemView.findViewById(R.id.textViewTime);  // Ensure these are correct IDs
        }
    }
}
