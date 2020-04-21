package com.example.buma_p5m.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.buma_p5m.R;
import com.example.buma_p5m.models.ModelP5M;
import com.example.buma_p5m.utils.Session;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class FormPm5 extends AppCompatActivity {
    private static final String FORM = "Data Absensi";
    public static final String SPREAD_SHEET ="https://script.google.com/macros/s/AKfycbz0V-_q9QcDeGp20IAMcVrMjuWbrHzXU8AcGjmGHU8W9npDFKs/exec";
    private static final String TAG ="FormP5m" ;
    private Button btnSubmit;
    private String terimaNama, uploadTanggal, uploadBangun, uploadTidur, uploadStatus,
            uploadDepartemen, uploadJamabsen,uploadTema,uploadPemateri,uid;
    private TextView tvNama, tvJabatan, tvTanggal, tvStatus, tvJam,tvTema,tvMateri;
    private RadioButton sehat, tidak_sehat;
    private TextInputEditText tieJamBangun, tieJamTidur;
    private String keterangan = "";
    private Session session;
    private  DatabaseReference dbRef;
    private String lokasi;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pm5);

        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //memberikan nilai pada variable / initialize
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        tvTema = findViewById(R.id.temaForm);
        tvMateri = findViewById(R.id.pemateriForm);
        btnSubmit = findViewById(R.id.submitForm);
        tvJam = findViewById(R.id.jam);
        sehat = findViewById(R.id.checkSehat);
        tidak_sehat = findViewById(R.id.checkTidakSehat);
        tieJamBangun = findViewById(R.id.bangunForm);
        tieJamTidur = findViewById(R.id.tidurForm);
        tvStatus = findViewById(R.id.status);
        tvJabatan = findViewById(R.id.jabatanForm);
        tvTanggal = findViewById(R.id.tanggalSekarang);
        tvNama = findViewById(R.id.namaForm);
        session = new Session(this);
        dbRef = FirebaseDatabase.getInstance().getReference(FORM).child(session.getSPNama());
        //terima data dari sharedpreference
        terimaNama = session.getSPNama();
        uploadTema = session.getSpTema();
        uid = session.getSPUid();
        uploadPemateri = session.getSpPemater();
        tvTema.setText(uploadTema);
        tvMateri.setText(uploadPemateri);
        tvNama.setText(terimaNama);


        //mendapat tanggal hari ini
        tanggal();

        //menndapatkan jam saat ini
        jam();

        nonAktifKeyboard(); // menghilangkan keyboard saat memilih jam

        sendToFirebase();

        tieJamTidur.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(FormPm5.this,
                    (timePicker,selectedHour,selectedMinute) ->
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
        memintaPerizinan();
    }

    private void memintaPerizinan() {
        if (ActivityCompat.checkSelfPermission(FormPm5.this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            mendapatkanLokasi();
        }
        else {
            ActivityCompat.requestPermissions(FormPm5.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    private void mendapatkanLokasi() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location !=null){
                Geocoder geocoder = new Geocoder(FormPm5.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    lokasi = addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private void sendToFirebase() {
        btnSubmit.setOnClickListener(v -> {

            if (lokasi == null){
                memintaPerizinan();
            } else {
                uploadBangun = Objects.requireNonNull(tieJamBangun.getText()).toString();
                uploadTidur = Objects.requireNonNull(tieJamTidur.getText()).toString();
                uploadDepartemen = tvJabatan.getText().toString();
                uploadStatus = tvStatus.getText().toString();
                uploadTanggal = tvTanggal.getText().toString();
                uploadJamabsen = tvJam.getText().toString();

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
                    if (isEmpty(keterangan, uploadJamabsen, uploadBangun, uploadTidur, uploadTanggal, uploadStatus, uploadDepartemen, terimaNama, uploadStatus)) {
                        alertDialog.dismiss();
                        Toasty.error(getApplicationContext(), "Presensi Gagal, Periksa Koneksi Anda", Toasty.LENGTH_SHORT).show();
                        tieJamTidur.setText("");
                        tieJamBangun.setText("");
                    }
                    else {
                        alertDialog.dismiss();
                        //jika tidak ada field kosong
                        AndroidNetworking.post(SPREAD_SHEET)
                                .setPriority(Priority.HIGH)
                                .addBodyParameter("action", "addItem")
                                .addBodyParameter("uid", uid)
                                .addBodyParameter("tanggal", uploadTanggal)
                                .addBodyParameter("tema", uploadTema)
                                .addBodyParameter("pemateri", uploadPemateri)
                                .addBodyParameter("nama", terimaNama)
                                .addBodyParameter("departemen", uploadDepartemen)
                                .addBodyParameter("jam_absensi", uploadJamabsen)
                                .addBodyParameter("jam_tidur", uploadTidur)
                                .addBodyParameter("jam_bangun", uploadBangun)
                                .addBodyParameter("lokasi", lokasi)
                                .addBodyParameter("status", uploadStatus)
                                .addBodyParameter("keterangan", keterangan)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.e(TAG, "onResponse: " + response);
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                                        Toasty.info(getApplicationContext(),"Tidak ada Koneksi, Data Tersimpan di Firebase",Toasty.LENGTH_LONG).show();
                                    }
                                });
                                submitItem(new ModelP5M(terimaNama, uploadTanggal, uploadJamabsen, uploadDepartemen,
                                uploadBangun, uploadTidur, uploadStatus, keterangan, uploadTema, uploadPemateri, lokasi));
                        Toasty.success(getApplicationContext(),"Absensi Berhasil", Toasty.LENGTH_SHORT).show();
                        Intent intentHome = new Intent(FormPm5.this, HomeActivity.class);
                        startActivity(intentHome);
                        finish();
                    }
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void jam() {
        tvJam = findViewById(R.id.jam);
        Calendar jamSekarang = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
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
