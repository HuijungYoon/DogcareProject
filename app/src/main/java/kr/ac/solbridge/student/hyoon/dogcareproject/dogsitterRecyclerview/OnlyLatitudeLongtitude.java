package kr.ac.solbridge.student.hyoon.dogcareproject.dogsitterRecyclerview;

public class OnlyLatitudeLongtitude {

    private String latitude;
    private String longtitude;

    public OnlyLatitudeLongtitude(String latitude, String longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

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
}
