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
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

/**
 * Osztály a store-ok megjelenítéséhez egy autocompletetextview-ban.
 * Created by Varsi on 2014.12.09..
 */
public class StoresAdapterForTV extends BaseAdapter {
    private Context context;
    private List<StoreData> data;

    public StoresAdapterForTV(Context context) {
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
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, null);
        }

        StoreData obj = data.get(position);
        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setText(obj.getName());
        return convertView;
    }
}
