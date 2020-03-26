package com.example.buma_p5m.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buma_p5m.R;
import com.example.buma_p5m.models.Absensi;

import java.util.ArrayList;
import java.util.List;

public class AbsensiAdapter extends RecyclerView.Adapter<AbsensiAdapter.AbsensiViewHolder> implements Filterable {

    private Context mCtx;
    private List<Absensi> absensilist;
    private List<Absensi> searchitems;


    public AbsensiAdapter(Context mCtx, List<Absensi> absensilist) {
        this.mCtx = mCtx;
        this.absensilist = absensilist;
        searchitems = new ArrayList<>(absensilist);
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
        holder.tvPresensi.setText(data.jam_Presensi);
        holder.tvTidur.setText(data.jam_Tidur);
        holder.tvBangun.setText(data.jam_Bangun);
        holder.tvKeterangan.setText(data.keterangan);
    }

    @Override
    public int getItemCount() {
        return absensilist.size();
    }


    class AbsensiViewHolder extends RecyclerView.ViewHolder {
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

//    private Filter exampleFilter = new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            ArrayList<Absensi> filteredList = new ArrayList<>();
//
//            if (constraint == null || constraint.length() == 0) {
//                filteredList.addAll(searchitems);
//            } else {
//                String filterPattern = constraint.toString().toLowerCase().trim();
//
//                for (Absensi item : searchitems) {
//                    if (item.getTanggal().toLowerCase().contains(filterPattern)) {
//                        filteredList.add(item);
//                    }
//                }
//            }
//
//            FilterResults results = new FilterResults();
//            results.values = filteredList;
//
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            absensilist.clear();
//            absensilist.addAll((ArrayList) results.values);
//            notifyDataSetChanged();
//        }
//    };

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    searchitems = absensilist;
                } else {
                    List<Absensi> filteredList = new ArrayList<>();
                    for (Absensi row : absensilist) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.tanggal.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    absensilist = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = searchitems;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                searchitems = (ArrayList<Absensi>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
}
