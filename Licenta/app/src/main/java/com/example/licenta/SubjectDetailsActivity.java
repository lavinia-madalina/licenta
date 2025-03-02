package com.example.licenta;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SubjectDetailsActivity extends AppCompatActivity {
    private TextView textSubjectName;
    private RecyclerView recyclerViewGrades;
    private TextView textSubjectAverage;
    private GradesListAdapter adapter;
    private List<Double> grades;
    private FirebaseFirestore db;
    private String subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);

        textSubjectName = findViewById(R.id.textSubjectName);
        textSubjectAverage=findViewById(R.id.textSubjectAverage);
        recyclerViewGrades = findViewById(R.id.recyclerViewGrades);
        recyclerViewGrades.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        grades = new ArrayList<>();
        adapter = new GradesListAdapter(grades);
        recyclerViewGrades.setAdapter(adapter);

        subjectId = getIntent().getStringExtra("subjectId");
        String subjectName = getIntent().getStringExtra("subjectName");
        String subjectAverage=getIntent().getStringExtra("average");

        if (subjectName != null) {
            textSubjectName.setText(subjectName);
        }
        if (subjectAverage != null) {
            textSubjectAverage.setText(subjectAverage);
        }

        loadGrades();
    }

    private void loadGrades() {
        db.collection("grades").document(subjectId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the grades and average from the Firestore document
                        List<Double> retrievedGrades = (List<Double>) documentSnapshot.get("grades");
                        Double subjectAverage = documentSnapshot.getDouble("average");  // Retrieve the average

                        // If grades are retrieved, update the grades list and adapter
                        if (retrievedGrades != null) {
                            grades.clear();
                            grades.addAll(retrievedGrades);
                            adapter.notifyDataSetChanged();
                        }


                        if (subjectAverage != null) {
                            textSubjectAverage.setText("Media Notelor= " + String.format("%.2f", subjectAverage));
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(SubjectDetailsActivity.this, "Failed to load grades", Toast.LENGTH_SHORT).show());
    }




    // Inner Adapter Class
    private static class GradesListAdapter extends RecyclerView.Adapter<GradesListAdapter.GradeViewHolder> {
        private List<Double> grades;

        public GradesListAdapter(List<Double> grades) {
            this.grades = grades;
        }

        @NonNull
        @Override
        public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grade_item, parent, false);
            return new GradeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
            holder.gradeNumber.setText(String.valueOf(position + 1));
            holder.gradeValue.setText(String.valueOf(grades.get(position)));
        }

        @Override
        public int getItemCount() {
            return grades.size();
        }

        static class GradeViewHolder extends RecyclerView.ViewHolder {
            TextView gradeNumber, gradeValue;

            public GradeViewHolder(@NonNull View itemView) {
                super(itemView);
                gradeNumber = itemView.findViewById(R.id.gradeNumber);
                gradeValue = itemView.findViewById(R.id.gradeValue);
            }
        }
    }
}
