package hu.bute.auctionapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import hu.bute.auctionapp.activities.LoginActivity;


public class MainActivity extends Activity {

    AuctionApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (AuctionApplication) getApplication();
        if(app.user==null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
