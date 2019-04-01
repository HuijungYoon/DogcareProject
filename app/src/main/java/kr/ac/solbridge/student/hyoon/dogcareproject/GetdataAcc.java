package kr.ac.solbridge.student.hyoon.dogcareproject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetdataAcc {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profile_img")
    @Expose
    private String profile_img;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("contents")
    @Expose
    private String contents;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("carrer_title")
    @Expose
    private String carrer_title;
    @SerializedName("carrer_contents")
    @Expose
    private String carrer_contents;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longtitude")
    @Expose
    private String longtitude;



    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCarrer_title() {
        return carrer_title;
    }

    public void setCarrer_title(String carrer_title) {
        this.carrer_title = carrer_title;
    }

    public String getCarrer_contents() {
        return carrer_contents;
    }

    public void setCarrer_contents(String carrer_contents) {
        this.carrer_contents = carrer_contents;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
