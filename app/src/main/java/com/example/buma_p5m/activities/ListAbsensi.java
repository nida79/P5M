package com.example.buma_p5m.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buma_p5m.R;
import com.example.buma_p5m.adapters.AbsensiAdapter;
import com.example.buma_p5m.models.Absensi;
import com.example.buma_p5m.utils.Session;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ListAbsensi extends AppCompatActivity {

    ImageView searchImage;
    EditText editText;
    String pencarian;
    TextView textViewNama, textViewEmail, textViewNik;
    RecyclerView recyclerView;
    DatabaseReference dbRefrence;
    Session session;
    Query query;
    AbsensiAdapter absensiAdapter;
    List<Absensi> absensilist = new ArrayList<>();
    private static final String PRESENSI = "Data Presensi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_absensi);

        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //actionbar(Tulisan diatas)
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Data Absensi");
//        actionBar.setCustomView(getResources().getColor(R.color.putih));
        actionBar.setDisplayHomeAsUpEnabled(true);
        searchImage = findViewById(R.id.gambarcari);
        editText = findViewById(R.id.cari);
        textViewNik = findViewById(R.id.presennik);
        textViewNama = findViewById(R.id.presennama);
        textViewEmail = findViewById(R.id.presenemail);
        session = new Session(this);

        //Firebase
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.keepSynced(true);
        dbRefrence = rootRef.child(PRESENSI);
        query = dbRefrence.orderByChild("name").equalTo(session.getSPNama());
        query.addListenerForSingleValueEvent(valueEventListener);
        absensiAdapter = new AbsensiAdapter(this,absensilist);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView = findViewById(R.id.rcvDataPresensi);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(absensiAdapter);

        //Profile
        textViewEmail.setText(session.getSPEmail());
        textViewNama.setText(session.getSPNama());
        textViewNik.setText(session.getSpNik());

        searchData();

    }

    private void searchData() {
        searchImage.setOnClickListener(v -> {
            pencarian = editText.getText().toString();
            Toasty.info(getApplicationContext(),"Hasil Pencarian",Toasty.LENGTH_SHORT).show();
            firebaseSearch(pencarian);
        });
    }

    private void firebaseSearch(String pencarian) {
        if (pencarian==null){

            query = dbRefrence.orderByChild("name").equalTo(session.getSPNama());
            query.addListenerForSingleValueEvent(valueEventListener);
        }else {
            query = dbRefrence.orderByChild("tanggal").startAt(pencarian).endAt(pencarian + "\uf8ff");
            query.addListenerForSingleValueEvent(valueEventListener);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (absensiAdapter!=null){
            absensiAdapter.notifyDataSetChanged();
        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            absensilist.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Absensi dataabsensi = snapshot.getValue(Absensi.class);
                    absensilist.add(dataabsensi);
                }
                absensiAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                absensiAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                absensiAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(ListAbsensi.this, HomeActivity.class);
        startActivity(intentHome);
        finish();
    }
}
