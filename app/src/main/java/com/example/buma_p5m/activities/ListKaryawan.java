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
import java.util.List;

public class ListKaryawan extends AppCompatActivity {

    TextView textViewNama, textViewEmail, textViewNik;
    RecyclerView recyclerView;
    DatabaseReference dbRefrence;
    Session session;
    private static final String KARYAWAN = "Data Karyawan";
    KaryawanAdapter karyawanAdapter;
    List<Karyawan> userList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_karyawan);
        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //actionbar(Tulisan diatas)
//        ActionBar actionBar = getSupportActionBar();
//        assert actionBar != null;
//        actionBar.setTitle("Data Presensi Karyawan");

        textViewNik = findViewById(R.id.presennikAdmin);
        textViewNama = findViewById(R.id.presennamaAdmin);
        textViewEmail = findViewById(R.id.presenemailAdmin);
        session = new Session(this);

        //firebase
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.keepSynced(true);
        dbRefrence = rootRef.child(KARYAWAN);
        Query query = dbRefrence
                .orderByChild("nama");
        query.addListenerForSingleValueEvent(valueEventListener);
        karyawanAdapter = new KaryawanAdapter(this,userList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView = findViewById(R.id.rcvDataKaryawan);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(karyawanAdapter);

        //profile
        textViewEmail.setText(session.getSPEmail());
        textViewNama.setText(session.getSPNama());
        textViewNik.setText(session.getSpNik());

    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            userList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Karyawan datakaryawan = snapshot.getValue(Karyawan.class);
                    userList.add(datakaryawan);
                }
                karyawanAdapter.notifyDataSetChanged();
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(ListKaryawan.this, HomeActivity.class);
        startActivity(intentHome);
        finish();
    }
}

