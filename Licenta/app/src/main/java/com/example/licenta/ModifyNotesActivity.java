package com.example.licenta;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifyNotesActivity extends AppCompatActivity {
    private TextView textSubjectName;
    private LinearLayout gradesContainer;
    private Button btnUpdateGrade;
    private Button btnAddGrade;
    private Button btnAddAnotherGrade;

    private String subjectId;
    private String subjectName;
    private List<GradeItem> grades;
    private FirebaseFirestore db;
    private static final int MAX_GRADES = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_notes);

        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");

        if (subjectId == null || subjectName == null) {
            Toast.makeText(this, "Error: Subject information missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        grades = new ArrayList<>();

        // Initialize views
        textSubjectName = findViewById(R.id.textSubjectName);
        gradesContainer = findViewById(R.id.gradesContainer);
        btnUpdateGrade = findViewById(R.id.btnUpdateGrade);
        btnAddGrade = findViewById(R.id.btnAddGrade);
        btnAddAnotherGrade = findViewById(R.id.btnAddAnotherGrade);

        textSubjectName.setText(subjectName);

        // Set button click listeners
        btnUpdateGrade.setOnClickListener(v -> updateGrades());
        btnAddGrade.setOnClickListener(v -> addNewGrade());
        btnAddAnotherGrade.setOnClickListener(v -> addAnotherGradeField());

        loadGrades();
    }

    private void loadGrades() {
        db.collection("grades").document(subjectId)
                .collection("grades")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    grades.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        Double value = document.getDouble("value");

                        if (value != null) {
                            grades.add(new GradeItem(id, value));
                        }
                    }

                    updateGradeViews();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading grades: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void updateGradeViews() {
        gradesContainer.removeAllViews();

        // Create input fields for existing grades
        for (int i = 0; i < Math.min(grades.size(), 2); i++) {
            GradeItem grade = grades.get(i);

            View gradeView = getLayoutInflater().inflate(R.layout.item_grade_input, gradesContainer, false);
            TextView gradeLabel = gradeView.findViewById(R.id.textGradeLabel);
            EditText gradeInput = gradeView.findViewById(R.id.editGradeValue);
            Button btnEdit = gradeView.findViewById(R.id.btnEdit);
            Button btnDelete = gradeView.findViewById(R.id.btnDelete);

            gradeLabel.setText("Grade " + (i + 1));
            gradeInput.setText(String.valueOf(grade.value));
            gradeInput.setTag(grade.id); // Store the grade ID in the tag

            // Set up edit functionality
            btnEdit.setOnClickListener(v -> {
                String newValue = gradeInput.getText().toString().trim();
                if (!newValue.isEmpty()) {
                    try {
                        double value = Double.parseDouble(newValue);
                        updateGradeInFirestore(grade.id, value);
                    } catch (NumberFormatException e) {
                        gradeInput.setError("Please enter a valid number");
                    }
                }
            });

            // Set up delete functionality
            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Grade")
                        .setMessage("Are you sure you want to delete this grade?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteGrade(grade.id))
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            gradesContainer.addView(gradeView);
        }

        // Add empty grade input fields if less than 2 grades
        for (int i = grades.size(); i < 2; i++) {
            View gradeView = getLayoutInflater().inflate(R.layout.item_grade_input, gradesContainer, false);
            TextView gradeLabel = gradeView.findViewById(R.id.textGradeLabel);
            EditText gradeInput = gradeView.findViewById(R.id.editGradeValue);
            Button btnEdit = gradeView.findViewById(R.id.btnEdit);
            Button btnDelete = gradeView.findViewById(R.id.btnDelete);

            gradeLabel.setText("Grade " + (i + 1));
            gradeInput.setHint("Enter grade value");

            // Hide edit/delete buttons for new grades
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);

            gradesContainer.addView(gradeView);
        }

        // Update button visibility based on grade count
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        // Show/hide buttons based on the number of grades
        if (grades.size() >= MAX_GRADES) {
            btnAddGrade.setVisibility(View.GONE);
            btnAddAnotherGrade.setVisibility(View.GONE);
        } else {
            btnAddGrade.setVisibility(View.VISIBLE);

            if (grades.size() < 2) {
                btnAddAnotherGrade.setVisibility(View.GONE);
            } else {
                btnAddAnotherGrade.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateGrades() {
        boolean anyUpdates = false;

        for (int i = 0; i < gradesContainer.getChildCount(); i++) {
            View gradeView = gradesContainer.getChildAt(i);
            EditText gradeInput = gradeView.findViewById(R.id.editGradeValue);
            String gradeId = (String) gradeInput.getTag();

            String valueStr = gradeInput.getText().toString().trim();

            if (!valueStr.isEmpty() && gradeId != null) {
                try {
                    double value = Double.parseDouble(valueStr);

                    // Find the grade in the list
                    for (GradeItem grade : grades) {
                        if (grade.id.equals(gradeId) && grade.value != value) {
                            updateGradeInFirestore(gradeId, value);
                            anyUpdates = true;
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                    gradeInput.setError("Please enter a valid number");
                }
            }
        }

        if (!anyUpdates) {
            Toast.makeText(this, "No changes to update", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGradeInFirestore(String gradeId, double value) {
        db.collection("grades").document(subjectId)
                .collection("grades").document(gradeId)
                .update("value", value)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ModifyNotesActivity.this, "Grade updated successfully",
                            Toast.LENGTH_SHORT).show();

                    // Update the grade in the local list
                    for (GradeItem grade : grades) {
                        if (grade.id.equals(gradeId)) {
                            grade.value = value;
                            break;
                        }
                    }

                    // Recalculate and update average
                    updateAverage();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ModifyNotesActivity.this, "Failed to update grade: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteGrade(String gradeId) {
        db.collection("grades").document(subjectId)
                .collection("grades").document(gradeId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ModifyNotesActivity.this, "Grade deleted successfully",
                            Toast.LENGTH_SHORT).show();

                    // Remove the grade from the local list
                    grades.removeIf(grade -> grade.id.equals(gradeId));

                    // Update UI
                    updateGradeViews();

                    // Recalculate and update average
                    updateAverage();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ModifyNotesActivity.this, "Failed to delete grade: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void addNewGrade() {
        if (grades.size() >= MAX_GRADES) {
            Toast.makeText(this, "Maximum number of grades reached", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect values from all input fields
        List<Double> values = new ArrayList<>();

        for (int i = 0; i < gradesContainer.getChildCount(); i++) {
            View gradeView = gradesContainer.getChildAt(i);
            EditText gradeInput = gradeView.findViewById(R.id.editGradeValue);
            String valueStr = gradeInput.getText().toString().trim();

            if (!valueStr.isEmpty()) {
                try {
                    values.add(Double.parseDouble(valueStr));
                } catch (NumberFormatException e) {
                    gradeInput.setError("Please enter a valid number");
                    return;
                }
            }
        }

        // Add each new grade to Firestore
        boolean addedAny = false;

        for (Double value : values) {
            // Skip if we already have this grade in the database
            boolean exists = false;
            for (GradeItem grade : grades) {
                if (Math.abs(grade.value - value) < 0.001) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                addGradeToFirestore(value);
                addedAny = true;
            }
        }

        if (!addedAny) {
            Toast.makeText(this, "No new grades to add", Toast.LENGTH_SHORT).show();
        }
    }

    private void addGradeToFirestore(double value) {
        Map<String, Object> gradeData = new HashMap<>();
        gradeData.put("value", value);

        db.collection("grades").document(subjectId)
                .collection("grades")
                .add(gradeData)
                .addOnSuccessListener(documentReference -> {
                    String id = documentReference.getId();
                    grades.add(new GradeItem(id, value));

                    updateGradeViews();
                    updateAverage();

                    Toast.makeText(ModifyNotesActivity.this, "Grade added successfully",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ModifyNotesActivity.this, "Failed to add grade: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void addAnotherGradeField() {
        if (grades.size() >= 2 && grades.size() < MAX_GRADES) {
            View gradeView = getLayoutInflater().inflate(R.layout.item_grade_input, gradesContainer, false);
            TextView gradeLabel = gradeView.findViewById(R.id.textGradeLabel);
            EditText gradeInput = gradeView.findViewById(R.id.editGradeValue);
            Button btnEdit = gradeView.findViewById(R.id.btnEdit);
            Button btnDelete = gradeView.findViewById(R.id.btnDelete);

            gradeLabel.setText("Grade " + (grades.size() + 1));
            gradeInput.setHint("Enter grade value");

            // Hide edit/delete buttons for new grades
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);

            gradesContainer.addView(gradeView);
        }
    }

    private void updateAverage() {
        if (grades.isEmpty()) {
            // If no grades, set average to 0
            Map<String, Object> data = new HashMap<>();
            data.put("average", 0.0);

            db.collection("grades").document(subjectId)
                    .update(data);
            return;
        }

        // Calculate average
        double sum = 0;
        for (GradeItem grade : grades) {
            sum += grade.value;
        }
        double average = sum / grades.size();

        // Update subject average
        Map<String, Object> data = new HashMap<>();
        data.put("average", average);

        db.collection("grades").document(subjectId)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    // Average updated successfully
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ModifyNotesActivity.this, "Failed to update average: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // Model class for a grade
    static class GradeItem {
        String id;
        double value;

        GradeItem(String id, double value) {
            this.id = id;
            this.value = value;
        }
    }
}