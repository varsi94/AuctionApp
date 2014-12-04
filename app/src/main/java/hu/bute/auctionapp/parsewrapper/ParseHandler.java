package hu.bute.auctionapp.parsewrapper;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.data.UserData;

/**
 * A Parse backend-szolgáltatást használó cloud handler.
 * Created by Varsi on 2014.12.04..
 */
public class ParseHandler implements CloudHandler {
    private static final String USER_USERNAME = "username";
    private static final String USER_PASSWORD = "password";
    private static final String USER_CLASSNAME = "AuctionUser";
    private static final String STORE_CLASSNAME = "Stores";
    private static final String STORE_NAME = "name";
    private static final String STORE_ADDRESS = "address";
    private static final String STORE_GPS_LATITUDE = "gps_lat";
    private static final String STORE_GPS_LONGITUDE = "gps_lon";

    private UserData parseObjectToUser(ParseObject obj) {
        String userName = obj.getString(USER_USERNAME);
        String passwordMD5 = obj.getString(USER_PASSWORD);
        UserData result = new UserData(userName, passwordMD5);
        result.setObjectId(obj.getObjectId());
        return result;
    }

    private StoreData parseObjectToStore(ParseObject obj) {
        String name = obj.getString(STORE_NAME);
        String address = obj.getString(STORE_ADDRESS);
        double gpsLat = obj.getNumber(STORE_GPS_LATITUDE).doubleValue();
        double gpsLon = obj.getNumber(STORE_GPS_LONGITUDE).doubleValue();
        StoreData result = new StoreData(name, address, gpsLon, gpsLat);
        result.setObjectId(obj.getObjectId());
        return result;
    }

    @Override
    public void getUser(String objectid, final ResultCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
        query.getInBackground(objectid, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject result, ParseException e) {
                if (e == null) {
                    callback.onResult(parseObjectToStore(result));
                } else {
                    callback.onResult(null);
                }
            }
        });
    }

    public static String MD5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    @Override
    public void getUser(final String username, final String password, final ResultCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
        final String passwordMD5 = MD5(password);
        query.whereEqualTo(USER_USERNAME, username);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                System.out.println(e);
                if (e != null || parseObject == null) {
                    callback.onResult(null);
                } else {
                    if (parseObject.getString(USER_PASSWORD).equals(passwordMD5)) {
                        callback.onResult(parseObjectToUser(parseObject));
                    } else {
                        callback.onResult(null);
                    }
                }
            }
        });
    }

    @Override
    public void saveUser(final UserData userData, final ResultCallback callback) {
        if (userData.getObjectId() == null) {
            //regisztráció
            ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
            query.whereEqualTo(USER_USERNAME, userData.getName());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (parseObject == null) {
                        final ParseObject obj = ParseObject.create(USER_CLASSNAME);
                        final String passwordMD5 = MD5(userData.getPasswordMD5());
                        obj.put(USER_USERNAME, userData.getName());
                        obj.put(USER_PASSWORD, passwordMD5);
                        obj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    callback.onResult(parseObjectToUser(obj));
                                } else {
                                    callback.onResult(null);
                                }
                            }
                        });
                    } else {
                        callback.onResult(null);
                    }
                    }
            });
        } else {
            //módosítás
            ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
            query.getInBackground(userData.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (parseObject == null) {
                        callback.onResult(null);
                    } else {
                        parseObject.put(USER_USERNAME, userData.getName());
                        parseObject.put(USER_PASSWORD, userData.getPasswordMD5());
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                callback.onResult(userData);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void getStore(String objectid, final ResultCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(STORE_CLASSNAME);
        query.getInBackground(objectid, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    if (parseObject == null) {
                        callback.onResult(null);
                    } else {
                        callback.onResult(parseObjectToStore(parseObject));
                    }
                } else {
                    callback.onResult(null);
                }
            }
        });
    }

    @Override
    public void saveStore(StoreData data) {

    }

    @Override
    public void getProduct(String objectid, ResultCallback callback) {

    }

    @Override
    public void saveProduct(ProductData data) {

    }
}
