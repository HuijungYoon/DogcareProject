package yoonhuijung.dogcareproject.get;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Getchatlists {
    @SerializedName("sender_chat")
    @Expose
    private String sender_chat;
    @SerializedName("msg_chat")
    @Expose
    private String msg_chat;
    @SerializedName("sender_time")
    @Expose
    private String sender_time;
    //수신함이미지
    @SerializedName("image")
    @Expose
    private String sender_image;

    public String getSender_image() {
        return sender_image;
    }

    public void setSender_image(String sender_image) {
        this.sender_image = sender_image;
    }



    public Getchatlists(String sender_image, String sender_chat, String msg_chat, String sender_time) {
        this.sender_image = sender_image;
        this.sender_chat = sender_chat;
        this.msg_chat = msg_chat;
        this.sender_time = sender_time;
    }

    public String getSender_chat() {
        return sender_chat;
    }

    public void setSender_chat(String sender_chat) {
        this.sender_chat = sender_chat;
    }

    public String getMsg_chat() {
        return msg_chat;
    }

    public void setMsg_chat(String msg_chat) {
        this.msg_chat = msg_chat;
    }

    public String getSender_time() {
        return sender_time;
    }

    public void setSender_time(String sender_time) {
        this.sender_time = sender_time;
    }
}
