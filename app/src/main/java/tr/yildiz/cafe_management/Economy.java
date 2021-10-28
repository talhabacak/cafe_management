package tr.yildiz.cafe_management;

public class Economy {
    private String isim;
    private Double fiyat;

    public Economy(String isim, Double fiyat) {
        this.isim = isim;
        this.fiyat = fiyat;
    }
    public Economy(){

    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public Double getFiyat() {
        return fiyat;
    }

    public void setFiyat(Double fiyat) {
        this.fiyat = fiyat;
    }
}
