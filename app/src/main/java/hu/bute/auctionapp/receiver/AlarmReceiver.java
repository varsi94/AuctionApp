package hu.bute.auctionapp.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.ProductData;

/**
 * Alarm esemény elkapása.
 * Created by Varsi on 2014.12.10..
 */
public class AlarmReceiver extends BroadcastReceiver {
    private Context lastContext;
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        AuctionApplication app = (AuctionApplication) lastContext.getApplicationContext();
                        ProductData data = app.cloud.getNearest(new LatLng(location.getLatitude(), location.getLongitude()));
                        Notification.Builder builder = new Notification.Builder(lastContext);
                        builder.setSmallIcon(R.drawable.ic_launcher);
                        builder.setContentTitle(lastContext.getString(R.string.nearest_product));
                        builder.setContentText(data.getName() + "\n" + data.getStore().getName() + "\n" + data.getAddress());
                        NotificationManager manager = (NotificationManager) lastContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(1, builder.getNotification());

                        AlarmManager alarmManager = (AlarmManager) lastContext.getSystemService(Context.ALARM_SERVICE);
                        PendingIntent intent = PendingIntent.getBroadcast(lastContext, 0, new Intent(lastContext, AlarmReceiver.class), 0);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 60 * 1000, intent);
                    } catch (Throwable t) {
                        System.out.println(t.getMessage());
                    }
                    return null;
                }
            };
            task.execute();
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
