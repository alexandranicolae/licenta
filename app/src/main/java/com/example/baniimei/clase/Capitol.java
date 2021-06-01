package com.example.baniimei.clase;

import java.util.ArrayList;

public class Capitol {

    private int id;
    private String numeCapitol;
    private boolean eActiv;

    public Capitol(int id, String numeCapitol){
        this.id=id;
        this.numeCapitol = numeCapitol;
        this.eActiv=false;
    }

    public String getNumeCapitol() {
        return numeCapitol;
    }

    public boolean isActiv() {
        return eActiv;
    }

    public void setActiv(boolean eActiv) {
        this.eActiv = eActiv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
