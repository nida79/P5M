package com.example.buma_p5m.activities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buma_p5m.R;
import com.example.buma_p5m.models.ModelP5M;
import com.example.buma_p5m.utils.Session;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class FormPm5 extends AppCompatActivity {
    private static final String FORM = "Data Absensi";
    Button btnSubmit,btnlokasi;
    String terimaNama, uploadTanggal, uploadBangun, uploadTidur, uploadStatus, uploadjbtn, uploadWaktu,uploadTema,uploadPemateri;
    TextView tvNama, tvJabatan, tvTanggal, tvStatus, tvJam,tvTema,tvMateri;
    Calendar myCalendar;
    RadioButton sehat, tidak_sehat;
    TextInputEditText tieJamBangun, tieJamTidur;
    String keterangan = "";
    Session session;
    DatabaseReference dbRef;
//    String lokasi = "-6.2160077,106.9472619";
//    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pm5);

        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //mendapat tanggal hari ini
        tanggal();

        //menndapatkan jam saat ini
        jam();

        //memberikan nilai pada variable / initialize
        tvTema = findViewById(R.id.temaForm);
        tvMateri = findViewById(R.id.pemateriForm);
        btnSubmit = findViewById(R.id.submitForm);
        tvJam = findViewById(R.id.jam);
        sehat = findViewById(R.id.checkSehat);
        tidak_sehat = findViewById(R.id.checkTidakSehat);
        tieJamBangun = findViewById(R.id.bangunForm);
        tieJamTidur = findViewById(R.id.tidurForm);
        myCalendar = Calendar.getInstance();
        tvStatus = findViewById(R.id.status);
        tvJabatan = findViewById(R.id.jabatanForm);
        tvTanggal = findViewById(R.id.tanggalSekarang);
        tvNama = findViewById(R.id.namaForm);
        session = new Session(this);
        dbRef = FirebaseDatabase.getInstance().getReference(FORM).child(session.getSPNama());
        //terima data dari sharedpreference
        terimaNama = session.getSPNama();
        uploadTema = session.getSpTema();
        uploadPemateri = session.getSpPemater();
        tvTema.setText(uploadTema);
        tvMateri.setText(uploadPemateri);
        tvNama.setText(terimaNama);


        nonAktifKeyboard(); // menghilangkan keyboard saat memilih jam

        sendToFirebase();

        tieJamTidur.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(FormPm5.this,
                    (timePicker, selectedHour, selectedMinute) ->
                            tieJamTidur.setText(String.format("%02d:%02d", selectedHour, selectedMinute) + " WIB")
                    , hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Pilih Jam");
            mTimePicker.show();

        });

        tieJamBangun.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(FormPm5.this,
                    (timePicker, selectedHour, selectedMinute) ->
                            tieJamBangun.setText(String.format("%02d:%02d", selectedHour, selectedMinute) + " WIB")
                    , hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Pilih Jam");
            mTimePicker.show();

        });

        //cek lokasi
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        btnlokasi.setOnClickListener(v -> {
//            if (ActivityCompat.checkSelfPermission(FormPm5.this,ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//                return;
//            }
//            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(FormPm5.this, location -> {
//
//            });
//
//        });
//
//        memintaperizinan();

    }
//    private void memintaperizinan() {
//        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
//    }


    private void sendToFirebase() {
        btnSubmit.setOnClickListener(v -> {
            uploadBangun = Objects.requireNonNull(tieJamBangun.getText()).toString();
            uploadTidur = Objects.requireNonNull(tieJamTidur.getText()).toString();
            uploadjbtn = tvJabatan.getText().toString();
            uploadStatus = tvStatus.getText().toString();
            uploadTanggal = tvTanggal.getText().toString();
            uploadWaktu = tvJam.getText().toString();

            if (sehat.isChecked()) {
                keterangan = "Sehat";
            }

            if (tidak_sehat.isChecked()) {
                keterangan = "Tidak Sehat";
            }

            if (uploadTidur.isEmpty()) {
                Toasty.warning(getApplicationContext(), "Masukkan Jam Tidur Anda"
                        , Toasty.LENGTH_SHORT).show();
                return;
            }

            if (uploadBangun.isEmpty()) {
                Toasty.warning(getApplicationContext(), "Masukkan Jam Bangun Anda"
                        , Toasty.LENGTH_SHORT).show();
                return;
            }

            if (keterangan.equals("")) {
                Toasty.warning(getApplicationContext(), "Status Keterangan Belum Dipilih"
                        , Toasty.LENGTH_SHORT).show();
            } else {

                final android.app.AlertDialog alertDialog =
                        new SpotsDialog.Builder().setContext(FormPm5.this).build();
                alertDialog.setMessage("Mengirim Data");
                alertDialog.show();

                //jika gagal
                if (isEmpty(keterangan, uploadWaktu, uploadBangun, uploadTidur, uploadTanggal, uploadStatus, uploadjbtn, terimaNama, uploadStatus)) {
                    alertDialog.dismiss();
                    Toasty.error(getApplicationContext(), "Presensi Gagal, Periksa Koneksi Anda", Toasty.LENGTH_SHORT).show();
                    tieJamTidur.setText("");
                    tieJamBangun.setText("");
                }

                //jika berhasil
                else {
                    alertDialog.dismiss();
                    submitItem(new ModelP5M(terimaNama, uploadTanggal, uploadWaktu, uploadjbtn,
                            uploadBangun, uploadTidur, uploadStatus, keterangan,uploadTema,uploadPemateri));
                    Toasty.success(getApplicationContext(),"Absensi Berhasil", Toasty.LENGTH_SHORT).show();
                    Intent intentHome = new Intent(FormPm5.this, HomeActivity.class);
                    startActivity(intentHome);
                    finish();
                }
            }
        });
    }

    private void jam() {

        tvJam = findViewById(R.id.jam);
        Calendar jamSekarang = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String jamNow = simpleDateFormat.format(jamSekarang.getTime());
        tvJam.setText(jamNow + " WIB");
    }

    private boolean isEmpty(String s, String uploadWaktu, String uploadBangun, String uploadTidur, String uploadTanggal, String uploadStatus, String uploadJabatan, String terimaNama, String status) {
        // Cek apakah ada fields yang kosong, sebelum disubmit
        return TextUtils.isEmpty(s);
    }

    private void submitItem(ModelP5M upload) {
        final android.app.AlertDialog alertDialog =
                new SpotsDialog.Builder().setContext(FormPm5.this).build();
        alertDialog.setMessage("Mengirim Data");
        alertDialog.show();

        dbRef.push().setValue(upload).addOnSuccessListener(this, aVoid -> {
            alertDialog.dismiss();
            tieJamTidur.setText("");
            tieJamBangun.setText("");
        });
    }

    private void tanggal() {
        tvTanggal = findViewById(R.id.tanggalSekarang);
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        tvTanggal.setText(currentDate);
    }

    private void nonAktifKeyboard() {
        tieJamTidur.requestFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tieJamTidur.setShowSoftInputOnFocus(false);
        }

        tieJamBangun.requestFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tieJamBangun.setShowSoftInputOnFocus(false);
        }


    }

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(FormPm5.this, HomeActivity.class);
        startActivity(intentHome);
        finish();
    }
}
