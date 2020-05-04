package com.example.buma_p5m.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Absensi implements Parcelable {

    private String nama,tanggal, jam_Absensi,jam_Bangun,jam_Tidur,keterangan,lokasi,tema,pemateri,koordinat;

    public Absensi(){}

    protected Absensi(Parcel in) {
        nama = in.readString();
        tanggal = in.readString();
        jam_Absensi = in.readString();
        jam_Bangun = in.readString();
        jam_Tidur = in.readString();
        keterangan = in.readString();
        lokasi = in.readString();
        tema = in.readString();
        pemateri = in.readString();
        koordinat = in.readString();
    }

    public static final Creator<Absensi> CREATOR = new Creator<Absensi>() {
        @Override
        public Absensi createFromParcel(Parcel in) {
            return new Absensi(in);
        }

        @Override
        public Absensi[] newArray(int size) {
            return new Absensi[size];
        }
    };

    public String getNama() {
        return nama;
    }

    public void setName(String nama) {
        this.nama = nama;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam_Absensi() {
        return jam_Absensi;
    }

    public void setJam_Absensi(String jam_Absensi) {
        this.jam_Absensi = jam_Absensi;
    }

    public String getJam_Bangun() {
        return jam_Bangun;
    }

    public void setJam_Bangun(String jam_Bangun) {
        this.jam_Bangun = jam_Bangun;
    }

    public String getJam_Tidur() {
        return jam_Tidur;
    }

    public void setJam_Tidur(String jam_Tidur) {
        this.jam_Tidur = jam_Tidur;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getPemateri() {
        return pemateri;
    }

    public void setPemateri(String pemateri) {
        this.pemateri = pemateri;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama);
        dest.writeString(tanggal);
        dest.writeString(jam_Absensi);
        dest.writeString(jam_Bangun);
        dest.writeString(jam_Tidur);
        dest.writeString(keterangan);
        dest.writeString(lokasi);
        dest.writeString(tema);
        dest.writeString(pemateri);
        dest.writeString(koordinat);
    }
}
