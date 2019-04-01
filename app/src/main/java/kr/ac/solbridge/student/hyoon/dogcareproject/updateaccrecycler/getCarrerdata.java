package kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler;

public class getCarrerdata {

    String carrer_title;
    String carrer_contents;

    public getCarrerdata(String carrer_title, String carrer_contents) {
        this.carrer_title = carrer_title;
        this.carrer_contents = carrer_contents;
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
}
