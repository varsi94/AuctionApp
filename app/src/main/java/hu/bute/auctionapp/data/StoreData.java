package hu.bute.auctionapp.data;

/**
 * Áruházak adatainak tárolása.
 * Created by Andras on 2014.12.04..
 */
public class StoreData {
    private String name;
    private String address;
    private double gpsLongitude;
    private double gpsLatitude;
    private String pictureFileName;
    private String objectId;

    public StoreData(String name, String address, double gpsLongitude, double gpsLatitude) {
        this.name = name;
        this.address = address;
        this.gpsLongitude = gpsLongitude;
        this.gpsLatitude = gpsLatitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public double getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
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
}
