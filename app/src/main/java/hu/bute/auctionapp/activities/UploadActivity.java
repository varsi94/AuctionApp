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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

public class UploadActivity extends Activity {
    public static final String IMAGEPATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/tmp_image.jpg";
    private static final int REQUEST_PICK_LOCATION = 250;
    private static final int REQUEST_SEARCH_STORE = 251;
    private final int REQUEST_CAMERA_IMAGE = 101;
    private String[] currencyTypes;
    private ImageView ivDrawer;
    private String[] productTypes;

    private TextView locationET;
    private EditText productNameET;
    private EditText priceET;
    private Spinner currencySpinner;
    private Spinner categorySpinner;
    private DatePicker durationDP;
    private EditText propertiesET;
    private EditText commentET;
    private TextView storeText;
    private PickLocationActivity.LocationInfo locationInfo;
    private boolean hasPhoto;

    private StoreData chosenStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productTypes = getResources().getStringArray(R.array.product_types);
        currencyTypes = getResources().getStringArray(R.array.currency_types);
        setContentView(R.layout.activity_upload_product);

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

        View pickLocationBtn = findViewById(R.id.getLocationBtn);
        pickLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(UploadActivity.this, PickLocationActivity.class), REQUEST_PICK_LOCATION);
            }
        });

        locationET = (TextView) findViewById(R.id.addressTextView);

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
        durationDP = (DatePicker) findViewById(R.id.duration);
        propertiesET = (EditText) findViewById(R.id.properties);
        commentET = (EditText) findViewById(R.id.comment);
        storeText = (TextView) findViewById(R.id.storeTextView);
        hasPhoto = false;

        View searchButton = findViewById(R.id.upload_product_search_store);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("v = [" + v + "]");
                Intent intent = new Intent(UploadActivity.this, SearchActivity.class);
                intent.putExtra(SearchActivity.KEY_REQUEST_DATA, SearchActivity.REQUEST_DATA_STORE);
                intent.putExtra(SearchActivity.KEY_DISPLAY_DATA, SearchActivity.DISPLAY_STORE);
                startActivityForResult(intent, REQUEST_SEARCH_STORE);
            }
        });
    }

    private void uploadProduct() {
        String name = productNameET.getText().toString();
        if (name.equals("")) {
            productNameET.setError(getString(R.string.field_is_empty));
            productNameET.requestFocus();
            return;
        } else if (priceET.getText().toString().equals("")) {
            priceET.setError(getString(R.string.field_is_empty));
            priceET.requestFocus();
            return;
        }

        StoreData store = chosenStore;
        if (store == null) {
            storeText.setError(getString(R.string.chose_a_store));
            storeText.requestFocus();
            return;
        }

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
        data.setCategory((String) categorySpinner.getSelectedItem());
        if (hasPhoto) {
            data.setPictureFileName(IMAGEPATH);
        }

        AuctionApplication app = (AuctionApplication) getApplication();
        app.cloud.saveProduct(data, new CloudHandler.ResultCallback() {
            @Override
            public void onResult(Object result) {
                if (result.equals(true)) {
                    Toast.makeText(UploadActivity.this, getString(R.string.product_upload_successful), Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(UploadActivity.this, getString(R.string.product_upload_failed), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA_IMAGE: {
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
                break;
            }
            case REQUEST_PICK_LOCATION: {
                if (resultCode == RESULT_OK) {
                    PickLocationActivity.LocationInfo info =
                            (PickLocationActivity.LocationInfo) data.getSerializableExtra(PickLocationActivity.LOCATION_INFO);
                    locationET.setText(info.getLocation());
                    locationInfo = info;
                }
                break;
            }
            case REQUEST_SEARCH_STORE: {
                if (resultCode == RESULT_OK) {
                    chosenStore = (StoreData) data.getSerializableExtra(SearchActivity.KEY_RESULT_STORE);
                    storeText.setText(chosenStore.getName());
                    System.out.println("chosenStore = " + chosenStore);
                }
                break;
            }
        }
    }
}
