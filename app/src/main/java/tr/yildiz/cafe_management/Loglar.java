package tr.yildiz.cafe_management;

public class Loglar {
    private String who, screen, what, time;

    public Loglar(String who, String screen, String what, String time) {
        this.who = who;
        this.screen = screen;
        this.what = what;
        this.time = time;
    }

    public Loglar() {

    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
