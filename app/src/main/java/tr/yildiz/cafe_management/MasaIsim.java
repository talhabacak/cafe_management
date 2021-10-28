package tr.yildiz.cafe_management;

public class MasaIsim {
    private String isim, isimKisisel, ac, son, sonFormat;
    private Integer mode;

    public MasaIsim(String isim, String isimKisisel, Integer mode, String ac, String son, String sonFormat) {
        this.isim = isim;
        this.isimKisisel = isimKisisel;
        this.mode = mode;
        this.ac = ac;
        this.son = son;
        this.sonFormat = sonFormat;
    }

    public MasaIsim(){

    }

    public String getSonFormat() {
        return sonFormat;
    }

    public void setSonFormat(String sonFormat) {
        this.sonFormat = sonFormat;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getSon() {
        return son;
    }

    public void setSon(String son) {
        this.son = son;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getIsimKisisel() {
        return isimKisisel;
    }

    public void setIsimKisisel(String isimKisisel) {
        this.isimKisisel = isimKisisel;
    }
}
