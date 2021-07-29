package tr.yildiz.cafe_management;

public class User {
    private String isim, soyisim, mail, sifre, mod;
    private int ID;

    public User(String isim, String soyisim, String mail, String sifre, String mod, int ID) {
        this.isim = isim;
        this.soyisim = soyisim;
        this.mail = mail;
        this.sifre = sifre;
        this.mod = mod;
        this.ID = ID;
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
