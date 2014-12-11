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
import hu.bute.auctionapp.dynamiclist.DynamicListAdapter;

/**
 * Osztály az áruházak megjelenítésére.
 * Created by Varsi on 2014.12.08..
 */
public class StoresAdapter extends BaseAdapter implements DynamicListAdapter.DynamicLoader {
    public static final int MOST_RECENT = 0;
    public static final int MOST_VIEWED = 1;
    public static final int FAVOURITES = 2;
    private static final int LOAD_COUNT = 2;
    private int type;
    private List<StoreData> storeDatas;
    private AuctionApplication app;
    private boolean wantsLoad;
    private Context context;
    private String filter;

    public StoresAdapter(Context context, int type, String filter) {
        this.type = type;
        this.filter = filter;
        this.app = (AuctionApplication) context.getApplicationContext();
        this.context = context;
        storeDatas = new ArrayList<StoreData>();
        this.wantsLoad = true;
    }

    public static View getStoreListItem(StoreData data, Context context, View view, ViewGroup viewGroup) {
        System.out.println("data = " + data);
        StoreViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_frag_stores_item, null);
            holder = new StoreViewHolder();
            holder.storeNameTV = (TextView) view.findViewById(R.id.storeNameET);
            holder.pictureIV = (ImageView) view.findViewById(R.id.iconPicImageView);
            holder.clicksTV = (TextView) view.findViewById(R.id.clicksTV);
            holder.typeTV = (TextView) view.findViewById(R.id.typeTV);
            view.setTag(holder);
        } else {
            holder = (StoreViewHolder) view.getTag();
        }
        System.out.println("view = " + view);
        holder.storeNameTV.setText(data.getName());
        holder.clicksTV.setText(context.getString(R.string.viewsLabel) + data.getClicks());
        holder.typeTV.setText(context.getString(R.string.typeLabel) + data.getType());
        if (data.getPictureFileName() == null) {
            holder.pictureIV.setImageResource(R.drawable.nophoto);
        } else {
            try {
                Bitmap image = BitmapFactory.decodeStream(context.openFileInput(data.getPictureFileName()));
                holder.pictureIV.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                holder.pictureIV.setImageResource(R.drawable.nophoto);
            }
        }
        return view;
    }

    private List<StoreData> loadFavourites() {
        List<StoreData> incoming = app.cloud.getFavoriteStores(storeDatas.size(), LOAD_COUNT, filter);
        return incoming;
    }

    private List<StoreData> loadMostViewed() {
        List<StoreData> incoming = app.cloud.getStoresByViewDirectly(storeDatas.size(), LOAD_COUNT, filter);
        return incoming;
    }

    private List<StoreData> loadMostRecent() {
        List<StoreData> incoming = app.cloud.getStoresByLastChangedDirectly(storeDatas.size(), LOAD_COUNT, filter);
        return incoming;
    }

    @Override
    public int getCount() {
        return storeDatas.size();
    }

    @Override
    public StoreData getItem(int i) {
        return storeDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void refresh(String filter) {
        this.filter = filter;
        storeDatas.clear();
        wantsLoad = true;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        StoreData data = storeDatas.get(position);
        return getStoreListItem(data, context, view, viewGroup);
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

    private static class StoreViewHolder {
        public TextView storeNameTV;
        public ImageView pictureIV;
        public TextView clicksTV;
        public TextView typeTV;
    }
}
