package tr.yildiz.cafe_management;

import java.util.ArrayList;

public class Masa {
    private String masaIsmi, isim;
    private int ID;
    private float tutar;
    private ArrayList<Urun> urun;

    public Masa(String masaIsmi, String isim, int id, float tutar, ArrayList<Urun> urun) {
        this.masaIsmi = masaIsmi;
        this.isim = isim;
        ID = id;
        this.tutar = tutar;
        this.urun = urun;
    }

    public String getMasaIsmi() {
        return masaIsmi;
    }

    public String getIsim() {
        return isim;
    }

    public int getID() {
        return ID;
    }

    public float getTutar() {
        return tutar;
    }

    public ArrayList<Urun> getUrun() {
        return urun;
    }

    public void setMasaIsmi(String masaIsmi) {
        this.masaIsmi = masaIsmi;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setTutar(float tutar) {
        this.tutar = tutar;
    }

    public void setUrun(ArrayList<Urun> urun) {
        this.urun = urun;
    }
}
