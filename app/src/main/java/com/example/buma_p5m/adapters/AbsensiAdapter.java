package com.example.buma_p5m.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buma_p5m.R;
import com.example.buma_p5m.models.Absensi;

import java.util.ArrayList;

public class AbsensiAdapter extends RecyclerView.Adapter<AbsensiAdapter.AbsensiViewHolder> {

    private Context mCtx;
    private ArrayList<Absensi> absensilist;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public AbsensiAdapter(Context mCtx, ArrayList<Absensi> absensilist) {
        this.mCtx = mCtx;
        this.absensilist = absensilist;
    }

    @NonNull
    @Override
    public AbsensiViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_absensi, parent, false);
        return new AbsensiViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsensiViewHolder holder, final int position) {
        final Absensi data = absensilist.get(position);
        holder.tvTanggal.setText(data.getTanggal());
        holder.tvPresensi.setText(data.getJam_Absensi());
        holder.tvTidur.setText(data.getJam_Tidur());
        holder.tvBangun.setText(data.getJam_Bangun());
        holder.tvKeterangan.setText(data.getKeterangan());

        
    }

    @Override
    public int getItemCount() {
        return absensilist.size();
    }


    static class AbsensiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal,tvPresensi,tvTidur,tvBangun,tvKeterangan;
        AbsensiViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            tvTanggal= itemView.findViewById(R.id.dataTanggal);
            tvPresensi= itemView.findViewById(R.id.dataJam);
            tvTidur= itemView.findViewById(R.id.dataTidur);
            tvBangun= itemView.findViewById(R.id.dataBangun);
            tvKeterangan= itemView.findViewById(R.id.dataKeterangan);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position1 = getAdapterPosition();
                    if (position1 != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position1);
                    }
                }
            });
        }
    }
}
