package tr.yildiz.cafe_management;

import java.util.ArrayList;

public class Urun {
    private String isim, alis, satis, depo,sinif;

    public Urun(String isim,String sinif, String alis, String satis, String depo) {
        this.isim = isim;
        this.alis = alis;
        this.satis = satis;
        this.depo = depo;
        this.sinif = sinif;
    }

    public Urun(){

    }

    public String getSinif() {
        return sinif;
    }

    public void setSinif(String sinif) {
        this.sinif = sinif;
    }

    public String getDepo() {
        return depo;
    }

    public void setDepo(String depo) {
        this.depo = depo;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getAlis() {
        return alis;
    }

    public void setAlis(String alis) {
        this.alis = alis;
    }

    public String getSatis() {
        return satis;
    }

    public void setSatis(String satis) {
        this.satis = satis;
    }
}
