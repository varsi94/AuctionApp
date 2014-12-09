package hu.bute.auctionapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.StoreData;

public class UploadStoreActivity extends Activity {
    private static final int PICK_LOCATION_REQUEST = 1894;
    private EditText storeNameET;
    private Button getLocationBtn;
    private Button pickImageBtn;
    private Button okBtn;
    private Button cancelBtn;
    private StoreData data;

    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.equals(getLocationBtn)) {
                pickLocation();
            } else if (view.equals(pickImageBtn)) {
                pickImage();
            } else if (view.equals(okBtn)) {
                setResult(RESULT_OK);
                finish();
            } else if (view.equals(cancelBtn)) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_store);
        data = new StoreData("", "", 0, 0, 0);
        //storeNameET = (EditText) findViewById(R.id.storeNameET);
        getLocationBtn = (Button) findViewById(R.id.getLocationBtn);
        getLocationBtn.setOnClickListener(btnClickListener);
        pickImageBtn = (Button) findViewById(R.id.pickImageBtn);
        pickImageBtn.setOnClickListener(btnClickListener);
        okBtn = (Button) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(btnClickListener);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(btnClickListener);
    }

    private void pickImage() {

    }

    public void pickLocation() {
        startActivityForResult(new Intent(this, PickLocationActivity.class), PICK_LOCATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOCATION_REQUEST && resultCode == RESULT_OK) {

        }
    }
}
