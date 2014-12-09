package hu.bute.auctionapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.data.StoreTypes;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

public class UploadStoreActivity extends Activity {
    private static final int PICK_IMAGE = 1562;
    private ImageButton pickImageBtn;
    private ImageButton okBtn;
    private ImageButton cancelBtn;
    private ImageButton deleteImageBtn;
    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.equals(pickImageBtn)) {
                pickImage();
            } else if (view.equals(okBtn)) {
                uploadStore();
            } else if (view.equals(cancelBtn)) {
                setResult(RESULT_CANCELED);
                finish();
            } else if (view.equals(deleteImageBtn)) {
                deleteImage();
            }
        }
    };
    private Spinner storeTypeSpinner;
    private EditText storeNameET;
    private ImageView previewImage;
    private String pictureFileName;

    private void deleteImage() {
        previewImage.setImageResource(R.drawable.nophoto);
        pictureFileName = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opt);
            int imgWidth = opt.outWidth;

            int realWidth = 128;
            int scaleFactor = Math.round((float)imgWidth / (float)realWidth);
            opt.inSampleSize = scaleFactor;
            opt.inJustDecodeBounds = false;

            Bitmap img = BitmapFactory.decodeFile(filePath,opt);
            previewImage.setImageBitmap(img);
            pictureFileName = filePath;
        }
    }

    private void uploadStore() {
        if (storeNameET.getText().toString().equals("")) {
            storeNameET.setError(getString(R.string.field_is_empty));
            return;
        }

        Toast.makeText(this, R.string.uploading, Toast.LENGTH_LONG).show();
        String storeName = storeNameET.getText().toString();
        StoreData data = new StoreData(storeName, 0, (String) storeTypeSpinner.getSelectedItem());
        data.setPictureFileName(pictureFileName);
        AuctionApplication app = (AuctionApplication) getApplication();
        app.cloud.saveStore(data, new CloudHandler.ResultCallback() {
            @Override
            public void onResult(Object result) {
                if ((Boolean) result == false) {
                    Toast.makeText(UploadStoreActivity.this, R.string.store_uploading_failed, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UploadStoreActivity.this, R.string.store_uploading_successful, Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_store);
        pickImageBtn = (ImageButton) findViewById(R.id.pickImageBtn);
        pickImageBtn.setOnClickListener(btnClickListener);
        okBtn = (ImageButton) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(btnClickListener);
        cancelBtn = (ImageButton) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(btnClickListener);
        deleteImageBtn = (ImageButton) findViewById(R.id.deleteImageBtn);
        deleteImageBtn.setOnClickListener(btnClickListener);

        storeNameET = (EditText) findViewById(R.id.storeNameUploadET);
        previewImage = (ImageView) findViewById(R.id.imagePreview);
        storeTypeSpinner = (Spinner) findViewById(R.id.storeTypeSpinner);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, StoreTypes.getValues(this));
        storeTypeSpinner.setAdapter(adapter);
    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE);
    }
}
