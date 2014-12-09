package hu.bute.auctionapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import hu.bute.auctionapp.R;

public class PickLocationActivity extends FragmentActivity {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button searchBtn;
    private EditText searchET;
    public static final String LOCATION_INFO = "location_info";
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.
                    newLatLngZoom(pos, 15);
            mMap.animateCamera(cameraUpdate);
            MarkerOptions marker = new MarkerOptions();
            marker.position(pos);
            marker.title(getString(R.string.you_are_here));
            mMap.addMarker(marker);
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

    public static class LocationInfo implements Serializable {
        private String location;
        private double gpsLat;
        private double gpsLon;

        public LocationInfo(String location, double gpsLat, double gpsLon) {
            this.location = location;
            this.gpsLat = gpsLat;
            this.gpsLon = gpsLon;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public double getGpsLat() {
            return gpsLat;
        }

        public void setGpsLat(double gpsLat) {
            this.gpsLat = gpsLat;
        }

        public double getGpsLon() {
            return gpsLon;
        }

        public void setGpsLon(double gpsLon) {
            this.gpsLon = gpsLon;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        setUpMapIfNeeded();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);

        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchET = (EditText) findViewById(R.id.searchET);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Geocoder geocoder = new Geocoder(PickLocationActivity.this, Locale.ENGLISH);
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocationName(searchET.getText().toString(), 1);
                    if (addresses.size() == 0) {
                        Toast.makeText(PickLocationActivity.this, R.string.no_result_found, Toast.LENGTH_LONG).show();
                        return;
                    }
                    Address address = addresses.get(0);
                    LatLng pos = new LatLng(address.getLatitude(), address.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.
                            newLatLngZoom(pos, 15);
                    mMap.animateCamera(cameraUpdate);
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(pos);
                    mMap.addMarker(marker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mMap = mapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                confirmLocation(latLng);
            }
        });
    }

    private void confirmLocation(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_location);
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                    address = address + addresses.get(0).getAddressLine(i) + "\n";
                }
            }
        } catch (IOException e) {
            //ignore.
        }
        final String finalAddress = address.trim();
        builder.setMessage(getString(R.string.confirm_location_message, finalAddress));
        builder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LocationInfo info = new LocationInfo(finalAddress, latLng.latitude, latLng.longitude);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(LOCATION_INFO, info);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}
