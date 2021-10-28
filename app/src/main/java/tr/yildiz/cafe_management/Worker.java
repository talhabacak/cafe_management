package tr.yildiz.cafe_management;

public class Worker {

    private String isim, soyisim, mail, sifre,userId,yer,mod, tel;
    private Integer status,sil,ode,depo,order;

    public Worker(String isim, String soyisim, String mail, String sifre, String mod, String userId, String yer, String tel, Integer status, Integer ode, Integer sil, Integer depo, Integer order) {
        this.isim = isim;
        this.userId = userId;
        this.soyisim = soyisim;
        this.mail = mail;
        this.sifre = sifre;
        this.mod = mod;
        this.yer = yer;
        this.tel = tel;
        this.status = status;
        this.sil = sil;
        this.ode = ode;
        this.depo = depo;
        this.order = order;
    }

    public Worker(){

    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getSoyisim() {
        return soyisim;
    }

    public void setSoyisim(String soyisim) {
        this.soyisim = soyisim;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getYer() {
        return yer;
    }

    public void setYer(String yer) {
        this.yer = yer;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSil() {
        return sil;
    }

    public void setSil(Integer sil) {
        this.sil = sil;
    }

    public Integer getOde() {
        return ode;
    }

    public void setOde(Integer ode) {
        this.ode = ode;
    }

    public Integer getDepo() {
        return depo;
    }

    public void setDepo(Integer depo) {
        this.depo = depo;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
