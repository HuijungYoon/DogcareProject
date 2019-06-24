package yoonhuijung.dogcareproject.get;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class  ResultModel {
    private String result;
    private String errMSG;
    private String SuccessMSG;
    private String json;
    @SerializedName("buyer")
    @Expose
    private String buyer;
    @SerializedName("price")
    @Expose
    private String price;

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getErrMSG() {
        return errMSG;
    }

    public String getSuccessMSG() {
        return SuccessMSG;
    }
    public String getResult() {
        return result;
    }







}
