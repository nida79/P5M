package com.example.buma_p5m.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buma_p5m.utils.EmailValidator;
import com.example.buma_p5m.R;
import com.example.buma_p5m.activities.HomeActivity;
import com.example.buma_p5m.models.User;
import com.example.buma_p5m.utils.Session;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG ="RgisterActivity" ;
    private String[] list = {"-Pilih-", "Admin", "User"};

    Session session;
    TextView textViewLevel;
    Button buttonReg;
    TextInputEditText tieRegNama, tieRegNik, tieRegEmail, tieRegPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth mAuth2;
    DatabaseReference databaseReference;
    RadioButton pria, wanita;
    String gender = "";
    private static final String USERS = "Data Karyawan";
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Memberikan Identitas Terhadap Variabel
        pria = findViewById(R.id.checkLaki);
        wanita = findViewById(R.id.checkPerempuan);
        textViewLevel = findViewById(R.id.texspin);
        buttonReg = findViewById(R.id.btnReg);
        tieRegEmail = findViewById(R.id.regisEmail);
        tieRegNama = findViewById(R.id.regisNama);
        Spinner spinnerLevel = findViewById(R.id.levelSpineer);
        session = new Session(this);
        tieRegNik = findViewById(R.id.regisNik);
        tieRegPassword = findViewById(R.id.regisPassword);

        // Setting Dabase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(USERS);

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("[https://buma-377a5.firebaseio.com/]")
                .setApiKey("AIzaSyAcCyuHlUvXAjZjqB2OCXAp3xlNA-nuEso")
                .setApplicationId("buma-377a5").build();

        try { FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "BumaAuth");
            mAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("BumaAuth"));

        }
        //set spinner
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapter);
        spinnerLevel.setOnItemSelectedListener(this);

        //Function Tombol Registrasi
        buttonReg.setOnClickListener(v -> {
            String level = textViewLevel.getText().toString();
            String email = Objects.requireNonNull(tieRegEmail.getText()).toString();
            String nama = Objects.requireNonNull(tieRegNama.getText()).toString();
            String nik = Objects.requireNonNull(tieRegNik.getText()).toString();
            String password = Objects.requireNonNull(tieRegPassword.getText()).toString();

            final android.app.AlertDialog alertDialog =
                    new SpotsDialog.Builder().setContext(RegisterActivity.this).build();
            alertDialog.setMessage("Mohon Tunggu");
            alertDialog.setIcon(R.mipmap.ic_apk);
            alertDialog.show();

            if (pria.isChecked()) {
                gender = "Pria";
            }
            if (wanita.isChecked()) {
                gender = "Wanita";
            }
            if (nama.isEmpty()) {
                Toasty.warning(getApplicationContext(), "Nama Belum Di Isi !", Toasty.LENGTH_SHORT).show();
                tieRegNama.setError("Nama Tidak Boleh Kosong!");
                alertDialog.dismiss();
                return;
            }
            if (nik.isEmpty()) {
                Toasty.warning(getApplicationContext(), "Nik Belum Di Isi !", Toasty.LENGTH_SHORT).show();
                tieRegNik.setError("Nik Tidak Boleh Kosong !");
                alertDialog.dismiss();
                return;
            }
            if (email.isEmpty()) {
                Toasty.warning(getApplicationContext(), "Email Belum Di Isi !", Toasty.LENGTH_SHORT).show();
                tieRegEmail.setError("Email Harus Di Isi !");
                alertDialog.dismiss();
                return;
            }
            if (!EmailValidator.validate(email)) {
                Toasty.warning(getApplicationContext(), "Email Tidak Valid!", Toasty.LENGTH_SHORT).show();
                tieRegEmail.setError("Format Email Salah !");
                alertDialog.dismiss();
                return;
            }
            if (password.isEmpty()) {
                Toasty.warning(getApplicationContext(), "Password Belum Di Isi !", Toasty.LENGTH_SHORT).show();
                tieRegPassword.setError("Password Tidka Boleh Kosong");
                alertDialog.dismiss();
                return;

            }
            if (password.length() <= 6) {
                Toasty.warning(getApplicationContext(), "Password Terlalu Pendek !", Toasty.LENGTH_SHORT).show();
                tieRegPassword.setError("Masukan Minimal 6 Karakter");
                alertDialog.dismiss();
                return;
            }
            if (gender.equals("")) {
                Toasty.warning(getApplicationContext(), "Pilih Gender", Toasty.LENGTH_SHORT).show();
                alertDialog.dismiss();
                return;
            }
            if (level.equals("-Pilih-")){
                alertDialog.dismiss();
                Toasty.warning(getApplicationContext(),"Pilih Level Pengguna",Toasty.LENGTH_SHORT).show();
                return;
            }
            else {
                tieRegEmail.setText("");
                tieRegNama.setText("");
                tieRegNik.setText("");
                tieRegPassword.setText("");
                textViewLevel.setText("-Pilih-");
                spinnerLevel.setSelection(0);
            }

            // Membuat Data Email Dan Password
            mAuth2.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this
                    , task -> {
                        if (task.isSuccessful()) {
                            //menghilangkan loading
                            alertDialog.dismiss();
                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                            String uid = Objects.requireNonNull(user).getUid();
                            // mengirim data sesuai model yang telah dibuat
                            User informasi = new User(nama, nik, email, gender,level,uid);
                            databaseReference.push().setValue(informasi).addOnCompleteListener(task1 -> {
                                alertDialog.dismiss();
                                Toasty.success(RegisterActivity.this, "Berhasil Menambah Data Karyawan"
                                        , Toasty.LENGTH_SHORT).show();
                                mAuth2.signOut();
                            });

                        } else {
                            alertDialog.dismiss();
                            Toasty.error(getApplicationContext()
                                    , "Terjadi kesalahan, Periksa Kembali Data Anda"
                                    , Toasty.LENGTH_SHORT).show();
                        }
                    });
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        textViewLevel.setText(list[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        textViewLevel.setText("");
    }
}
