package com.example.licenta;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private List<CalendarSlot> calendarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        recyclerView = findViewById(R.id.recyclerViewCalendar);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        calendarList = new ArrayList<>();
        calendarAdapter = new CalendarAdapter(calendarList);
        recyclerView.setAdapter(calendarAdapter);

        // Initializează zilele săptămânii și orele
        initializeCalendar();
    }

    private void initializeCalendar() {
        String[] daysOfWeek = {"luni", "marți", "miercuri", "joi", "vineri", "sâmbătă", "duminică"};
        String[] hours = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00"};

        for (String day : daysOfWeek) {
            for (String hour : hours) {
                calendarList.add(new CalendarSlot(day, hour, null));  // 'null' înseamnă că nu există materie
            }
        }
        calendarAdapter.notifyDataSetChanged();
    }
}
