package com.example.buma_p5m.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.buma_p5m.R;
import com.example.buma_p5m.models.ModelP5M;
import com.example.buma_p5m.utils.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class FormPm5 extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String FORM = "Data Absensi";
    public static final String SPREAD_SHEET ="https://script.google.com/macros/s/AKfycbz0V-_q9QcDeGp20IAMcVrMjuWbrHzXU8AcGjmGHU8W9npDFKs/exec";
    private static final String TAG ="FormP5m" ;
    private Button btnSubmit;
    private String terimaNama, uploadTanggal, uploadBangun, uploadTidur, uploadKordinat,
            uploadDepartemen, uploadJamabsen,uploadTema,uploadPemateri,uid;
    private TextView tvNama, tvJabatan, tvTanggal, tvJam,tvTema,tvMateri;
    private RadioButton sehat, tidak_sehat;
    private TextInputEditText tieJamBangun, tieJamTidur;
    private String keterangan = "";
    private Session session;
    private  DatabaseReference dbRef;
    private String lokasi;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(USERS);
    private static final String USERS = "Data Karyawan";
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
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[0]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();

    }

    private void mendapatkanLokasi() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            String korlat;
            String korlong;
            if (location !=null){
                Geocoder geocoder = new Geocoder(FormPm5.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    lokasi = addresses.get(0).getAddressLine(0);
                    korlat = String.valueOf(addresses.get(0).getLatitude());
                    korlong = String.valueOf(addresses.get(0).getLongitude());
                    uploadKordinat = korlat+","+korlong;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendToFirebase() {
        btnSubmit.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&  ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showSettingsDialog();
            }
            else {


                if (lokasi == null){
                    Toasty.info(getApplicationContext(),"Aktifkan GPS Anda dan tunggu 6 detik untuk mendapatkan lokasi yang akurat",Toasty.LENGTH_LONG).show();
                    mendapatkanLokasi();
                }
                else
                {
                    uploadBangun = Objects.requireNonNull(tieJamBangun.getText()).toString();
                    uploadTidur = Objects.requireNonNull(tieJamTidur.getText()).toString();
                    uploadDepartemen = tvJabatan.getText().toString();
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
                        if (isEmpty(keterangan, uploadJamabsen, uploadBangun, uploadTidur, uploadTanggal, uploadKordinat, uploadDepartemen, terimaNama,lokasi)) {
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
                                    .addBodyParameter("status", uploadKordinat)
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
                                    uploadBangun, uploadTidur, uploadKordinat, keterangan, uploadTema, uploadPemateri, lokasi));
                            Query query = databaseReference.orderByChild("nama").equalTo(terimaNama);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds :dataSnapshot.getChildren()){
                                        //update Data
                                        ds.getRef().child("tanggal_limit").setValue(uploadTanggal);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //TODO~
                                }
                            });

                            Toasty.success(getApplicationContext(),"Absensi Berhasil", Toasty.LENGTH_SHORT).show();
                            Intent intentHome = new Intent(FormPm5.this, HomeActivity.class);
                            intentHome.putExtra("sudah",1);
                            startActivity(intentHome);
                            finish();
                        }
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

    private boolean isEmpty(String s, String uploadWaktu, String uploadBangun, String uploadTidur, String uploadTanggal, String uploadKordinat, String uploadJabatan, String terimaNama, String status) {
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


    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            Toasty.info(getApplicationContext(),"Aktifkan GPS Anda",Toasty.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Permissions ok, we get last location
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

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toasty.info(this, "Aktifkan Perizinan Lokasi Untuk Melakukan Absensi !", Toasty.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mendapatkanLokasi();

        }
    }
    private void showSettingsDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(FormPm5.this);
        builder.setTitle("Izin Lokasi Diperlukan !");
        builder.setMessage("Aktifkan Perizinan untuk Melakukan Absensi");
        builder.setPositiveButton("BUKA PENGATURAN", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perm : permissionsToRequest) {
                if (!hasPermission(perm)) {
                    permissionsRejected.add(perm);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                        new AlertDialog.Builder(FormPm5.this).
                                setMessage("Izin Lokasi Dibutuhkan Untuk Melakukan Absensi .!").
                                setPositiveButton("BUKA PENGATURAN", (dialogInterface, i) -> requestPermissions(permissionsRejected.
                                        toArray(new String[0]), ALL_PERMISSIONS_RESULT)).setNegativeButton("Cancel", null).create().show();

                    }
                }
            } else {
                if (googleApiClient != null) {
                    googleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(FormPm5.this, HomeActivity.class);
        startActivity(intentHome);
        finishAffinity();
        finish();
    }
}
