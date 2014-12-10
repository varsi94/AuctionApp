package hu.bute.auctionapp.data;

import java.io.Serializable;

/**
 * Áruházak adatainak tárolása.
 * Created by Andras on 2014.12.04..
 */
public class StoreData implements Serializable {
    private String name;
    private String pictureFileName;
    private String objectId;
    private String type;
    private int clicks;
    private boolean favorite;

    public StoreData(String name, int clicks, String type) {
        this.name = name;
        this.clicks = clicks;
        this.type = type;
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

    public boolean hasPicture() {
        return pictureFileName != null;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
