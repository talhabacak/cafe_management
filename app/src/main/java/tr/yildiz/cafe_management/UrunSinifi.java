package tr.yildiz.cafe_management;

import java.util.ArrayList;
import java.util.List;

public class UrunSinifi {
    private ArrayList<String> urunSinif;

    public UrunSinifi(ArrayList<String> urunSinif) {
        this.urunSinif = urunSinif;
    }

    public ArrayList<String> getUrun() {
        return urunSinif;
    }

    public void setUrun(ArrayList<String> urunSinif) {
        this.urunSinif = urunSinif;
    }
}
