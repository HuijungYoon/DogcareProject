package kr.ac.solbridge.student.hyoon.dogcareproject.dogsitterRecyclerview;

public class LatitudeLongtitudeDogSitterRegister {
    private String latitude;
    private String longtitude;
    private String image;
    private String name;
    private String price;

    public LatitudeLongtitudeDogSitterRegister(String image, String name, String price,String latitude, String longtitude) {
        this.image = image;
        this.name = name;
        this.price = price;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
