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
 * Osztály az áruházak megjelenítésére.
 * Created by Varsi on 2014.12.08..
 */
public class StoresAdapter extends BaseAdapter{
    public static final int MOST_RECENT = 0;
    public static final int MOST_VIEWED = 1;
    public static final int FAVOURITES = 2;
    private int type;
    private List<StoreData> storeDatas;
    private AuctionApplication app;

    public StoresAdapter(Context context, int type) {
        this.type = type;
        this.app = (AuctionApplication)context;
        switch (type) {
            case MOST_RECENT:
                loadMostRecent();
                break;
            case MOST_VIEWED:
                loadMostViewed();
                break;
            case FAVOURITES:
                loadFavourites();
                break;
            default:
                throw new IllegalArgumentException("Invalid storelist type!");
        }

        storeDatas = new ArrayList<StoreData>();
    }

    private void loadFavourites() {
    }

    private void loadMostViewed() {
        app.cloud.getStoresByView(new CloudHandler.ResultCallback() {
            @Override
            public void onResult(Object result) {
                storeDatas = (List<StoreData>) result;
                notifyDataSetChanged();
            }
        });
    }

    private void loadMostRecent() {
        app.cloud.getStoresByLastChanged(new CloudHandler.ResultCallback() {
            @Override
            public void onResult(Object result) {
                storeDatas = (List<StoreData>) result;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        return storeDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return storeDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        public TextView storeNameTV;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        StoreData data = storeDatas.get(i);
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(app);
            view = inflater.inflate(R.layout.store_line, null);
            holder = new ViewHolder();
            holder.storeNameTV = (TextView) view.findViewById(R.id.storeName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.storeNameTV.setText(data.getName());
        return view;
    }
}
