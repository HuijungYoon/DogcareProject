package kr.ac.solbridge.student.hyoon.dogcareproject;

import java.text.SimpleDateFormat;

public class ChattingListItem {
    private String sender;
    private String msg;
    private String image;


    public ChattingListItem(String image,String sender, String msg ) {
        this.image = image;
        this.sender = sender;
        this.msg = msg;

    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
