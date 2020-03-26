package com.example.buma_p5m.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.buma_p5m.R;
import com.example.buma_p5m.activities.HomeActivity;
import com.example.buma_p5m.utils.EmailValidator;
import com.example.buma_p5m.utils.Session;
import com.google.android.material.textfield.TextInputEditText;
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

public class LoginActivity extends AppCompatActivity {

    //membuat variabel
    private TextInputEditText tieEmail, tiePassword;
    private TextView textViewLupa;
    private Button buttonLogin;
    private FirebaseAuth auth;
    Session session;
    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //deklarasi
        auth = FirebaseAuth.getInstance();
        tieEmail = findViewById(R.id.loginUser);
        tiePassword = findViewById(R.id.loginpsswd);
        buttonLogin = findViewById(R.id.btnloginAkun);
        textViewLupa = findViewById(R.id.tvLupaPass);
        session = new Session(this);
        //Cek Apakah Sudah ada user yang login
        session();

        login(); // memanggil method login

        //Berpindah ke aktivitas Reset Password
        textViewLupa.setOnClickListener(
                v -> {
                    //function berpindah ke activity Lupa Password
                    Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    startActivity(intent);
                    finish();
                });
    }

    @Override
    protected void onStart() {
        session();
        super.onStart();
    }

    //function session
    public void updateUI(FirebaseUser currentUser) {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        Log.v("DATA", currentUser.getUid());
        finish();
    }

    //method session
    private void session() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void keluar() {
        finish();
        System.exit(0);
    }
    //method login
    private void login() {
        buttonLogin.setOnClickListener(v -> {
            //alert dialog
            final android.app.AlertDialog alertDialog =
                    new SpotsDialog.Builder().setContext(LoginActivity.this).build();
            alertDialog.setMessage("Mohon Tunggu");
            alertDialog.setIcon(R.mipmap.ic_apk);
            alertDialog.show();

            //Validasi
            final String email = Objects.requireNonNull(tieEmail.getText()).toString().trim();
            final String password = Objects.requireNonNull(tiePassword.getText()).toString().trim();
            session.saveSPString(Session.SP_EMAIL,email);
            if (TextUtils.isEmpty(email)) {
                alertDialog.dismiss();
                Toasty.warning(LoginActivity.this,
                        "Anda Belum Mengisi Form Email !",
                        Toasty.LENGTH_SHORT).show();
                tieEmail.setError("Email Tidak Boleh Kosong");
                return;
            }
            if (!EmailValidator.validate(email)) {
                alertDialog.dismiss();
                Toasty.warning(LoginActivity.this,
                        "Terjadi Kesalahan,Periksa Kembali Email Anda",
                        Toasty.LENGTH_SHORT).show();
                tieEmail.setError("Email Tidak Valid");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                alertDialog.dismiss();
                Toasty.warning(LoginActivity.this,
                        "Anda Belum Mengisi Form Password !", Toasty.LENGTH_SHORT).show();
                tiePassword.setError("Password Tidak Boleh Kosong");
                return;
            }
            if (password.length() < 6) {
                alertDialog.dismiss();
                Toasty.warning(LoginActivity.this,
                        "Password Terlalu Pendek !", Toasty.LENGTH_SHORT).show();
                tiePassword.setError("Masukan Password Minimal 6 Karakter");
                return;
            }

            //Firebase Login Dengan Email dan Password
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                alertDialog.dismiss();  //menghilangkan loading

                //jika tugas berhasil
                if (task.isSuccessful()) {
                    alertDialog.dismiss(); // menghilangkan loading
                    Toasty.success(LoginActivity.this, "Login Berhasil", Toasty.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(Objects.requireNonNull(user));
                } else { //jika tidak berhasil
                    alertDialog.dismiss();
                    Toasty.error(LoginActivity.this,
                            "Login Gagal, Data Tidak Sesuai", Toasty.LENGTH_SHORT).show();

                }
            });
        });
    }

    // Tombol Back
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar !")
                .setIcon(R.mipmap.ic_apk)
                .setMessage("Apakah Anda Yakin Ingin Keluar Dari Aplikasi ?")
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, which) -> LoginActivity.this.keluar())
                .setNegativeButton("Tidak", null)
                .show();

    }
}