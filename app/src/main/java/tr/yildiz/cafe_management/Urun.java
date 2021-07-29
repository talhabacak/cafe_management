package tr.yildiz.cafe_management;

import java.util.ArrayList;

public class Urun {
    private String isim, birim;
    private int ID;
    private float alis, satis;

    public Urun(String isim, String birim, int ID, float alis, float satis) {
        this.isim = isim;
        this.birim = birim;
        this.ID = ID;
        this.alis = alis;
        this.satis = satis;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getBirim() {
        return birim;
    }

    public void setBirim(String birim) {
        this.birim = birim;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public float getAlis() {
        return alis;
    }

    public void setAlis(float alis) {
        this.alis = alis;
    }

    public float getSatis() {
        return satis;
    }

    public void setSatis(float satis) {
        this.satis = satis;
    }
}
