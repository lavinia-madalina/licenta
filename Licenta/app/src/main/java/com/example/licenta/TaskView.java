package com.example.licenta;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TaskView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button button;
    private TaskAdapter taskAdapter;
    private List<TaskModel> taskList;
    private FirebaseFirestore db;
    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtask);

        Button addButton = findViewById(R.id.button);
        recyclerView = findViewById(R.id.recycleViewTask);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        db = FirebaseFirestore.getInstance();

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskView.this, TaskActivity.class);
            startActivity(intent);
        });

        loadTasks();

        // Data curentă
        Date currentDate = new Date();

        db.collection("task")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            Timestamp timestamp = document.getTimestamp("date");
                            String room= document.getString("room");

                            if (timestamp != null) {
                                Date taskDate = timestamp.toDate();

                                // Adăugăm doar task-urile care nu au expirat
                                if (!taskDate.before(currentDate)) {
                                    taskList.add(new TaskModel(name, description, timestamp, room));
                                }
                            }
                        }
                        taskAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Eroare la încărcarea task-urilor", task.getException());
                    }
                });
    }
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadTasks(); // Refresh data
            handler.postDelayed(this, 5000); // Refresh every 5 seconds
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(refreshRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }
    private void loadTasks() {
        taskList.clear(); // Curăță lista pentru a evita duplicarea datelor
        Date currentDate = new Date();

        db.collection("task")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Eroare la ascultarea task-urilor", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        taskList.clear(); // Curăță lista la fiecare actualizare

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            Timestamp timestamp = document.getTimestamp("date");
                            String room = document.getString("room");

                            if (timestamp != null) {
                                Date taskDate = timestamp.toDate();
                                if (!taskDate.before(currentDate)) {
                                    taskList.add(new TaskModel(name, description, timestamp, room));
                                }
                            }
                        }
                        taskAdapter.notifyDataSetChanged();
                    }
                });
    }


}