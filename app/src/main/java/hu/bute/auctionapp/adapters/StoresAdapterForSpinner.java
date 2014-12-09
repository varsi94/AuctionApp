package hu.bute.auctionapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

/**
 * Osztály a store-ok megjelenítéséhez egy spinnerben.
 * Created by Varsi on 2014.12.09..
 */
public class StoresAdapterForSpinner extends ArrayAdapter<StoreData> {
    public StoresAdapterForSpinner(Context context, int resource) {
        super(context, resource);
        download();
    }

    private void download() {
        AuctionApplication app = (AuctionApplication) getContext().getApplicationContext();
        app.cloud.getStoresWithoutImages(new CloudHandler.ResultCallback() {
            @Override
            public void onResult(Object result) {
                clear();
                addAll((List<StoreData>) result);
                notifyDataSetChanged();
            }
        });
    }
}
