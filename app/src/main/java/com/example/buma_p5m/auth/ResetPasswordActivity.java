package com.example.buma_p5m.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.buma_p5m.utils.EmailValidator;
import com.example.buma_p5m.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class ResetPasswordActivity extends AppCompatActivity {

    //membuat variable
    private TextView textViewBack;
    private TextInputEditText tieResetEmail;
    private Button buttonSubmit;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //deklarasi
        auth = FirebaseAuth.getInstance();
        tieResetEmail = findViewById(R.id.lupaEmail);
        textViewBack = findViewById(R.id.tvmasukLogin);
        buttonSubmit = findViewById(R.id.btnSbumit);

        // function kembali
        textViewBack.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        resetPassword();
    }

    //method resset password
    private void resetPassword() {
        buttonSubmit.setOnClickListener(v -> {

            String email = Objects.requireNonNull(tieResetEmail.getText()).toString().trim();

            final android.app.AlertDialog alertDialog =
                    new SpotsDialog.Builder().setContext(ResetPasswordActivity.this).build();
            alertDialog.setMessage("Mohon Tunggu Sedang Dalam Prosess");
            alertDialog.setIcon(R.mipmap.ic_apk);
            alertDialog.show();

            if (TextUtils.isEmpty(email)) {
                alertDialog.dismiss();
                Toasty.warning(getApplication(), "Masukan Email Anda !", Toasty.LENGTH_SHORT).show();
                tieResetEmail.setError("Email Tidak Boleh Kosong");
                return;
            }
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            alertDialog.dismiss();
                            Toasty.success(ResetPasswordActivity.this, "Kami telah mengirimkan instruksi kepada email anda !", Toasty.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }else{
                            alertDialog.dismiss();
                            EmailValidator.validate(email);
                            {
                            Toasty.warning(getApplicationContext(),
                                    "Email tidak Valid", Toasty.LENGTH_SHORT).show();
                            tieResetEmail.setError("Hindari Karakter $%@+_,");
                            }
                        }
                    }).addOnFailureListener(e -> {
                        alertDialog.dismiss();
                        Toasty.error(getApplicationContext(), "Email tidak Valid", Toasty.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
        super.onBackPressed();
    }
}
