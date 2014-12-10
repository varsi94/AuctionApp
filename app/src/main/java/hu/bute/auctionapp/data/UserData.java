package hu.bute.auctionapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andras on 2014.12.04..
 */
public class UserData {
    private String name;
    private String passwordMD5;
    private String objectId;
    private List<String> favoriteProductIds;
    private List<String> favoriteStoreIds;

    public UserData(String name) {
        this.name = name;
    }

    public UserData(String name, String passwordMD5) {
        this.name = name;
        this.passwordMD5 = passwordMD5;
        favoriteProductIds = new ArrayList<>();
        favoriteStoreIds = new ArrayList<>();
    }

    public String getPasswordMD5() {
        return passwordMD5;
    }

    public void setPasswordMD5(String passwordMD5) {
        this.passwordMD5 = passwordMD5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public List<String> getFavoriteProductIds() {
        return favoriteProductIds;
    }

    public void setFavoriteProductIds(List<String> favoriteProductIds) {
        this.favoriteProductIds = favoriteProductIds;
    }

    public List<String> getFavoriteStoreIds() {
        return favoriteStoreIds;
    }

    public void setFavoriteStoreIds(List<String> favoriteStoreIds) {
        this.favoriteStoreIds = favoriteStoreIds;
    }
}
