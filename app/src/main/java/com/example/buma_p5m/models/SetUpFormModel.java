package com.example.buma_p5m.models;

public class SetUpFormModel {

    private String status;
    private String uid;
    private String tema;
    private String pemateri;

    public SetUpFormModel(String status, String uid, String tema, String pemateri) {
        this.status = status;
        this.uid = uid;
        this.tema = tema;
        this.pemateri = pemateri;
    }

    public SetUpFormModel(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
}
