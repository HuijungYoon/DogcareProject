package kr.ac.solbridge.student.hyoon.dogcareproject.chatting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class getchatdata {
    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("receiver")
    @Expose
    private String receiver;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("send_time")
    @Expose
    private String send_time;

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public getchatdata(String sender, String msg, String receiver,String send_time) {

        this.sender = sender;
        this.msg = msg;
        this.receiver = receiver;
        this.send_time = send_time;
    }
}
