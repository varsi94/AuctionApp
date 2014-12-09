package hu.bute.auctionapp.data;

/**
 * Áruházak adatainak tárolása.
 * Created by Andras on 2014.12.04..
 */
public class StoreData {
    private String name;
    private String pictureFileName;
    private String objectId;
    private int clicks;

    public StoreData(String name, int clicks) {
        this.name = name;
        this.clicks = clicks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureFileName() {
        return pictureFileName;
    }

    public void setPictureFileName(String pictureFileName) {
        this.pictureFileName = pictureFileName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
}
