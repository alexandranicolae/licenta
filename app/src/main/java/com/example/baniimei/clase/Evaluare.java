package com.example.baniimei.clase;

public class Evaluare {

    private float rating;
    private String mesaj;

    public Evaluare(float rating, String mesaj) {
        this.rating = rating;
        this.mesaj = mesaj;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }
}
