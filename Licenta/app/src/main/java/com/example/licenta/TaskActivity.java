package com.example.licenta;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Calendar;

public class TaskActivity extends AppCompatActivity {

    private EditText editTextTaskName, editTextTaskDescription, editTextTaskRoom;
    private TextView textViewDate;
    private Button buttonSelectDate, buttonSubmitTask;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Calendar selectedDate = Calendar.getInstance();  // Default to current date and time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);  // Rename the layout file here

        editTextTaskName = findViewById(R.id.editTextTaskName);
        editTextTaskDescription = findViewById(R.id.editTextTaskDescription);
        editTextTaskRoom = findViewById(R.id.editTextTaskRoom );
        textViewDate = findViewById(R.id.textViewDate);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSubmitTask = findViewById(R.id.buttonSubmitTask);

        // Date Picker logic
        buttonSelectDate.setOnClickListener(v -> {
            // Open Date Picker (you can use a DatePickerDialog)
            new DatePickerDialog(TaskActivity.this, (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);  // Set the selected date
                textViewDate.setText("Selected Date: " + (month + 1) + "/" + dayOfMonth + "/" + year);
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Submit Task logic
        buttonSubmitTask.setOnClickListener(v -> {
            String taskName = editTextTaskName.getText().toString();
            String taskDescription = editTextTaskDescription.getText().toString();
            String taskRoom = editTextTaskRoom.getText().toString();

            if (taskName.isEmpty()) {
                Toast.makeText(TaskActivity.this, "Numele Task-ului nu poate fi lasat gol", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepare data to be added to Firestore
            TaskModel newTask = new TaskModel(taskName, taskDescription, new Timestamp(selectedDate.getTime()), taskRoom);

            // Add to Firestore
            db.collection("task")
                    .add(newTask)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(TaskActivity.this, "Task adaugat!", Toast.LENGTH_SHORT).show();
                        finish();  // Optionally, close the activity or reset fields
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(TaskActivity.this, "Eroare", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
