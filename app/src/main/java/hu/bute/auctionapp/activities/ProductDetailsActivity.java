package hu.bute.auctionapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.ProductData;

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
        showDetails();
    }

    private void showDetails() {
        productNameTV.setText(data.getName());
        propertiesTV.setText(data.getProperties());
        SimpleDateFormat sdf = new SimpleDateFormat();
        durationEndTV.setText(sdf.format(data.getDurationEnd()));
        priceTV.setText(data.getPrice() + " " + data.getCurrency());
        locationTV.setText(data.getAddress());
        Toast.makeText(this, data.getPictureFileName(), Toast.LENGTH_LONG).show();
        if (data.getPictureFileName() != null) {
            Bitmap b = BitmapFactory.decodeFile(data.getPictureFileName());
            previewIV.setImageBitmap(b);
        }
        storeTV.setText(data.getStore().getName());
        commentsTV.setText(data.getComment());
        categoryTV.setText(data.getCategory());
    }

}
