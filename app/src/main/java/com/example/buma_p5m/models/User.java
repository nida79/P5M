package com.example.buma_p5m.models;

public class User {

    private String Nama;
    private String Nik;
    private String Email;
    private String Gender;
    private String Level;

    //Constructor

    public User(){}

    public User(String nama, String nik, String email, String gender, String level) {
        Nama = nama;
        Nik = nik;
        Email = email;
        Gender = gender;
        Level = level;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getNik() {
        return Nik;
    }

    public void setNik(String nik) {
        Nik = nik;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }
}
