package com.example.baniimei.clase;

import java.io.Serializable;

public class Informatie implements Serializable {

    private int id = 0;
    private String titluSubnivel;
    private String informatie;
    private String exemplu1;
    private String exemplu2;
    private int idCapitol;

    public Informatie(int id, String titluSubnivel, String informatie, String exemplu1, String exemplu2, int idCapitol) {
        this.id = id;
        this.titluSubnivel = titluSubnivel;
        this.informatie = informatie;
        this.exemplu1 = exemplu1;
        this.exemplu2 = exemplu2;
        this.idCapitol = idCapitol;
    }

    public String getTitluSubnivel() {
        return titluSubnivel;
    }

    public void setTitluSubnivel(String titluSubnivel) {
        this.titluSubnivel = titluSubnivel;
    }

    public String getInformatie() {
        return informatie;
    }

    public void setInformatie(String informatie) {
        this.informatie = informatie;
    }

    public String getExemplu1() {
        return exemplu1;
    }

    public void setExemplu1(String exemplu1) {
        this.exemplu1 = exemplu1;
    }

    public String getExemplu2() {
        return exemplu2;
    }

    public void setExemplu2(String exemplu2) {
        this.exemplu2 = exemplu2;
    }

    public int getIdCapitol() {
        return idCapitol;
    }

    public void setIdCapitol(int idCapitol) {
        this.idCapitol = idCapitol;
    }

}
