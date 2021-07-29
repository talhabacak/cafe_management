package tr.yildiz.cafe_management;

import java.util.ArrayList;
import java.util.List;

public class UrunSinifi {
    private String isim;
    private int ID;
    private ArrayList<Urun> urun;

    public UrunSinifi(String isim, int ID, ArrayList<Urun> urun) {
        this.isim = isim;
        this.ID = ID;
        this.urun = urun;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ArrayList<Urun> getUrun() {
        return urun;
    }

    public void setUrun(ArrayList<Urun> urun) {
        this.urun = urun;
    }
}
