package com.example.licenta;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class NotesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GradesAdapter adapter;
    private List<GradeSubject> subjects;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        db = FirebaseFirestore.getInstance();
        subjects = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GradesAdapter(subjects);
        recyclerView.setAdapter(adapter);

        // Set up click listener for the adapter items
        adapter.setOnItemClickListener(subject -> {
            showAddGradeDialog(subject);
        });

        adapter.setOnItemLongClickListener(subject -> {
            Intent intent = new Intent(NotesActivity.this, SubjectDetailsActivity.class);
            intent.putExtra("subjectId", subject.getId());
            intent.putExtra("subjectName", subject.getName());
            startActivity(intent);
        });

        // Add a floating action button to add new subjects
        FloatingActionButton fab = findViewById(R.id.fabAddSubject);
        fab.setOnClickListener(v -> {
            // Open dialog to add a new subject
            showAddSubjectDialog();
        });

        loadSubjects();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadSubjects();
    }

    private void loadSubjects() {
        db.collection("grades")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    subjects.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String name = document.getString("name");
                        Double average = document.getDouble("average");

                        if (name != null) {
                            GradeSubject subject = new GradeSubject(id, name, average != null ? average : 0.0);
                            subjects.add(subject);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NotesActivity.this,
                            "Error loading subjects: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // Method to update a subject's grades and recalculate average
    private void updateSubjectGrade(String subjectId, double newGrade) {
        // First, get the current document to retrieve existing grades
        DocumentReference subjectRef = db.collection("grades").document(subjectId);

        subjectRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Get the existing grades array or create a new one if it doesn't exist
                List<Double> grades = (List<Double>) documentSnapshot.get("grades");
                if (grades == null) {
                    grades = new ArrayList<>();
                }

                // Add the new grade
                grades.add(newGrade);

                // Calculate the new average
                double sum = 0;
                for (Double grade : grades) {
                    sum += grade;
                }
                double newAverage = sum / grades.size();

                // Update the document with the new grades and average
                Map<String, Object> updates = new HashMap<>();
                updates.put("grades", grades);
                updates.put("average", newAverage);

                subjectRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            // Success, refresh the data
                            loadSubjects();
                            Toast.makeText(NotesActivity.this,
                                    "Grade added successfully",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Handle error
                            Toast.makeText(NotesActivity.this,
                                    "Failed to update grades: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(NotesActivity.this,
                    "Error retrieving subject: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    // Dialog to add a new grade to a subject
    private void showAddGradeDialog(GradeSubject subject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Grade for " + subject.getName());

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter grade (e.g., 85.5)");

        // Add some padding
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = getResources().getDimensionPixelSize(R.dimen.dialog_padding);
        if (padding == 0) padding = 20; // fallback if dimen resource not found
        layout.setPadding(padding, padding, padding, padding);
        layout.addView(input);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            try {
                String inputText = input.getText().toString().trim();
                if (inputText.isEmpty()) {
                    Toast.makeText(NotesActivity.this,
                            "Please enter a grade",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                double grade = Double.parseDouble(inputText);
                // Call the update method with the new grade
                updateSubjectGrade(subject.getId(), grade);
            } catch (NumberFormatException e) {
                Toast.makeText(NotesActivity.this,
                        "Please enter a valid number",
                        Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Dialog to add a new subject
    private void showAddSubjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Subject");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter subject name");

        // Add some padding
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = getResources().getDimensionPixelSize(R.dimen.dialog_padding);
        if (padding == 0) padding = 20; // fallback if dimen resource not found
        layout.setPadding(padding, padding, padding, padding);
        layout.addView(input);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            String subjectName = input.getText().toString().trim();
            if (!subjectName.isEmpty()) {
                addNewSubject(subjectName);
            } else {
                Toast.makeText(NotesActivity.this,
                        "Subject name cannot be empty",
                        Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Method to add a new subject to Firestore
    private void addNewSubject(String subjectName) {
        Map<String, Object> subject = new HashMap<>();
        subject.put("name", subjectName);
        subject.put("average", 0.0);
        subject.put("grades", new ArrayList<Double>());

        db.collection("grades")
                .add(subject)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(NotesActivity.this,
                            "Subject added successfully",
                            Toast.LENGTH_SHORT).show();
                    loadSubjects();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NotesActivity.this,
                            "Error adding subject: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}