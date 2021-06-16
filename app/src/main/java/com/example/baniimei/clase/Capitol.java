package com.example.baniimei.clase;

import java.io.Serializable;

public class Capitol implements Serializable {

    private int id;
    private String numeCapitol;
    private boolean eActiv;

    private static int nrCapitoleActive;
    private Categorie categorie;

    private int nrChTotale;
    private int nrChCompletate;

    public Capitol(int id, String numeCapitol) {
        this.id = id;
        this.numeCapitol = numeCapitol;
        this.eActiv = false;
        nrCapitoleActive = 0;
        this.categorie = Categorie.CHESTIONAR;

        this.nrChTotale = 0;
        this.nrChCompletate = 0;
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
            nrCapitoleActive++;
        }
    }

    public void setActiv(boolean eActiv) {
        this.eActiv = eActiv;
    }

    public static int getNrCapitoleActive() {
        return nrCapitoleActive;
    }

    public static void setNrCapitoleActive(int nrCapitoleActive) {
        Capitol.nrCapitoleActive = nrCapitoleActive;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }


    public int getNrChTotale() {
        return nrChTotale;
    }

    public void setNrChTotale(int nrChTotale) {
        this.nrChTotale = nrChTotale;
    }

    public int getNrChCompletate() {
        return nrChCompletate;
    }

    public void setNrChCompletate(int nrChCompletate) {
        this.nrChCompletate = nrChCompletate;
    }
}
