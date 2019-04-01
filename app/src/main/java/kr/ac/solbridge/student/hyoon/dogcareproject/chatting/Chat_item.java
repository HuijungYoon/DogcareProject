package kr.ac.solbridge.student.hyoon.dogcareproject.chatting;

public class Chat_item {

    public String messagechatting;
    public String nickname;
    public  boolean isMe;

    public Chat_item(String nickname,String messagechatting, boolean isMe) {
        this.nickname = nickname;
        this.messagechatting = messagechatting;
        this.isMe = isMe;
    }



    public String getMessagechatting() {
        return messagechatting;
    }

    public void setMessagechatting(String messagechatting) {
        this.messagechatting = messagechatting;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
