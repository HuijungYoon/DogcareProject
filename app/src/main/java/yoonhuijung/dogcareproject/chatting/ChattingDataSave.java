package yoonhuijung.dogcareproject.chatting;

import io.realm.RealmObject;

public class ChattingDataSave extends RealmObject {
    private String sender;
    private String msg;

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
