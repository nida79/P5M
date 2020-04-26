package com.example.buma_p5m.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.buma_p5m.R;
import com.example.buma_p5m.models.Absensi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private TextView tvtidur,tvabsen,tvbangun,tvtanggal,tvalamat,tvketerangan,tvtema,tvpemateri
            ,tvnama,tvnik;
    private static final String DATA = "EXTRA_DATA";
    private String terima,nama,nik;
    private SupportMapFragment supportMapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        terima = getIntent().getStringExtra("data");
        nik = getIntent().getStringExtra("extranik");
        init();
        //Map Setting
        supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.framemaps);
        //permission
        perizinan();


    }

    private void perizinan() {
        if (ActivityCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            ShowMap();
        }
        else {
            ActivityCompat.requestPermissions(DetailActivity.this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    private void init() {
        tvnik = findViewById(R.id.nikDetail);
        tvnama = findViewById(R.id.nameDetail);
        tvtidur = findViewById(R.id.tidurdetail);
        tvabsen = findViewById(R.id.jamdetail);
        tvbangun = findViewById(R.id.bangundetail);
        tvtanggal = findViewById(R.id.tanggaldetail);
        tvalamat = findViewById(R.id.alamatdetail);
        tvketerangan= findViewById(R.id.keterangandetail);
        tvtema = findViewById(R.id.temadetail);
        tvpemateri = findViewById(R.id.materidetail);
        tvnik.setText(nik);
        Intent intent = getIntent();
        Absensi data = intent.getParcelableExtra(DATA);
        if (data != null) {
            tvnama.setText(data.getNama());
            tvtidur.setText(data.getJam_Tidur());
            tvabsen.setText(data.getJam_Absensi());
            tvbangun.setText(data.getJam_Bangun());
            tvtanggal.setText(data.getTanggal());
            tvalamat.setText(data.getLokasi());
            tvketerangan.setText(data.getKeterangan());
            tvtema.setText(data.getTema());
            tvpemateri.setText(data.getPemateri());
            nama = data.getNama();
        }
    }
    private void ShowMap() {
        supportMapFragment.getMapAsync(googleMap -> {
            String location = tvalamat.getText().toString();
            Double latitude = null;
            Double longitude = null;

            Geocoder geocoder = new Geocoder(DetailActivity.this);
            try {
                List<Address> addresses = geocoder.getFromLocationName(location,1);
                if (addresses.size()>0){
                    latitude = addresses.get(0).getLatitude();
                    longitude = addresses.get(0).getLongitude();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (latitude!=null && longitude!=null){
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions options = new MarkerOptions().position(latLng)
                        .title("Lokasi Absen "+tvnama.getText().toString());
                CameraPosition posisi = CameraPosition.builder().target(latLng).zoom(18).bearing(0).tilt(45).build();
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(posisi));
                googleMap.addMarker(options);
            }

        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==44){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                ShowMap();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (terima.equals("admin")){
            Intent intent = new Intent(getApplicationContext(),ListAdmin.class);
            intent.putExtra("extranama",nama);
            intent.putExtra("extranik",nik);
            startActivity(intent);
            finishAffinity();
            finish();
        }
        else {
            Intent intent = new Intent(getApplicationContext(),ListAbsensi.class);
            startActivity(intent);
            finishAffinity();
            finish();
        }
    }

}
