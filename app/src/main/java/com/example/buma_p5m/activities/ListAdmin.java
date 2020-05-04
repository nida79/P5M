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

import es.dmoral.toasty.Toasty;

public class ListAdmin extends AppCompatActivity {

    private String dapatnama,dapatnik;
    private TextView textViewNama, textViewNik;
    private RecyclerView recyclerView;
    private DatabaseReference dbRefrence;
    private Query query;
    private SearchView searchView;
    private AbsensiAdapter absensiAdapter;
    private ArrayList<Absensi> absensilist;
    private static final String PRESENSI = "Data Absensi";
    public static final String DATA = "EXTRA_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_admin);

        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dapatnama = getIntent().getStringExtra("extranama");
        dapatnik = getIntent().getStringExtra("extranik");

        textViewNik = findViewById(R.id.detailNik);
        textViewNama = findViewById(R.id.detailNama);
        searchView = findViewById(R.id.filterAbsen);
        absensiAdapter = new AbsensiAdapter(absensilist);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
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
            dbRefrence.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        absensilist = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            absensilist.add(ds.getValue(Absensi.class));
                        }
                        absensiAdapter = new AbsensiAdapter(absensilist);
                        recyclerView.setAdapter(absensiAdapter);
                        absensiAdapter.notifyDataSetChanged();
                        absensiAdapter.setOnItemClickListener(position -> {
                            Intent intent = new Intent(ListAdmin.this,DetailActivity.class);
                            intent.putExtra(DATA,absensilist.get(position));
                            intent.putExtra("data","admin");
                            intent.putExtra("extranik",dapatnik);
                            startActivity(intent);
                        });
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
        if (absensilist!=null){
            for (Absensi data : absensilist){
                    if (data.getTanggal().toLowerCase().contains(newText.toLowerCase())){
                        searchList.add(data);
                    }

            }
            absensiAdapter = new AbsensiAdapter(searchList);
            recyclerView.setAdapter(absensiAdapter);
            absensiAdapter.notifyDataSetChanged();
            absensiAdapter.setOnItemClickListener(position -> {
                Intent intent = new Intent(ListAdmin.this,DetailActivity.class);
                intent.putExtra(DATA,searchList.get(position));
                intent.putExtra("data","admin");
                intent.putExtra("extranik",dapatnik);
                startActivity(intent);
            });
        }
        else {
            Toasty.info(getApplicationContext(),"Data Tidak Ditemukan",Toasty.LENGTH_SHORT).show();
        }

    }



    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(ListAdmin.this, ListKaryawan.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intentHome);
        finishAffinity();
        finish();
    }
}
