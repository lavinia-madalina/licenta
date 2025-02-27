package com.example.licenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obține referința la TextView
        TextView textViewOrar = findViewById(R.id.textView2);

        // Setează locale pentru limba română
        Locale localeRo = new Locale("ro", "RO");

        // Obține data curentă
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", localeRo);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", localeRo);

        // Stocăm doar ziua din săptămână
        String currentDay = dayFormat.format(calendar.getTime());
        String dayOfWeek = dayFormat.format(calendar.getTime());
        String currentDate = dateFormat.format(calendar.getTime());

        // Apelăm funcția pentru a obține materiile pentru ziua curentă
        getSubjectsForToday(currentDay);

        // Setează textul în TextView
        textViewOrar.setText("Orarul pentru astăzi, " + dayOfWeek + ", " + currentDate);

        // Configurare Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer Layout și Navigation View
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Adaugă buton pentru deschiderea meniului
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Acasă", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_tasks) {
            Toast.makeText(this, "Task-uri", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, TaskView.class);
            startActivity(intent);
        } else if (id == R.id.nav_calendar) {
            Toast.makeText(this, "Calendar", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notes) {
            Toast.makeText(this, "Note", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Funcție pentru a obține materiile din Firestore pentru ziua curentă
    private void getSubjectsForToday(String currentDay) {
        db.collection("subjects")
                .whereEqualTo("day", currentDay)  // Căutăm doar materiile pentru ziua curentă
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if (documents != null && !documents.isEmpty()) {
                            for (QueryDocumentSnapshot document : documents) {
                                String name = document.getString("name");
                                String startTime = document.getString("startTime");
                                String endTime = document.getString("endTime");
                                String room = document.getString("room");
                                String type = document.getString("type");

                                // Populăm tabelul cu datele
                                addSubjectToTable(name, startTime, endTime, room, type);
                            }
                        } else {
                            Log.d("Firestore", "Nu au fost găsite materii pentru astăzi.");
                        }
                    } else {
                        // Dacă există o eroare la obținerea documentelor
                        Toast.makeText(MainActivity.this, "Eroare la încărcarea materiilor", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Funcție pentru a adăuga materiile în tabelul din UI
    private void addSubjectToTable(String name, String startTime, String endTime, String room, String type) {
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // Crearea unui rând pentru fiecare materie
        TableRow tableRow = new TableRow(this);

        // Parametrii pentru fiecare TextView
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

        // Adăugăm un TextView pentru numele materiei
        TextView subjectNameTextView = new TextView(this);
        subjectNameTextView.setText(name);
        subjectNameTextView.setPadding(16, 8, 16, 8);
        subjectNameTextView.setLayoutParams(params);  // Apply LayoutParams for equal width
        tableRow.addView(subjectNameTextView);

        // Adăugăm un TextView pentru ora de început și de sfârșit
        TextView timeTextView = new TextView(this);
        timeTextView.setText(startTime + " - " + endTime);
        timeTextView.setPadding(16, 8, 16, 8);
        timeTextView.setLayoutParams(params);  // Apply LayoutParams for equal width
        tableRow.addView(timeTextView);

        // Adăugăm un TextView pentru sală
        TextView roomTextView = new TextView(this);
        roomTextView.setText(room);
        roomTextView.setPadding(16, 8, 16, 8);
        roomTextView.setLayoutParams(params);  // Apply LayoutParams for equal width
        tableRow.addView(roomTextView);

        // Colorarea rândului în funcție de tipul materiei
        switch (type) {
            case "laborator":
                tableRow.setBackgroundColor(getResources().getColor(R.color.colorLaborator));
                break;
            case "seminar":
                tableRow.setBackgroundColor(getResources().getColor(R.color.colorSeminar));
                break;
            case "curs":
                tableRow.setBackgroundColor(getResources().getColor(R.color.colorCurs));
                break;
            default:
                tableRow.setBackgroundColor(getResources().getColor(R.color.white));
                break;
        }

        // Adăugăm rândul în tabel
        tableLayout.addView(tableRow);
        Log.d("Firestore", "Document data: " + type + ", " + endTime + ", " + startTime + ", " + name + ", " + room);
        View divider = new View(this);
        divider.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2)); // Divider height = 2px
        divider.setBackgroundColor(getResources().getColor(R.color.dividerColor));
        tableLayout.addView(divider);

    }


    @Override
    protected void onStart() {
        super.onStart();
        String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(Calendar.getInstance().getTime());
        getSubjectsForToday(currentDay);
    }
}
