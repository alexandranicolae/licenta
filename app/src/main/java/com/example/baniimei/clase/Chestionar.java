package com.example.baniimei.clase;

import java.io.Serializable;
import java.util.List;

public class Chestionar implements Serializable {

    private int id = 0;
    private String titluSubnivel;
    private String informatie;
    private String exemplu1;
    private String exemplu2;
    private String intrebare;
    private String raspunsCorect;
    private List<String> raspunsuri;
    private String indiciu;
    private int idCapitol;

    public Chestionar(int id, String titluSubnivel, String informatie, String exemplu1, String exemplu2, String intrebare, String raspunsCorect, List<String> raspunsuri, String indiciu, int idCapitol) {
        this.id=id;
        this.titluSubnivel = titluSubnivel;
        this.informatie = informatie;
        this.exemplu1 = exemplu1;
        this.exemplu2 = exemplu2;
        this.intrebare = intrebare;
        this.raspunsCorect = raspunsCorect;
        this.raspunsuri = raspunsuri;
        this.indiciu = indiciu;
        this.idCapitol=idCapitol;
    }

    public int getId() {
        return id;
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

    public String getIntrebare() {
        return intrebare;
    }

    public void setIntrebare(String intrebare) {
        this.intrebare = intrebare;
    }

    public String getRaspunsCorect() {
        return raspunsCorect;
    }

    public void setRaspunsCorect(String raspunsCorect) {
        this.raspunsCorect = raspunsCorect;
    }

    public List<String> getRaspunsuri() {
        return raspunsuri;
    }

    public void setRaspunsuri(List<String> raspunsuri) {
        this.raspunsuri = raspunsuri;
    }

    public String getIndiciu() {
        return indiciu;
    }

    public void setIndiciu(String indiciu) {
        this.indiciu = indiciu;
    }

    public void setId(int id) { this.id = id; }

    public int getIdCapitol() { return idCapitol; }

    public void setIdCapitol(int idCapitol) { this.idCapitol = idCapitol; }
}
