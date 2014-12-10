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
import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.dynamiclist.DynamicListHandler;

/**
 * Adapter a termékek megjelenítésére.
 * Created by Varsi on 2014.12.10..
 */
public class ProductsAdapter extends BaseAdapter implements DynamicListHandler.DynamicLoader {
    public static final int MOST_RECENT = 0;
    public static final int MOST_VIEWED = 1;
    public static final int FAVOURITES = 2;
    private static final int LOAD_COUNT = 2;
    private int type;
    private List<ProductData> products;
    private AuctionApplication app;
    private boolean wantsLoad = true;
    private Context context;

    public ProductsAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
        app = (AuctionApplication) context.getApplicationContext();
        products = new ArrayList<>();
    }

    private List<ProductData> loadFavourites() {
        wantsLoad = false;
        return new ArrayList<>();
    }

    private List<ProductData> loadMostViewed() {
        List<ProductData> incoming = app.cloud.getProductsByViewDirectly(products.size(), LOAD_COUNT);
        return incoming;
    }

    private List<ProductData> loadMostRecent() {
        List<ProductData> incoming = app.cloud.getProdcutsByLastChangedDirectly(products.size(), LOAD_COUNT);
        return incoming;
    }

    public void refresh() {
        wantsLoad = true;
        products.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductData data = products.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.fragment_product_listitem, null);
            holder = new ViewHolder();
            holder.pictureImageView = (ImageView) convertView.findViewById(R.id.iconPicImageView);
            holder.productNameTV = (TextView) convertView.findViewById(R.id.productNameTV);
            holder.storeNameTV = (TextView) convertView.findViewById(R.id.storeNameTV);
            holder.typeTV = (TextView) convertView.findViewById(R.id.typeTV);
            holder.clicksTV = (TextView) convertView.findViewById(R.id.clicksTV);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.productNameTV.setText(data.getName());
        holder.storeNameTV.setText(data.getStore().getName());
        holder.clicksTV.setText(app.getString(R.string.viewsLabel) + data.getClicks());
        holder.typeTV.setText(app.getString(R.string.typeLabel) + data.getCategory());
        if (data.getPictureFileName() == null) {
            holder.pictureImageView.setImageResource(R.drawable.nophoto);
        } else {
            try {
                Bitmap image = BitmapFactory.decodeStream(app.openFileInput(data.getPictureFileName()));
                holder.pictureImageView.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                holder.pictureImageView.setImageResource(R.drawable.nophoto);
            }
        }
        return convertView;
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
    }

    @Override
    public void addLoaded(Object result) {
        List<ProductData> incoming = (List<ProductData>) result;
        wantsLoad = incoming.size() >= LOAD_COUNT;
        products.addAll(incoming);
    }

    private static class ViewHolder {
        public ImageView pictureImageView;
        private TextView productNameTV;
        private TextView clicksTV;
        private TextView typeTV;
        private TextView storeNameTV;
    }
}
