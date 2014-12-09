package hu.bute.auctionapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;

import hu.bute.auctionapp.R;
import hu.bute.auctionapp.adapters.StoresAdapterForSpinner;

public class UploadActivity extends Activity {

    public static final String IMAGEPATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/tmp_image.jpg";
    private final int REQUEST_CAMERA_IMAGE = 101;
    private ImageView ivDrawer;

    static final String[] currencyTypes = new String[] { "EUR", "USD", "HUF", "GBP"};
    static final String[] productTypes = new String[] {"Food", "Drink", "Clothes", "Electronic device",
            "Service", "Tool", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        View uploadButton = findViewById(R.id.imgBtnUpload);
        uploadButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

            }
        });

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

        StoresAdapterForSpinner storeAdapter = new StoresAdapterForSpinner(getApplicationContext());
        tv.setAdapter(storeAdapter);

        //Currency adapter
        Spinner crcy = (Spinner)
                findViewById(R.id.currency);
        ArrayAdapter<String> currencyAdapter =new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, currencyTypes);
        crcy.setAdapter(currencyAdapter);

        //Category adapter
        Spinner ctgry = (Spinner)findViewById(R.id.category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, productTypes);
        ctgry.setAdapter(categoryAdapter);
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
                    int imgHeight = opt.outHeight;

                    int realWidth = ivDrawer.getMeasuredWidth();
                    int scaleFactor = Math.round((float)imgWidth / (float)realWidth);
                    opt.inSampleSize = scaleFactor;
                    opt.inJustDecodeBounds = false;

                    Bitmap img = BitmapFactory.decodeFile(IMAGEPATH,opt);

                    ivDrawer.setImageBitmap(img);
                } catch (Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(this, "ERROR: " + t, Toast.LENGTH_LONG).show();
                }
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
