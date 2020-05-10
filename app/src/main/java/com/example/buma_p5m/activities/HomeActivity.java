package com.example.buma_p5m.activities;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.buma_p5m.R;
import com.example.buma_p5m.auth.LoginActivity;
import com.example.buma_p5m.auth.RegisterActivity;
import com.example.buma_p5m.utils.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

import static com.example.buma_p5m.utils.App.CHANNEL_1_ID;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "TEMA";
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String email, Nama, Nik, Level,status,setara,pengisi,tema,uid,tgl_limit;
    private TextView namaKaryawan, emailKarawan, nikKaryawan, levelakun,tvTanggal;
    private static final String USERS = "Data Karyawan";
    private static final String TEMA = "Setup Form";
    private LinearLayout linearLayouthome, linearLayoutwadah,llsetting,llform;
    private Session session;
    private NotificationManagerCompat notificationManager;
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
        tanggal();
        notificationManager = NotificationManagerCompat.from(this);

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
        DatabaseReference userRef = rootRef.child(USERS);
        Query query = userRef.orderByChild("email").equalTo(email);
        Log.v("USERID", Objects.requireNonNull(userRef.getKey()));

        // Menampilkan data dari database
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alertDialog.dismiss();
                for (DataSnapshot keyId : dataSnapshot.getChildren()) {
                    Level = keyId.child("level").getValue(String.class);
                    Nama = keyId.child("nama").getValue(String.class);
                    Nik = keyId.child("nik").getValue(String.class);
                    email = keyId.child("email").getValue(String.class);
                    uid = keyId.child("uid").getValue(String.class);
                    tgl_limit = keyId.child("tanggal_limit").getValue(String.class);

                }

                namaKaryawan.setText(Nama);
                nikKaryawan.setText(Nik);
                emailKarawan.setText(email);
                levelakun.setText(Level);
                session.saveSPString(Session.SP_NAMA, Nama);
                session.saveSPString(Session.SP_LEVEL, Level);
                session.saveSPString(Session.SP_NIK, Nik);
                session.saveSPString(Session.SP_UID,uid);

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
                        databaseError.getMessage(), Toasty.LENGTH_SHORT).show();

            }
        });

        //initialize firebase 2
        DatabaseReference secondRef = FirebaseDatabase.getInstance().getReference();
        secondRef.keepSynced(true);
        DatabaseReference temaRef = secondRef.child(TEMA);

        //cek absensi enable/disable
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

                llform.setOnClickListener(v->{
                    if (Objects.requireNonNull(status).equals(setara)){
                        Toasty.info(getApplicationContext(),"Absensi Belum dibuka",Toasty.LENGTH_SHORT).show();
                    }
                    else if (tgl_limit.equals(tvTanggal.getText().toString())){
                        Toasty.warning(getApplicationContext(),"Anda Sudah Melakukan Absensi !",Toasty.LENGTH_SHORT).show();
                    }
                    else {
                        startActivity(new Intent(getApplicationContext(),FormPm5.class));
                        finishAffinity();
                        finish();
                    }

                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getApplicationContext(),databaseError.getMessage(),Toasty.LENGTH_LONG).show();
            }
        });

            temaRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (!status.equals(setara)){
                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.icon_p5m)
                                .setContentTitle("Selamat Pagi Buma")
                                .setContentText("Absensi Sudah Dibuka, Terimakasih ")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();
                        notificationManager.notify(1, notification);
                    }
                    if (status.equals(setara)){
                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.icon_p5m)
                                .setContentTitle("Selamat Pagi Buma")
                                .setContentText("Absensi Sudah Ditutup, Terimakasih ")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();
                        notificationManager.notify(1, notification);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
            finishAffinity();
            finish();


        });

        tombolDatapresensiAdmin.setOnClickListener(v -> {
            Intent showProfile = new Intent(HomeActivity.this, ListKaryawan.class);
            startActivity(showProfile);
            finishAffinity();
            finish();
        });

        linearLayouthome.setOnClickListener(v -> {
            Intent intentregis = new Intent(HomeActivity.this, RegisterActivity.class);
            startActivity(intentregis);
            finishAffinity();
            finish();
        });

        llsetting.setOnClickListener(v -> {
            Intent setting = new Intent(HomeActivity.this,SetUpP5M.class);
            startActivity(setting);
            finishAffinity();
            finish();
        });

        memintaPerizinan();
    }

    private void tanggal() {

        tvTanggal = findViewById(R.id.tanggallimit);
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        tvTanggal.setText(currentDate);
    }
    private void memintaPerizinan() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            Log.e(TAG, "memintaPerizinan: " );
        }
        else {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==44){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.e(TAG, "onRequestPermissionsResult: " );
            }
        }
        else {
            memintaPerizinan();
        }
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
