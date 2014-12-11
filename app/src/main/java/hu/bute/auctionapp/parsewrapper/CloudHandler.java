package hu.bute.auctionapp.parsewrapper;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.data.UserData;

/**
 * Interfész a backend egységes kezelésére, ha esetleg a Parse-ot kicserélnénk.
 * Created by Varsi on 2014.12.04..
 */
public interface CloudHandler {
    public List<StoreData> findStoresDirectly(int skip, int limit, String keyword);

    public List<ProductData> findProductsDirectly(int skip, int limit, String keyword);

    public List<Object> findProdAndStoreDirectly(int skip, int limit, String keyword);

    public List<StoreData> getStoresByViewDirectly(int skip, int limit, String filter);

    public List<StoreData> getStoresByLastChangedDirectly(int skip, int limit, String filter);

    public List<ProductData> getProductsByViewDirectly(int skip, int limit, String filter);

    public List<ProductData> getProdcutsByLastChangedDirectly(int skip, int limit, String filter);

    public void getUser(String objectid, ResultCallback callback);

    public void getUser(String username, String password, ResultCallback callback);

    public UserData getUserDirectly(String username, String passmd5);

    public void saveUser(UserData userData, ResultCallback callback);

    public void getStore(String objectid, ResultCallback callback);

    public void getStores(ResultCallback callback);

    public void saveStore(StoreData data, ResultCallback callback);

    public void getProduct(String objectid, ResultCallback callback);

    public void saveProduct(ProductData data, ResultCallback callback);

    public void getStoresWithoutImages(ResultCallback callback);

    public List<StoreData> getMostPopularStoreDirectly(String category, int count);

    public void addFavoriteStore(UserData userData, String objectId);

    public void removeFavoriteStore(UserData userData, String objectId);

    public void addFavoriteProduct(UserData userData, String objectId);

    public void removeFavoriteProduct(UserData userData, String objectId);

    public List<StoreData> getFavoriteStores(int size, int loadCount, String filter);

    public List<ProductData> getFavoriteProducts(int skip, int limit, String filter);

    public ProductData getNearest(LatLng currPos);

    public interface ResultCallback {
        public void onResult(Object result);
    }
}
