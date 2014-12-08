package hu.bute.auctionapp.parsewrapper;

import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.data.UserData;

/**
 * Interfész a backend egységes kezelésére, ha esetleg a Parse-ot kicserélnénk.
 * Created by Varsi on 2014.12.04..
 */
public interface CloudHandler {
    public void getStoresByMostViewed(ResultCallback callback);

    public interface ResultCallback {
        public void onResult(Object result);
    }

    public void getUser(String objectid, ResultCallback callback);

    public void getUser(String username, String password, ResultCallback callback);

    public UserData getUserDirectly(String username, String passmd5);

    public void saveUser(UserData userData, ResultCallback callback);

    public void getStore(String objectid, ResultCallback callback);

    public void getStores(ResultCallback callback);

    public void saveStore(StoreData data, ResultCallback callback);

    public void getProduct(String objectid, ResultCallback callback);

    public void saveProduct(ProductData data);
}
