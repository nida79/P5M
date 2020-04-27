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
import com.example.buma_p5m.adapters.KaryawanAdapter;
import com.example.buma_p5m.models.Karyawan;
import com.example.buma_p5m.utils.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ListKaryawan extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference dbRefrence;
    Session session;
    Query query;
    private static final String KARYAWAN = "Data Karyawan";
    ArrayList<Karyawan> userList;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_karyawan);
        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        session = new Session(this);
        searchView = findViewById(R.id.searchKaryawan);
        //firebase
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.keepSynced(true);
        dbRefrence = rootRef.child(KARYAWAN);
        query = dbRefrence.orderByChild("nama");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView = findViewById(R.id.rcvDataKaryawan);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (dbRefrence!=null){
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        userList = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            userList.add(ds.getValue(Karyawan.class));
                        }
                        KaryawanAdapter karyawanAdapter = new KaryawanAdapter(ListKaryawan.this,userList);
                        karyawanAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(karyawanAdapter);
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
        ArrayList<Karyawan> searchList = new ArrayList<>();
        for (Karyawan data : userList){
            if (data.nama.toLowerCase().contains(newText.toLowerCase())||data.nik.toLowerCase().contains(newText)){
                searchList.add(data);
            }
        }
        KaryawanAdapter cariKaryawan = new KaryawanAdapter(this,searchList);
        cariKaryawan.notifyDataSetChanged();
        recyclerView.setAdapter(cariKaryawan);
    }

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(ListKaryawan.this, HomeActivity.class);
        startActivity(intentHome);
        finishAffinity();
        finish();
    }
}

