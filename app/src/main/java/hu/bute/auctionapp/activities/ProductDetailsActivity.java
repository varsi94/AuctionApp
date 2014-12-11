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
import java.text.DateFormat;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

public class ProductDetailsActivity extends Activity {
    public static final String PRODUCT_KEY = "product_key";
    private ProductData data;
    private TextView productNameTV;
    private TextView categoryTV;
    private TextView priceTV;
    private TextView durationEndTV;
    private TextView storeTV;
    private TextView locationTV;
    private TextView propertiesTV;
    private TextView commentsTV;
    private ImageView previewIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Intent intent = getIntent();
        data = (ProductData) intent.getSerializableExtra(PRODUCT_KEY);
        if (data == null) {
            finish();
        }

        productNameTV = (TextView) findViewById(R.id.productNameTV);
        durationEndTV = (TextView) findViewById(R.id.durationEndTV);
        priceTV = (TextView) findViewById(R.id.priceTV);
        locationTV = (TextView) findViewById(R.id.locationTV);
        commentsTV = (TextView) findViewById(R.id.commentsTV);
        previewIV = (ImageView) findViewById(R.id.imagePreview);
        categoryTV = (TextView) findViewById(R.id.categoryTV);
        storeTV = (TextView) findViewById(R.id.storeNameTV);
        propertiesTV = (TextView) findViewById(R.id.propertiesTV);
        View setFavoriteBtn = findViewById(R.id.setFavoriteImgBtn);
        setFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuctionApplication app = (AuctionApplication) getApplication();
                if (data.isFavorite()) {
                    app.cloud.removeFavoriteProduct(app.getUser(), data.getObjectId());
                    app.getUser().getFavoriteProductIds().remove(data.getObjectId());
                } else {
                    app.cloud.addFavoriteProduct(app.getUser(), data.getObjectId());
                    app.getUser().getFavoriteProductIds().add(data.getObjectId());
                }
                data.setFavorite(!data.isFavorite());
                v.setSelected(data.isFavorite());
            }
        });
        showDetails();
        updateClicks();
        System.out.println("data.isFavorite() = " + data.isFavorite());
        setFavoriteBtn.setSelected(data.isFavorite());
    }

    private void updateClicks() {
        data.setClicks(data.getClicks() + 1);
        AuctionApplication app = (AuctionApplication) getApplicationContext();
        app.cloud.saveProduct(data, new CloudHandler.ResultCallback() {
            @Override
            public void onResult(Object result) {

            }
        });
    }

    private void showDetails() {
        productNameTV.setText(data.getName());
        propertiesTV.setText(data.getProperties());
        DateFormat sdf = DateFormat.getDateInstance();
        durationEndTV.setText(sdf.format(data.getDurationEnd()));
        priceTV.setText(data.getPrice() + " " + data.getCurrency());
        locationTV.setText(data.getAddress());
        if (data.getPictureFileName() != null) {
            Bitmap b = null;
            try {
                b = BitmapFactory.decodeStream(openFileInput(data.getPictureFileName()));
                previewIV.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                previewIV.setImageResource(R.drawable.nophoto);
            }
        }
        storeTV.setText(data.getStore().getName());
        commentsTV.setText(data.getComment());
        categoryTV.setText(data.getCategory());
    }

}
