package hu.bute.auctionapp;

import android.app.Application;

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

    public CloudHandler cloud;
    public UserData user;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        cloud = new ParseHandler();
    }
}
