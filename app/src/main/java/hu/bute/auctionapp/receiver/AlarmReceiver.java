package hu.bute.auctionapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.data.ProductData;

/**
 * Alarm esemény elkapása.
 * Created by Varsi on 2014.12.10..
 */
public class AlarmReceiver extends BroadcastReceiver {
    private Context lastContext;
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            AuctionApplication app = (AuctionApplication) lastContext.getApplicationContext();
            ProductData data = app.cloud.getNearest(new LatLng(location.getLatitude(), location.getLongitude()));

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        lastContext = context;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null);
        }
    }
}
