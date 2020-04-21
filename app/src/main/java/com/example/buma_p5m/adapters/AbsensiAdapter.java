package com.example.buma_p5m.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buma_p5m.R;
import com.example.buma_p5m.models.Absensi;

import java.util.ArrayList;

public class AbsensiAdapter extends RecyclerView.Adapter<AbsensiAdapter.AbsensiViewHolder> {

    private Context mCtx;
    private ArrayList<Absensi> absensilist;


    public AbsensiAdapter(Context mCtx, ArrayList<Absensi> absensilist) {
        this.mCtx = mCtx;
        this.absensilist = absensilist;
    }

    @NonNull
    @Override
    public AbsensiViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mCtx).inflate(R.layout.row_absensi, parent, false);
        return new AbsensiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsensiViewHolder holder,final int position) {
        final Absensi data = absensilist.get(position);
        holder.tvTanggal.setText(data.tanggal);
        holder.textViewNama.setText(data.name);
        holder.tvPresensi.setText(data.jam_Absensi);
        holder.tvTidur.setText(data.jam_Tidur);
        holder.tvBangun.setText(data.jam_Bangun);
        holder.tvKeterangan.setText(data.keterangan);
        
    }

    @Override
    public int getItemCount() {
        return absensilist.size();
    }


    static class AbsensiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal,tvPresensi,tvTidur,tvBangun,tvKeterangan, textViewNama;
        AbsensiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal= itemView.findViewById(R.id.dataTanggal);
            tvPresensi= itemView.findViewById(R.id.dataJam);
            tvTidur= itemView.findViewById(R.id.dataTidur);
            tvBangun= itemView.findViewById(R.id.dataBangun);
            tvKeterangan= itemView.findViewById(R.id.dataKeterangan);
            textViewNama = itemView.findViewById(R.id.dataNama);
        }
    }
}
