package kr.ac.solbridge.student.hyoon.dogcareproject;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {

    @SerializedName("success")
    boolean success;
    @SerializedName("message")
    String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
