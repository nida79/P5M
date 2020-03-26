package com.example.buma_p5m.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buma_p5m.R;
import com.example.buma_p5m.activities.DetailActivity;
import com.example.buma_p5m.models.Karyawan;

import java.util.List;

public class KaryawanAdapter extends RecyclerView.Adapter<KaryawanAdapter.KaryawanViewHolder> {

    private Context mCtx;
    private List<Karyawan> userList;

    public KaryawanAdapter(Context mCtx, List<Karyawan> userList) {
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
