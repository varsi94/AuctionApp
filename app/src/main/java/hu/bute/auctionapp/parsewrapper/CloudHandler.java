package hu.bute.auctionapp.parsewrapper;

import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.data.UserData;

/**
 * Interfész a
 * Created by Varsi on 2014.12.04..
 */
public interface CloudHandler {
    public interface ResultCallback {
        public void onResult(Object result);
    }

    public void getUser(String objectid, ResultCallback callback);

    public void getUser(String username, String password, ResultCallback callback);

    public void saveUser(UserData data);

    public void getStore(String objectid, ResultCallback callback);

    public void saveStore(StoreData data);

    public void getProduct(String objectid, ResultCallback callback);

    public void saveProduct(ProductData data);
}
