package tr.yildiz.cafe_management;

public class User {
    private String isim, soyisim, mail, sifre,userId,yer,mod, tel;
    private Integer status;

    public User(String isim, String soyisim, String mail, String sifre, String mod, String userId, String yer, String tel, Integer status) {
        this.isim = isim;
        this.userId = userId;
        this.soyisim = soyisim;
        this.mail = mail;
        this.sifre = sifre;
        this.mod = mod;
        this.yer = yer;
        this.tel = tel;
        this.status = status;
    }

    public User(){

    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
