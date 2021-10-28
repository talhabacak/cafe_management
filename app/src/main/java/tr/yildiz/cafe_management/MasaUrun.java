package tr.yildiz.cafe_management;

import java.util.ArrayList;

public class MasaUrun {
    private String isimUrun;
    private Double alis,satis,alisTutar,satisTutar;
    private Integer adet;

    public MasaUrun(String isimUrun, Double alis, Double satis, Double alisTutar, Double satisTutar, Integer adet) {
        this.isimUrun = isimUrun;
        this.alis = alis;
        this.satis = satis;
        this.alisTutar = alisTutar;
        this.satisTutar = satisTutar;
        this.adet = adet;
    }

    public MasaUrun() {

    }

    public String getIsimUrun() {
        return isimUrun;
    }

    public void setIsimUrun(String isimUrun) {
        this.isimUrun = isimUrun;
    }

    public Double getAlis() {
        return alis;
    }

    public void setAlis(Double alis) {
        this.alis = alis;
    }

    public Double getSatis() {
        return satis;
    }

    public void setSatis(Double satis) {
        this.satis = satis;
    }

    public Double getAlisTutar() {
        return alisTutar;
    }

    public void setAlisTutar(Double alisTutar) {
        this.alisTutar = alisTutar;
    }

    public Double getSatisTutar() {
        return satisTutar;
    }

    public void setSatisTutar(Double satisTutar) {
        this.satisTutar = satisTutar;
    }

    public Integer getAdet() {
        return adet;
    }

    public void setAdet(Integer adet) {
        this.adet = adet;
    }
}
