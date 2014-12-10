package hu.bute.auctionapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.customviews.FavoriteImageView;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

public class StoreDetailsActivity extends Activity {
    public static final String STORE_KEY = "store_key";
    private StoreData data;
    private TextView storeNameTV;
    private TextView typeTV;
    private ImageView previewIV;
    private FavoriteImageView setFavoriteImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);
        Intent i = getIntent();
        data = (StoreData) i.getSerializableExtra(STORE_KEY);
        if (data == null) {
            finish();
        }
        storeNameTV = (TextView) findViewById(R.id.storeNameTV);
        typeTV = (TextView) findViewById(R.id.typeTV);
        previewIV = (ImageView) findViewById(R.id.imagePreview);
        setFavoriteImageBtn = (FavoriteImageView) findViewById(R.id.setFavoriteImgBtn);
        setFavoriteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuctionApplication app = (AuctionApplication) getApplication();
                if (data.isFavorite()) {
                    app.cloud.removeFavoriteStore(app.getUser(), data.getObjectId());
                    app.getUser().getFavoriteStoreIds().remove(data.getObjectId());
                } else {
                    app.cloud.addFavoriteStore(app.getUser(), data.getObjectId());
                    app.getUser().getFavoriteStoreIds().add(data.getObjectId());
                }
                data.setFavorite(!data.isFavorite());
                setFavoriteImageBtn.setFavorite(data.isFavorite());
            }
        });

        showData();
        updateClicks();
        setFavoriteImageBtn.setFavorite(data.isFavorite());
    }

    private void updateClicks() {
        data.setClicks(data.getClicks() + 1);
        AuctionApplication app = (AuctionApplication) getApplicationContext();
        app.cloud.saveStore(data, new CloudHandler.ResultCallback() {
            @Override
            public void onResult(Object result) {

            }
        });
    }

    private void showData() {
        storeNameTV.setText(data.getName());
        typeTV.setText(data.getType());
        if (data.getPictureFileName() != null) {
            try {
                Bitmap b = BitmapFactory.decodeStream(openFileInput(data.getPictureFileName()));
                previewIV.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                previewIV.setImageResource(R.drawable.nophoto);
            }
        }
    }
}