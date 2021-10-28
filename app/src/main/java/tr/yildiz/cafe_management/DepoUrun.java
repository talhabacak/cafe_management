package tr.yildiz.cafe_management;

public class DepoUrun {
    private String alis,adet, satis,isim,adisyon;

    public DepoUrun(String isim,String alis, String satis, String adet,String  adisyon) {
        this.isim = isim;
        this.alis = alis;
        this.satis = satis;
        this.adet = adet;
        this.adisyon = adisyon;
    }

    public DepoUrun() {

    }

    public String getAdisyon() {
        return adisyon;
    }

    public void setAdisyon(String adisyon) {
        this.adisyon = adisyon;
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

    public String getIsim() { return isim; }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getAdet() {
        return adet;
    }

    public void setAdet(String adet) {
        this.adet = adet;
    }
}
