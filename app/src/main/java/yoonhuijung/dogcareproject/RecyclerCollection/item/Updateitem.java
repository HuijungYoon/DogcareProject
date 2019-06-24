package yoonhuijung.dogcareproject.RecyclerCollection.item;

public class Updateitem {

    private int image_acc; //이미지
    private String title; //제목
    private String contents; //내용

    public int getImage_acc() {
        return image_acc;
    }

    public void setImage_acc(int image_acc) {
        this.image_acc = image_acc;
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

    public Updateitem(int image_acc, String title, String contents) {

        this.image_acc = image_acc;
        this.title = title;
        this.contents = contents;
    }
}
