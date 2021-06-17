package com.example.baniimei.clase;

public class User implements Comparable {
    private String nume;
    private String scor;

    public User() {
    }

    public User(String nume) {
        this.nume = nume;
        this.scor = "15";
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getScor() {
        return scor;
    }

    public void setScor(String scor) {
        this.scor = scor;
    }

    @Override
    public int compareTo(Object o) {
        int scor = Integer.parseInt(((User) o).getScor());
        return Integer.parseInt(this.scor) - scor;
    }
}
