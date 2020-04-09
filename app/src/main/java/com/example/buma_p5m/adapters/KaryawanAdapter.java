package com.example.buma_p5m.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buma_p5m.R;
import com.example.buma_p5m.activities.DetailActivity;
import com.example.buma_p5m.models.Karyawan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class KaryawanAdapter extends RecyclerView.Adapter<KaryawanAdapter.KaryawanViewHolder> {

    private Context mCtx;
    private ArrayList<Karyawan> userList;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(USERS);
    private static final String USERS = "Data Karyawan";
    public KaryawanAdapter(Context mCtx, ArrayList<Karyawan> userList) {
        this.mCtx = mCtx;
        this.userList = userList;
    }

    @NonNull
    @Override
    public KaryawanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mCtx).inflate(R.layout.row_karyawan, parent, false);
        return new KaryawanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KaryawanViewHolder holder, int position) {
        final Karyawan data = userList.get(position);
        holder.namakaryawan.setText(data.nama);
        holder.nikkaryawan.setText(data.nik);

        if (holder.namakaryawan.getText().equals("Admin")){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        //pindah activity
        holder.itemView.setOnClickListener(v -> {
            String extranama = data.nama;
            String extranik = data.nik;

            Intent detail = new Intent(mCtx, DetailActivity.class);
            detail.putExtra("extranama",extranama);
            detail.putExtra("extranik",extranik);
            detail.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            mCtx.startActivity(detail);

        });
        holder.itemView.setOnLongClickListener(v -> {

            String extranama = data.nama;
            String extranik = data.nik;

            AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
            String[] pilihan = {"Edit","Hapus"};
            builder.setItems(pilihan,(dialog, which) -> {
                if (which == 0){
                    final Dialog dialoEdit = new Dialog(mCtx);
                    dialoEdit.setContentView(R.layout.alert_dialog_edit);
                    dialoEdit.setCanceledOnTouchOutside(false);
                    Objects.requireNonNull(dialoEdit.getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT
                            , WindowManager.LayoutParams.WRAP_CONTENT);
                    dialoEdit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialoEdit.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                    dialoEdit.show();
                    EditText editTextNama,editTextNik;
                    editTextNama = dialoEdit.findViewById(R.id.editNama);
                    editTextNik = dialoEdit.findViewById(R.id.editNik);

                    editTextNama.setText(extranama);
                    editTextNik.setText(extranik);

                    Button buttonBack = dialoEdit.findViewById(R.id.canceledit);
                    buttonBack.setOnClickListener(v1 -> dialoEdit.dismiss());
                    Button buttonSubmit = dialoEdit.findViewById(R.id.submitedit);
                    buttonSubmit.setOnClickListener(v12 -> {
                        String kirimnama = editTextNama.getText().toString();
                        String submitnik = editTextNik.getText().toString();
                        Query query = databaseReference.orderByChild("nama").equalTo(extranama);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds :dataSnapshot.getChildren()){
                                    //update Data
                                    ds.getRef().child("nama").setValue(kirimnama);
                                    ds.getRef().child("nik").setValue(submitnik);
                                }
                                Toasty.success(mCtx,"Data Berhasil di Update",Toasty.LENGTH_LONG).show();
                                dialoEdit.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toasty.error(mCtx,""+databaseError.getMessage(),Toasty.LENGTH_LONG).show();
                            }
                        });

                    });

                }

                if (which == 1){
                    AlertDialog.Builder builderHapus = new AlertDialog.Builder(mCtx);
                    builderHapus.setTitle("Menghapus Data Karyawan.!");
                    builderHapus.setIcon(R.mipmap.ic_apk);
                    builderHapus.setMessage("Apakah Anda Yakin Akan Menghapus Ini.?");
                    //set tombol positif
                    builderHapus.setPositiveButton("Iya", (dialog1, which1) -> {
                        Query query = databaseReference.orderByChild("nama").equalTo(extranama);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();//menghapus nilai yang sama di database
                                }
                               Toasty.success(mCtx,"Berhasil Menghapus Data",Toasty.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //jika terjadi kesalahan
                              Toasty.error(mCtx,databaseError.getMessage(),Toasty.LENGTH_LONG).show();

                            }
                        });
                    });
                    builderHapus.setNegativeButton("Tidak", (dialog2, which2) -> dialog2.dismiss());
                    builderHapus.create().show();

                }

            });
            builder.create().show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class KaryawanViewHolder extends RecyclerView.ViewHolder {
        TextView namakaryawan,nikkaryawan;

        KaryawanViewHolder(@NonNull View itemView) {
            super(itemView);
            namakaryawan = itemView.findViewById(R.id.dataNamaKaryawan);
            nikkaryawan = itemView.findViewById(R.id.dataNikKaryawan);
        }
    }
}
