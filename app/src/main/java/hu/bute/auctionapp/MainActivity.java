package hu.bute.auctionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hu.bute.auctionapp.activities.LoginActivity;
import hu.bute.auctionapp.activities.ProductsActivity;
import hu.bute.auctionapp.activities.StoresActivity;


public class MainActivity extends Activity {

    private static final int REQUEST_LOGIN = 9746;
    AuctionApplication app;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN: {
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
                break;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (AuctionApplication) getApplication();
        if (!app.hasUser()) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
        }

        View storesButton = findViewById(R.id.main_stores);
        View searchButton = findViewById(R.id.main_search);
        View productsButton = findViewById(R.id.main_products);
        View favoritesButton = findViewById(R.id.main_favorites);
        View uploadAdButton = findViewById(R.id.main_upload_ad);


        storesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StoresActivity.class));
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, StoresActivity.class));
            }
        });
        productsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProductsActivity.class));
            }
        });
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, StoresActivity.class));
            }
        });
        uploadAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(MainActivity.this, StoresActivity.class));
            }
        });

    }
}
