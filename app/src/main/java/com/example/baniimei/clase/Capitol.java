package com.example.baniimei.clase;

import java.io.Serializable;
import java.util.ArrayList;

public class Capitol implements Serializable {

    private int id;
    private String numeCapitol;
    private boolean eActiv;
    private static int nrActive;

    public Capitol(int id, String numeCapitol) {
        this.id = id;
        this.numeCapitol = numeCapitol;
        this.eActiv = false;
        nrActive = 0;
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

    public void setNumeCapitol(String numeCapitol) {
        this.numeCapitol = numeCapitol;
    }

    public boolean isActiv() {
        return eActiv;
    }

    public void activeaza() {
        if (!this.isActiv()) {
            this.eActiv = true;
            nrActive++;
        }
    }

    public void setActiv(boolean eActiv) {
        this.eActiv = eActiv;
    }

    public static int getNrActive() {
        return nrActive;
    }

    public static void setNrActive(int nrActive) {
        Capitol.nrActive = nrActive;
    }
}
