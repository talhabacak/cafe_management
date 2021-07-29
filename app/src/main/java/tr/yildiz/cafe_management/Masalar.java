package tr.yildiz.cafe_management;

import java.util.ArrayList;

public class Masalar {
    int ID;
    private ArrayList<Masa> masa;

    public Masalar(int ID, ArrayList<Masa> masa) {
        this.ID = ID;
        this.masa = masa;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ArrayList<Masa> getMasa() {
        return masa;
    }

    public void setMasa(ArrayList<Masa> masa) {
        this.masa = masa;
    }
}
