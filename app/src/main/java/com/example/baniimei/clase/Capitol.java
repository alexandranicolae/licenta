package com.example.baniimei.clase;

import java.io.Serializable;

public class Capitol implements Serializable {

    private int id;
    private final String numeCapitol;

    private boolean eActiv;
    private Dificultate dificultate;

    public Capitol(int id, String numeCapitol, Dificultate dificultate) {
        this.id = id;
        this.numeCapitol = numeCapitol;

        this.eActiv = false;
        this.dificultate = dificultate;
    }

    public String getNumeCapitol() {
        return numeCapitol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActiv() {
        return eActiv;
    }

    public void setActiv(boolean eActiv) {
        this.eActiv = eActiv;
    }

    public Dificultate getDificultate() {
        return dificultate;
    }

    public void setDificultate(Dificultate dificultate) {
        this.dificultate = dificultate;
    }
}
