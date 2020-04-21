package com.example.buma_p5m.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.buma_p5m.R;
import com.example.buma_p5m.models.Absensi;
import com.example.buma_p5m.models.SetUpFormModel;
import com.example.buma_p5m.utils.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class SetUpP5M extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String FORM = "Setup Form";
    String spin,statusAbsen,materi;
    String uid ;
    EditText editText;
    TextView textView;
    private Button kirim;

    Query query;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_p5_m);


        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        uid = "1";
        kirim = findViewById(R.id.submitFormSetup);
        Switch mySwitch = findViewById(R.id.btnswithc);
        editText = findViewById(R.id.edtPemateri);
        textView = findViewById(R.id.kirimAbsen);

        //switch button
        mySwitch.setChecked(false);
        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                textView.setText("Enable");
                textView.setTextColor(getResources().getColor(R.color.ijo));
            } else {
                textView.setText("Disable");
                textView.setTextColor(getResources().getColor(R.color.red_btn_bg_color));
            }

        });

        //spiner
        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.themeP5M, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        sendtofirebase();
    }

    private void sendtofirebase() {
        kirim.setOnClickListener(v -> { final android.app.AlertDialog alertDialog =
                new SpotsDialog.Builder().setContext(SetUpP5M.this).build();
            alertDialog.setMessage("Mengirim Data");
            alertDialog.show();
            statusAbsen = textView.getText().toString();
            materi = editText.getText().toString();
            submitItem(statusAbsen,uid,spin,materi);
        });
    }

    private void submitItem(String statusAbsen, String uid, String spin, String materi) {
        //Firebase
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mFirebaseDatabase.getReference(FORM).child(uid);
        SetUpFormModel data = new SetUpFormModel(statusAbsen,uid,spin,materi);
        mRef.setValue(data);
        Toasty.success(getApplicationContext(),"Setting Berhasil", Toasty.LENGTH_SHORT).show();
        Intent intentHome = new Intent(SetUpP5M.this, HomeActivity.class);
        startActivity(intentHome);
        finish();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spin = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(SetUpP5M.this, HomeActivity.class);
        startActivity(intentHome);
        finish();
    }
}
