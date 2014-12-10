package hu.bute.auctionapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.dynamiclist.DynamicListHandler;

/**
 * Osztály az áruházak megjelenítésére.
 * Created by Varsi on 2014.12.08..
 */
public class StoresAdapter extends BaseAdapter implements DynamicListHandler.DynamicLoader {
    public static final int MOST_RECENT = 0;
    public static final int MOST_VIEWED = 1;
    public static final int FAVOURITES = 2;
    private static final int LOAD_COUNT = 2;
    private int type;
    private List<StoreData> storeDatas;
    private AuctionApplication app;
    private boolean wantsLoad;
    private Context context;

    public StoresAdapter(Context context, int type) {
        this.type = type;
        this.app = (AuctionApplication) context.getApplicationContext();
        this.context = context;
        storeDatas = new ArrayList<StoreData>();
        this.wantsLoad = true;
    }

    private List<StoreData> loadFavourites() {
        wantsLoad = false;
        return new ArrayList<StoreData>();
    }

    private List<StoreData> loadMostViewed() {
        List<StoreData> incoming = app.cloud.getStoresByViewDirectly(storeDatas.size(), LOAD_COUNT);
        return incoming;
    }

    private List<StoreData> loadMostRecent() {
        List<StoreData> incoming = app.cloud.getStoresByLastChangedDirectly(storeDatas.size(), LOAD_COUNT);
        return incoming;
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

    public void refresh() {
        storeDatas.clear();
        wantsLoad = true;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        StoreData data = storeDatas.get(i);
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.store_line, null);
            holder = new ViewHolder();
            holder.storeNameTV = (TextView) view.findViewById(R.id.storeNameET);
            holder.pictureIV = (ImageView) view.findViewById(R.id.iconPicImageView);
            holder.clicksTV = (TextView) view.findViewById(R.id.clicksTV);
            holder.typeTV = (TextView) view.findViewById(R.id.typeTV);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.storeNameTV.setText(data.getName());
        holder.clicksTV.setText(app.getString(R.string.viewsLabel) + data.getClicks());
        holder.typeTV.setText(app.getString(R.string.typeLabel) + data.getType());
        if (data.getPictureFileName() == null) {
            holder.pictureIV.setImageResource(R.drawable.nophoto);
        } else {
            try {
                Bitmap image = BitmapFactory.decodeStream(app.openFileInput(data.getPictureFileName()));
                holder.pictureIV.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                holder.pictureIV.setImageResource(R.drawable.nophoto);
            }
        }
        return view;
    }

    @Override
    public boolean wantsToLoad() {
        return wantsLoad;
    }

    @Override
    public Object doLoading() {
        switch (type) {
            case MOST_RECENT:
                return loadMostRecent();
            case MOST_VIEWED:
                return loadMostViewed();
            case FAVOURITES:
                return loadFavourites();
            default:
                throw new IllegalArgumentException("Invalid storelist type!");
        }
        //return null;
    }

    @Override
    public void addLoaded(Object result) {
        if (result != null) {
            List<StoreData> incoming = (List<StoreData>) result;
            wantsLoad = incoming.size() >= LOAD_COUNT;
            storeDatas.addAll(incoming);
        }
    }

    private static class ViewHolder {
        public TextView storeNameTV;
        public ImageView pictureIV;
        public TextView clicksTV;
        public TextView typeTV;
    }
}
