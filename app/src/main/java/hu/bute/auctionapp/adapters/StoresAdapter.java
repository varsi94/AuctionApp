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
    private Context context;

    public StoresAdapter(Context context, int type) {
        this.type = type;
        this.app = (AuctionApplication) context.getApplicationContext();
        this.context = context;
        storeDatas = new ArrayList<StoreData>();
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

    public void refresh() {
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
    }

    private static class ViewHolder {
        public TextView storeNameTV;
        public ImageView pictureIV;
        public TextView clicksTV;
        public TextView typeTV;
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
}
