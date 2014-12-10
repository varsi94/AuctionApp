package hu.bute.auctionapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.adapters.StoresAdapterForSpinner;
import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

public class UploadActivity extends Activity {
    public static final String IMAGEPATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/tmp_image.jpg";
    private static final int PICK_LOCATION_REQUEST = 250;
    private final int REQUEST_CAMERA_IMAGE = 101;
    private String[] currencyTypes;
    private ImageView ivDrawer;
    private String[] productTypes;

    private EditText locationET;
    private EditText productNameET;
    private EditText priceET;
    private Spinner currencySpinner;
    private Spinner categorySpinner;
    private Spinner storeNameSpinner;
    private DatePicker durationDP;
    private EditText propertiesET;
    private EditText commentET;
    private PickLocationActivity.LocationInfo locationInfo;
    private boolean hasPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productTypes = getResources().getStringArray(R.array.product_types);
        currencyTypes = getResources().getStringArray(R.array.currency_types);
        setContentView(R.layout.activity_upload);

        final ImageButton imgBtnPhoto =
                (ImageButton) findViewById(R.id.imgBtnPhoto);
        imgBtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imageFile = new File(IMAGEPATH);
                Uri imageFileUri = Uri.fromFile(imageFile);
                Intent cameraIntent =
                        new Intent(
                                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(
                        android.provider.MediaStore.EXTRA_OUTPUT,
                        imageFileUri);
                startActivityForResult(cameraIntent,
                        REQUEST_CAMERA_IMAGE);
            }
        });

        ivDrawer = (ImageView) findViewById(R.id.ivDrawer);
        Spinner tv = (Spinner) findViewById(R.id.store);

        ArrayAdapter<StoreData> storeAdapter = new StoresAdapterForSpinner(
                this, android.R.layout.simple_dropdown_item_1line);
        tv.setAdapter(storeAdapter);

        //Currency adapter
        Spinner crcy = (Spinner)
                findViewById(R.id.currency);
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, currencyTypes);
        crcy.setAdapter(currencyAdapter);

        //Category adapter
        Spinner ctgry = (Spinner) findViewById(R.id.category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, productTypes);
        ctgry.setAdapter(categoryAdapter);

        Button pickLocationBtn = (Button) findViewById(R.id.getLocationBtn);
        pickLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(UploadActivity.this, PickLocationActivity.class), PICK_LOCATION_REQUEST);
            }
        });

        locationET = (EditText) findViewById(R.id.addressET);


        View uploadButton = findViewById(R.id.btnUpload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProduct();
            }
        });

        ImageButton imageDeleteBtn = (ImageButton) findViewById(R.id.imgBtnDelete);
        imageDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(IMAGEPATH);
                if (f.exists()) {
                    f.delete();
                    ivDrawer.setImageResource(R.drawable.nophoto);
                    hasPhoto = false;
                }
            }
        });

        productNameET = (EditText) findViewById(R.id.productname);
        priceET = (EditText) findViewById(R.id.price);
        currencySpinner = (Spinner) findViewById(R.id.currency);
        categorySpinner = (Spinner) findViewById(R.id.category);
        storeNameSpinner = (Spinner) findViewById(R.id.store);
        durationDP = (DatePicker) findViewById(R.id.duration);
        propertiesET = (EditText) findViewById(R.id.properties);
        commentET = (EditText) findViewById(R.id.comment);
        hasPhoto = false;
    }

    private void uploadProduct() {
        if (productNameET.getText().toString().equals("")) {
            productNameET.setError(getString(R.string.field_is_empty));
            return;
        } else if (priceET.getText().toString().equals("")) {
            priceET.setError(getString(R.string.field_is_empty));
            return;
        }
        String name = productNameET.getText().toString();
        StoreData store = (StoreData) storeNameSpinner.getSelectedItem();
        double price = Double.parseDouble(priceET.getText().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.set(durationDP.getYear(), durationDP.getMonth(), durationDP.getDayOfMonth());
        Date durationEnd = calendar.getTime();
        ProductData data = new ProductData(name, store, price, durationEnd);
        if (locationInfo != null) {
            data.setAddress(locationInfo.getLocation());
            data.setGpsLat(locationInfo.getGpsLat());
            data.setGpsLon(locationInfo.getGpsLon());
        } else {
            data.setAddress("");
        }

        data.setComment(commentET.getText().toString());
        data.setProperties(propertiesET.getText().toString());
        data.setCurrency((String) currencySpinner.getSelectedItem());
        if (hasPhoto) {
            data.setPictureFileName(IMAGEPATH);
        }

        AuctionApplication app = (AuctionApplication) getApplication();
        app.cloud.saveProduct(data, new CloudHandler.ResultCallback() {
            @Override
            public void onResult(Object result) {
                if (result.equals(true)) {
                    Toast.makeText(UploadActivity.this, getString(R.string.product_upload_successful), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UploadActivity.this, getString(R.string.product_upload_failed), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_IMAGE) {
            if (resultCode == RESULT_OK) {
                try {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(IMAGEPATH, opt);
                    int imgWidth = opt.outWidth;

                    int realWidth = ivDrawer.getMeasuredWidth();
                    int scaleFactor = Math.round((float) imgWidth / (float) realWidth);
                    opt.inSampleSize = scaleFactor;
                    opt.inJustDecodeBounds = false;

                    Bitmap img = BitmapFactory.decodeFile(IMAGEPATH, opt);

                    ivDrawer.setImageBitmap(img);
                    hasPhoto = true;
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } else if (requestCode == PICK_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                PickLocationActivity.LocationInfo info =
                        (PickLocationActivity.LocationInfo) data.getSerializableExtra(PickLocationActivity.LOCATION_INFO);
                locationET.setText(info.getLocation());
                locationInfo = info;
            }
        }
    }
}
