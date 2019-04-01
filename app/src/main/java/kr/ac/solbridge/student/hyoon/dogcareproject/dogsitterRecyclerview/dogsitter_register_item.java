package kr.ac.solbridge.student.hyoon.dogcareproject.dogsitterRecyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import kr.ac.solbridge.student.hyoon.dogcareproject.R;

public class dogsitter_register_item {

    private String image_dogsitter; //신청확인이미지
    private String name_dogsitter; //신청확인이름
    private String price_dogsitter; //신청확인가격

    public dogsitter_register_item(String image_dogsitter, String name_dogsitter, String price_dogsitter) {
        this.image_dogsitter = image_dogsitter;
        this.name_dogsitter =  name_dogsitter;
        this.price_dogsitter = price_dogsitter;
    }

    public String getImage_dogsitter() {
        return image_dogsitter;
    }

    public void setImage_dogsitter(String image_dogsitter) {
        this.image_dogsitter = image_dogsitter;
    }

    public String getName_dogsitter() {
        return name_dogsitter;
    }

    public void setName_dogsitter(String name_dogsitter) {
        this.name_dogsitter = name_dogsitter;
    }

    public String getPrice_dogsitter() {
        return price_dogsitter;
    }

    public void setPrice_dogsitter(String price_dogsitter) {
        this.price_dogsitter = price_dogsitter;
    }



}
