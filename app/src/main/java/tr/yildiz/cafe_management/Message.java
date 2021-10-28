package tr.yildiz.cafe_management;

public class Message {
    private  String who,text,time;
    private Boolean visibled;

    public Message(String who, String text, String time, Boolean visibled) {
        this.who = who;
        this.text = text;
        this.time = time;
        this.visibled = visibled;
    }

    public Message(){

    }

    public Boolean getVisibled() {
        return visibled;
    }

    public void setVisibled(Boolean visibled) {
        this.visibled = visibled;
    }

    public String getWho() {
        return who;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
