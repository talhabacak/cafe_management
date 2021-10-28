package tr.yildiz.cafe_management;

public class Siparis {
    private String time, isim, adet, timeFormat,masasinif,masanum,iptal;

    public Siparis(String time, String timeFormat, String isim, String adet, String masasinif, String masanum, String iptal) {
        this.time = time;
        this.isim = isim;
        this.adet = adet;
        this.timeFormat = timeFormat;
        this.masasinif = masasinif;
        this.masanum = masanum;
        this.iptal = iptal;
    }

    public Siparis(){

    }

    public String getIptal() {
        return iptal;
    }

    public void setIptal(String iptal) {
        this.iptal = iptal;
    }

    public String getMasasinif() {
        return masasinif;
    }

    public void setMasasinif(String masasinif) {
        this.masasinif = masasinif;
    }

    public String getMasanum() {
        return masanum;
    }

    public void setMasanum(String masanum) {
        this.masanum = masanum;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsim() {
        return isim;
    }

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
