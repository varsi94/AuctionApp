package hu.bute.auctionapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

public class UploadStoreActivity extends Activity {
    private ImageButton pickImageBtn;
    private ImageButton okBtn;
    private ImageButton cancelBtn;
    private EditText storeNameET;
    private String pictureFileName;

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
            }
        }
    };

    private void uploadStore() {
        if (storeNameET.getText().toString().equals("")) {
            storeNameET.setError(getString(R.string.field_is_empty));
            return;
        }

        Toast.makeText(this, R.string.uploading, Toast.LENGTH_LONG).show();
        String storeName = storeNameET.getText().toString();
        StoreData data = new StoreData(storeName, 0);
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

        storeNameET = (EditText) findViewById(R.id.storeNameUploadET);
    }

    private void pickImage() {

    }
}
