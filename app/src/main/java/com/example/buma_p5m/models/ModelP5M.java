package com.example.buma_p5m.models;

public class ModelP5M {

    private String Nama,Tanggal,Jam_Presensi,Departemen,Jam_Bangun,Jam_Tidur,Status,Keterangan,Tema,Pemateri,Lokasi;

    public ModelP5M(String nama, String tanggal, String jam_Presensi, String departemen, String jam_Bangun, String jam_Tidur, String status, String keterangan, String tema, String pemateri, String lokasi) {
        Nama = nama;
        Tanggal = tanggal;
        Jam_Presensi = jam_Presensi;
        Departemen = departemen;
        Jam_Bangun = jam_Bangun;
        Jam_Tidur = jam_Tidur;
        Status = status;
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

    public String getJam_Presensi() {
        return Jam_Presensi;
    }

    public void setJam_Presensi(String jam_Presensi) {
        Jam_Presensi = jam_Presensi;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
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

