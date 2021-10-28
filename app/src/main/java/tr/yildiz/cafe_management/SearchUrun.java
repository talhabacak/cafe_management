package tr.yildiz.cafe_management;

public class SearchUrun {
    private String urunIsim,sinifIsim;
    private Integer sinifPosition,urunPosition;

    public SearchUrun(Integer sinifPosition,Integer urunPosition,String sinifIsim, String urunIsim) {
        this.sinifPosition = sinifPosition;
        this.urunPosition = urunPosition;
        this.urunIsim = urunIsim;
        this.sinifIsim = sinifIsim;
    }

    public SearchUrun(){

    }

    public String getSinifIsim() {
        return sinifIsim;
    }

    public void setSinifIsim(String sinifIsim) {
        this.sinifIsim = sinifIsim;
    }

    public Integer getUrunPosition() {
        return urunPosition;
    }

    public void setUrunPosition(Integer urunPosition) {
        this.urunPosition = urunPosition;
    }

    public Integer getSinifPosition() {
        return sinifPosition;
    }

    public void setSinifPosition(Integer sinifPosition) {
        this.sinifPosition = sinifPosition;
    }

    public String getUrunIsim() {
        return urunIsim;
    }

    public void setUrunIsim(String urunIsim) {
        this.urunIsim = urunIsim;
    }
}
