package com.example.buma_p5m.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ListAbsensi extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference dbRefrence;
    private Session session;
    private SearchView searchView;
    private ArrayList<Absensi> absensilist;
    private Query query;
    private static final String PRESENSI = "Data Absensi";
    public static final String DATA = "EXTRA_DATA";
    private AbsensiAdapter absensiAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_absensi);

        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        searchView = findViewById(R.id.searchAbsen);
        session = new Session(this);

        //Firebase
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.keepSynced(true);
        dbRefrence = rootRef.child(PRESENSI).child(session.getSPNama());
        query = dbRefrence.limitToLast(20);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        recyclerView = findViewById(R.id.rcvDataPresensi);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);


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
                            Intent intent = new Intent(ListAbsensi.this,DetailActivity.class);
                            intent.putExtra(DATA,absensilist.get(position));
                            intent.putExtra("data","user");
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
                Intent intent = new Intent(ListAbsensi.this,DetailActivity.class);
                intent.putExtra(DATA,searchList.get(position));
                intent.putExtra("data","user");
                startActivity(intent);
            });
        }
        else {
            Toasty.info(getApplicationContext(),"Data Tidak Ditemukan",Toasty.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(ListAbsensi.this, HomeActivity.class);
        startActivity(intentHome);
        finish();
    }
}
