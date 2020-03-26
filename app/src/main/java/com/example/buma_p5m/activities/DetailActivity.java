package com.example.buma_p5m.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.buma_p5m.R;
import com.example.buma_p5m.adapters.AbsensiAdapter;
import com.example.buma_p5m.models.Absensi;
import com.example.buma_p5m.utils.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    String dapatnama,dapatnik;
    TextView textViewNama, textViewEmail, textViewNik;
    RecyclerView recyclerView;
    DatabaseReference dbRefrence;
    Session session;
    Query query;
    AbsensiAdapter absensiAdapter;
    List<Absensi> absensilist = new ArrayList<>();
    private static final String PRESENSI = "Data Presensi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dapatnama = getIntent().getStringExtra("extranama");
        dapatnik = getIntent().getStringExtra("extranik");

        textViewNik = findViewById(R.id.detailNik);
        textViewNama = findViewById(R.id.detailNama);
        textViewEmail = findViewById(R.id.detailEmail);
        session = new Session(this);

        absensiAdapter = new AbsensiAdapter(this,absensilist);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView = findViewById(R.id.rcvDataPresensi);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        dbRefrence = FirebaseDatabase.getInstance().getReference(PRESENSI);
        query = dbRefrence.orderByChild("name").equalTo(dapatnama);
        query.addListenerForSingleValueEvent(valueEventListener);
        recyclerView.setAdapter(absensiAdapter);

        textViewNik.setText(dapatnik);
        textViewNama.setText(dapatnama);

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            absensilist.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Absensi dataabsensi = snapshot.getValue(Absensi.class);
                    absensilist.add(dataabsensi);
                }
                absensiAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };



    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(DetailActivity.this, ListKaryawan.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intentHome);
        finish();
    }
}
