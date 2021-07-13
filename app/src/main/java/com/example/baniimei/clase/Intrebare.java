package com.example.baniimei.clase;

import java.io.Serializable;
import java.util.List;

public class Intrebare implements Serializable {

    private int id = 0;
    private String intrebare;
    private String raspunsCorect;
    private List<String> raspunsuri;
    private String indiciu;
    private int idCapitol;

    public Intrebare(int id, String intrebare, String raspunsCorect, List<String> raspunsuri, String indiciu, int idCapitol) {
        this.id = id;
        this.intrebare = intrebare;
        this.raspunsCorect = raspunsCorect;
        this.raspunsuri = raspunsuri;
        this.indiciu = indiciu;
        this.idCapitol = idCapitol;
    }

    public int getId() {
        return id;
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

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCapitol() {
        return idCapitol;
    }

    public void setIdCapitol(int idCapitol) {
        this.idCapitol = idCapitol;
    }

}
