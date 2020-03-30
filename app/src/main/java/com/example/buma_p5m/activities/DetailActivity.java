package com.example.buma_p5m.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

import es.dmoral.toasty.Toasty;

public class DetailActivity extends AppCompatActivity {

    String dapatnama,dapatnik;
    TextView textViewNama, textViewEmail, textViewNik;
    RecyclerView recyclerView;
    DatabaseReference dbRefrence;
    Session session;
    Query query;
    SearchView searchView;
    AbsensiAdapter absensiAdapter;
    ArrayList<Absensi> absensilist;
    private static final String PRESENSI = "Data Absensi";

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
        searchView = findViewById(R.id.filterAbsen);
        absensiAdapter = new AbsensiAdapter(this,absensilist);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView = findViewById(R.id.rcvDataPresensi);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        dbRefrence = FirebaseDatabase.getInstance().getReference(PRESENSI).child(dapatnama);
        query = dbRefrence.limitToLast(20);
        textViewNik.setText(dapatnik);
        textViewNama.setText(dapatnama);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (dbRefrence!=null){
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        absensilist = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            absensilist.add(ds.getValue(Absensi.class));
                        }
                        AbsensiAdapter absensiAdapter = new AbsensiAdapter(DetailActivity.this,absensilist);
                        recyclerView.setAdapter(absensiAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toasty.error(getApplicationContext(),""+databaseError.getMessage(),Toasty.LENGTH_LONG).show();
                }
            });
        }
        if (searchView!=null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    cari(newText);
                    return true;
                }
            });
        }
    }

    private void cari(String newText) {
        ArrayList<Absensi> searchList = new ArrayList<>();
        for (Absensi data : absensilist){
            if (data.tanggal.toLowerCase().contains(newText.toLowerCase())){
                searchList.add(data);
            }
        }
        AbsensiAdapter absensiAdapter = new AbsensiAdapter(this,searchList);
        recyclerView.setAdapter(absensiAdapter);
    }



    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(DetailActivity.this, ListKaryawan.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intentHome);
        finish();
    }
}
