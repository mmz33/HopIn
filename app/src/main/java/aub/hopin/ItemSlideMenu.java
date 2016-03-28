package aub.hopin;

public class ItemSlideMenu {
    private int imageId;
    private String title;

    public ItemSlideMenu(int imageId) {
        this.imageId = imageId;
    }

    public ItemSlideMenu(String title) {
        this.title = title;
    }

    public ItemSlideMenu(int imageId, String title) {
        this.imageId = imageId;
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setImageId(int newImageId) {
        imageId = newImageId;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }
}
