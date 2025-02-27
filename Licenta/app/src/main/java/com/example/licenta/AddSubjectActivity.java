package com.example.licenta;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddSubjectActivity extends AppCompatActivity {

    private EditText subjectEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        subjectEditText = findViewById(R.id.subjectEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            // Obține textul introdus de utilizator
            String subject = subjectEditText.getText().toString();

            // Obține ziua și ora din Intent
            String day = getIntent().getStringExtra("day");
            String time = getIntent().getStringExtra("time");

            // Verifică dacă textul nu este gol
            if (!subject.isEmpty()) {
                // Salvează subiectul într-o bază de date (Firestore sau locală)
                // Poți salva acest subiect într-o bază de date sau o altă metodă de stocare

                // Poți utiliza Firestore pentru a salva subiectul
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("subject", subject);
                subjectData.put("day", day);
                subjectData.put("time", time);

                db.collection("schedule")
                        .add(subjectData)
                        .addOnSuccessListener(documentReference -> {
                            // După salvare, se poate întoarce la activitatea anterioară
                            Toast.makeText(AddSubjectActivity.this, "Materia a fost salvată!", Toast.LENGTH_SHORT).show();
                            finish(); // Închide activitatea
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddSubjectActivity.this, "Eroare la salvarea materiei!", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Afișează un mesaj de eroare dacă subiectul nu a fost introdus
                Toast.makeText(AddSubjectActivity.this, "Te rog să introduci o materie!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
