package hu.bute.auctionapp;

import android.app.Application;
import android.preference.PreferenceManager;

import com.parse.Parse;

import hu.bute.auctionapp.data.UserData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;
import hu.bute.auctionapp.parsewrapper.ParseHandler;

/**
 * Globális Application osztály. Fő feladata, hogy a Parse-ot felkonfigurálja.
 * Created by Varsi on 2014.11.25..
 */
public class AuctionApplication extends Application {
    public static final String APPLICATION_ID = "OHC2I2Y2qV7hNtFynv2LOY1p6zPVdlifueYl0IpK";
    public static final String CLIENT_KEY = "efh4GWeOsffY1pDNYqRvIv7YGtqe8zo6Nn3gJ3Pr";

    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PASS = "user_pass";

    public CloudHandler cloud;
    private UserData user;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        cloud = new ParseHandler(this);
        String name = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_USER_NAME, null);
        if (name != null) {
            String pass = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_USER_PASS, null);
            this.user = cloud.getUserDirectly(name, pass);
        }
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
        if (user != null) {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(KEY_USER_NAME, user.getName())
                    .putString(KEY_USER_PASS, user.getPasswordMD5())
                    .commit();
        } else {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().remove(KEY_USER_NAME).remove(KEY_USER_PASS)
                    .commit();
        }
    }

    public boolean hasUser() {
        return this.user != null;
    }

}
