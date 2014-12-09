package hu.bute.auctionapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

/**
 * Osztály a store-ok megjelenítéséhez egy spinnerben.
 * Created by Varsi on 2014.12.09..
 */
public class StoresAdapterForSpinner extends BaseAdapter {
    private Context context;
    private List<StoreData> data;

    public StoresAdapterForSpinner(Context context) {
        this.context = context;
        data = new ArrayList<StoreData>();
        download();
    }

    private void download() {
        AuctionApplication app = (AuctionApplication) context;
        app.cloud.getStoresWithoutImages(new CloudHandler.ResultCallback() {
            @Override
            public void onResult(Object result) {
                data = (List<StoreData>) result;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.store_line_spinner, null);
        }

        StoreData obj = data.get(position);
        TextView text = (TextView) convertView.findViewById(R.id.storeLineTV);
        text.setText(obj.getName());
        return convertView;
    }
}
