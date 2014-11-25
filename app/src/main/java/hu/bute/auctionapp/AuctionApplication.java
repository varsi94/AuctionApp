package hu.bute.auctionapp;

import android.app.Application;

import com.parse.Parse;

/**
 * Globális Application osztály. Fő feladata, hogy a Parse-ot felkonfigurálja.
 * Created by Varsi on 2014.11.25..
 */
public class AuctionApplication extends Application {
    public static final String APPLICATION_ID = "OHC2I2Y2qV7hNtFynv2LOY1p6zPVdlifueYl0IpK";
    public static final String CLIENT_KEY = "efh4GWeOsffY1pDNYqRvIv7YGtqe8zo6Nn3gJ3Pr";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        Parse.setLogLevel(Parse.LOG_LEVEL_ERROR);
    }
}
