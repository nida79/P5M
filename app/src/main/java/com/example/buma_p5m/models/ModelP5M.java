package com.example.buma_p5m.models;

public class ModelP5M {

    private String Nama,Tanggal,Jam_Absensi,Departemen,Jam_Bangun,Jam_Tidur,Koordinat,Keterangan,Tema,Pemateri,Lokasi;

    public ModelP5M(String nama, String tanggal, String jam_Absensi, String departemen, String jam_Bangun, String jam_Tidur, String koordinat, String keterangan, String tema, String pemateri, String lokasi) {
        Nama = nama;
        Tanggal = tanggal;
        Jam_Absensi = jam_Absensi;
        Departemen = departemen;
        Jam_Bangun = jam_Bangun;
        Jam_Tidur = jam_Tidur;
        Koordinat = koordinat;
        Keterangan = keterangan;
        Tema = tema;
        Pemateri = pemateri;
        Lokasi = lokasi;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }

    public String getJam_Absensi() {
        return Jam_Absensi;
    }

    public void setJam_Absensi(String jam_Absensi) {
        Jam_Absensi = jam_Absensi;
    }

    public String getDepartemen() {
        return Departemen;
    }

    public void setDepartemen(String departemen) {
        Departemen = departemen;
    }

    public String getJam_Bangun() {
        return Jam_Bangun;
    }

    public void setJam_Bangun(String jam_Bangun) {
        Jam_Bangun = jam_Bangun;
    }

    public String getJam_Tidur() {
        return Jam_Tidur;
    }

    public void setJam_Tidur(String jam_Tidur) {
        Jam_Tidur = jam_Tidur;
    }

    public String getKoordinat() {
        return Koordinat;
    }

    public void setKoordinat(String koordinat) {
        Koordinat = koordinat;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }

    public String getTema() {
        return Tema;
    }

    public void setTema(String tema) {
        Tema = tema;
    }

    public String getPemateri() {
        return Pemateri;
    }

    public void setPemateri(String pemateri) {
        Pemateri = pemateri;
    }

    public String getLokasi() {
        return Lokasi;
    }

    public void setLokasi(String lokasi) {
        Lokasi = lokasi;
    }
}

