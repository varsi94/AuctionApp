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
    private static final String USER_CLASSNAME = "_User";

    @Override
    public void getUser(String objectid, final ResultCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
        query.getInBackground(objectid, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject result, ParseException e) {
                if (e == null) {
                    String userName = result.getString(USER_USERNAME);
                    callback.onResult(new UserData(userName));
                } else {
                    callback.onResult(null);
                }
            }
        });
    }

    @Override
    public void getUser(final String username, final String password, final ResultCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
        MessageDigest coder;
        try {
            coder = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            callback.onResult(null);
            return;
        }
        final String passwordMD5 = new String(coder.digest(password.getBytes()));
        query.whereEqualTo(USER_USERNAME, username);
        query.whereEqualTo(USER_PASSWORD, passwordMD5);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e != null) {
                    //itt történjen valami?
                } else {
                    if (parseObject == null) {
                        ParseObject obj = ParseObject.create(USER_CLASSNAME);
                        obj.put(USER_USERNAME, username);
                        obj.put(USER_PASSWORD, passwordMD5);
                        obj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    callback.onResult(new UserData(username));
                                } else {
                                    callback.onResult(null);
                                }
                            }
                        });
                    } else {
                        String userName = parseObject.getString(USER_USERNAME);
                        callback.onResult(new UserData(userName));
                    }
                }
            }
        });
    }

    @Override
    public void saveUser(UserData data) {
        ParseObject userObject = ParseObject.create(USER_CLASSNAME);
        userObject.put(USER_USERNAME, data.getName());
        userObject.put(USER_PASSWORD, data.getPasswordMD5());
        userObject.saveInBackground();
    }

    @Override
    public void getStore(String objectid, ResultCallback callback) {

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
