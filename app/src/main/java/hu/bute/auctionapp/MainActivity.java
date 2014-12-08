package hu.bute.auctionapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import hu.bute.auctionapp.activities.LoginActivity;
import hu.bute.auctionapp.activities.ProductsActivity;
import hu.bute.auctionapp.activities.SearchActivity;
import hu.bute.auctionapp.activities.StoresActivity;
import hu.bute.auctionapp.widgets.DynamicLoaderListView;
import hu.bute.auctionapp.activities.UploadActivity;


public class MainActivity extends Activity {
    private static class MainListAdapter extends BaseAdapter implements DynamicLoaderListView.OnLoadListener {
        private Context context;

        List<String> titles = new ArrayList<String>();
        List<Object> items = new ArrayList<Object>();

        public MainListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return titles.size() + items.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position + 1 == getCount() - 1) {
                return null;
            }
            if (position % 2 == 0) {
                return titles.get(position / 2);
            }
            return items.get(position / 2);
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getCount() - 1) {
                return 2;//loader
            }
            if (position % 2 == 0) {
                return 0;//title type
            }
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == getCount() - 1) {
                if (convertView == null) {
                    convertView = new ProgressBar(context);
                }
                return convertView;
            }
            return null;
        }


        @Override
        public boolean startLoad() {
            return false;
        }
    }

    private static final int REQUEST_LOGIN = 9746;

    private AuctionApplication app;
    private MainListAdapter adapter;

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
        DynamicLoaderListView list = (DynamicLoaderListView) findViewById(R.id.main_list);
        adapter = new MainListAdapter(this);
        list.setAdapter(adapter);
        list.setOnLoadListener(adapter);

        storesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StoresActivity.class));
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
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
                 startActivity(new Intent(MainActivity.this, UploadActivity.class));
            }
        });

    }
}