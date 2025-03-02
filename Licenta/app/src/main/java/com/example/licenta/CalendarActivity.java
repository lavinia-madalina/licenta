package com.example.licenta;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDays;
    private RecyclerView recyclerViewHours;
    private RecyclerView recyclerViewCalendar;
    private CalendarAdapter calendarAdapter;
    private HourAdapter hourAdapter;
    private DayAdapter dayAdapter;
    private List<CalendarSlot> calendarList;
    private List<String> hourList;
    private List<String> dayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        recyclerViewDays = findViewById(R.id.recyclerViewDays);
        recyclerViewHours = findViewById(R.id.recyclerViewHours);
        recyclerViewCalendar = findViewById(R.id.recyclerViewCalendar);

        recyclerViewDays.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerViewHours.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCalendar.setLayoutManager(new GridLayoutManager(this, 7));

        dayList = new ArrayList<>();
        hourList = new ArrayList<>();
        calendarList = new ArrayList<>();

        dayAdapter = new DayAdapter(dayList);
        hourAdapter = new HourAdapter(hourList);
        calendarAdapter = new CalendarAdapter(calendarList);

        recyclerViewDays.setAdapter(dayAdapter);
        recyclerViewHours.setAdapter(hourAdapter);
        recyclerViewCalendar.setAdapter(calendarAdapter);

        initializeDays();
        initializeHours();
        initializeCalendar();
    }

    private void initializeDays() {
        String[] daysOfWeek = {"Luni", "Marți", "Miercuri", "Joi", "Vineri", "Sâmbătă", "Duminică"};
        for (String day : daysOfWeek) {
            dayList.add(day);
        }
        dayAdapter.notifyDataSetChanged();
    }

    private void initializeHours() {
        String[] hours = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00"};
        for (String hour : hours) {
            hourList.add(hour);
        }
        hourAdapter.notifyDataSetChanged();
    }

    private void initializeCalendar() {
        String[] hours = hourList.toArray(new String[0]);
        String[] daysOfWeek = dayList.toArray(new String[0]);

        for (String hour : hours) {
            for (String day : daysOfWeek) {
                calendarList.add(new CalendarSlot(day, hour, null, null));
            }
        }
        calendarAdapter.notifyDataSetChanged();
    }
}
