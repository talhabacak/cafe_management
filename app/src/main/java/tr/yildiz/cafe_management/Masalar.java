package tr.yildiz.cafe_management;

import java.util.ArrayList;

public class Masalar {
    int ID;
    private ArrayList<MasaUrun> masa;

    public Masalar(int ID, ArrayList<MasaUrun> masa) {
        this.ID = ID;
        this.masa = masa;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ArrayList<MasaUrun> getMasa() {
        return masa;
    }

    public void setMasa(ArrayList<MasaUrun> masa) {
        this.masa = masa;
    }
}
