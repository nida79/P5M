package com.example.buma_p5m.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.buma_p5m.R;
import com.example.buma_p5m.auth.LoginActivity;
import com.example.buma_p5m.auth.RegisterActivity;
import com.example.buma_p5m.utils.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "TEMA";
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String email, Nama, Nik, Level,status,setara,pengisi,tema;
    private TextView namaKaryawan, emailKarawan, nikKaryawan, levelakun;
    private static final String USERS = "Data Karyawan";
    private static final String TEMA = "Setup Form";
    private LinearLayout linearLayouthome, linearLayoutwadah,llsetting,llform;
    Session session;
    DatabaseReference userRef;
    DatabaseReference temaRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);

        //inisialisasi
        llform = findViewById(R.id.llForm);
        llsetting = findViewById(R.id.setupform);
        linearLayoutwadah = findViewById(R.id.llwadah);
        auth = FirebaseAuth.getInstance();
        Button buttonLogout = findViewById(R.id.btnLogout);
        namaKaryawan = findViewById(R.id.profileNama);
        emailKarawan = findViewById(R.id.profileEmail);
        nikKaryawan = findViewById(R.id.profileNIK);
        LinearLayout tombolDatapresensi = findViewById(R.id.llProfile);
        LinearLayout tombolDatapresensiAdmin = findViewById(R.id.lldatapresensi);
        linearLayouthome = findViewById(R.id.tambahData);
        levelakun = findViewById(R.id.level);
        session = new Session(this);
        setara = "Disable";

        final android.app.AlertDialog alertDialog =
                new SpotsDialog.Builder().setContext(HomeActivity.this).build();
        alertDialog.setMessage("Mohon Tunggu");
        alertDialog.setIcon(R.mipmap.ic_apk);
        alertDialog.show();

        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //menerima data sesuai email yang login
        email = session.getSPEmail();

        //initialize firebase 1
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.keepSynced(true);
        userRef = rootRef.child(USERS);
        Log.v("USERID", Objects.requireNonNull(userRef.getKey()));

        // Menampilkan data dari database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alertDialog.dismiss();
                for (DataSnapshot keyId : dataSnapshot.getChildren()) {
                    if (Objects.requireNonNull(keyId.child("email").getValue()).equals(email)) {
                        Level = keyId.child("level").getValue(String.class);
                        Nama = keyId.child("nama").getValue(String.class);
                        Nik = keyId.child("nik").getValue(String.class);
                        email = keyId.child("email").getValue(String.class);
                        break;
                    }
                }
                namaKaryawan.setText(Nama);
                nikKaryawan.setText(Nik);
                emailKarawan.setText(email);
                levelakun.setText(Level);
                session.saveSPString(Session.SP_NAMA, Nama);
                session.saveSPString(Session.SP_LEVEL, Level);
                session.saveSPString(Session.SP_NIK, Nik);

                //jika level id admin, makan menu tambah data muncul
                if (levelakun.getText().equals("Admin")) {
                    llsetting.setVisibility(View.VISIBLE);
                    linearLayouthome.setVisibility(View.VISIBLE);
                    linearLayoutwadah.setWeightSum(3);
                    llform.setVisibility(View.GONE);
                    tombolDatapresensi.setVisibility(View.GONE);
                    tombolDatapresensiAdmin.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                alertDialog.dismiss();

                Toasty.error(getApplicationContext(),
                        "Terjadi Kesalahan Pada Database", Toasty.LENGTH_SHORT).show();

            }
        });

        //initialize firebase 2
        DatabaseReference secondRef = FirebaseDatabase.getInstance().getReference();
        secondRef.keepSynced(true);
        temaRef = secondRef.child(TEMA);

        temaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    status = postSnapshot.child("status").getValue(String.class);
                    pengisi = postSnapshot.child("pemateri").getValue(String.class);
                    tema = postSnapshot.child("tema").getValue(String.class);
                }

                session.saveSPString(Session.SP_STATUS,status);
                session.saveSPString(Session.SP_PEMATER,pengisi);
                session.saveSPString(Session.SP_TEMA,tema);
                if (Objects.requireNonNull(status).equals(setara)){
                    llform.setOnClickListener(v -> {
                        Toasty.info(getApplicationContext(),"Absensi Belum dibuka",Toasty.LENGTH_LONG).show();
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // session login
        authListener = firebaseAuth -> {
            FirebaseUser user1 = firebaseAuth.getCurrentUser();
            if (user1 == null) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        };


        buttonLogout.setOnClickListener(v -> new AlertDialog.Builder(HomeActivity.this)
                .setTitle("SignOut Akun!")
                .setIcon(R.mipmap.ic_apk)
                .setMessage("Apakah Anda Yakin Ingin Keluar Akun ?")
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, which) -> HomeActivity.this.auth.signOut())
                .setNegativeButton("Tidak", null)
                .show());

        tombolDatapresensi.setOnClickListener(v -> {
            Intent showProfile = new Intent(HomeActivity.this, ListAbsensi.class);
            startActivity(showProfile);
            finish();


        });

        tombolDatapresensiAdmin.setOnClickListener(v -> {
            Intent showProfile = new Intent(HomeActivity.this, ListKaryawan.class);
            startActivity(showProfile);
            finish();


        });

        linearLayouthome.setOnClickListener(v -> {
            Intent intentregis = new Intent(HomeActivity.this, RegisterActivity.class);
            startActivity(intentregis);
            finish();
        });


        llform.setOnClickListener(v -> {
            Intent form = new Intent(HomeActivity.this, FormPm5.class);
            startActivity(form);
            finish();
        });

        llsetting.setOnClickListener(v -> {
            Intent setting = new Intent(HomeActivity.this,SetUpP5M.class);
            startActivity(setting);
            finish();
        });

    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar !")
                .setIcon(R.mipmap.ic_apk)
                .setMessage("Apakah Anda Yakin Ingin Keluar Dari Aplikasi ?")
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, which) -> HomeActivity.this.keluar())
                .setNegativeButton("Tidak", null)
                .show();


    }

    @Override
    protected void onStop() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        super.onStop();
    }

    private void keluar() {
        finish();
        System.exit(0);
    }
}
