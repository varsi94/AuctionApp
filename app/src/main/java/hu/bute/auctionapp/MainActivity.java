package hu.bute.auctionapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bute.auctionapp.activities.LoginActivity;
import hu.bute.auctionapp.activities.ProductsActivity;
import hu.bute.auctionapp.activities.SearchActivity;
import hu.bute.auctionapp.activities.StoresActivity;
import hu.bute.auctionapp.activities.UploadActivity;
import hu.bute.auctionapp.dynamiclist.DynamicListHandler;


public class MainActivity extends Activity {
    private static final int REQUEST_LOGIN = 9746;
    private AuctionApplication app;
    private MainListAdapter adapter;

    private DynamicListHandler loadhandler;

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
        View uploadAdButton = findViewById(R.id.main_upload_ad);
        ListView list = (ListView) findViewById(R.id.main_list);
        adapter = new MainListAdapter(this);
        loadhandler = new DynamicListHandler(list, adapter, adapter);


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
        uploadAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.upload_store:
                break;
            case R.id.signOut:
                app.setUser(null);
                startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private static class MainListAdapter extends BaseAdapter implements DynamicListHandler.DynamicLoader {
        List<String> titles = new ArrayList<String>();
        List<Object> items = new ArrayList<Object>();
        private Context context;

        public MainListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            if (position % 2 == 0) {
                return titles.get(position / 2);
            }
            return items.get(position / 2);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position % 2 == 0) {
                return 0;//title type
            }
            return 1;//content
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text = new TextView(context);
            text.setText("Pos: " + items.get(position));
            return text;
        }

        @Override
        public boolean wantsToLoad() {
            return items.size() < 40;
        }

        @Override
        public void doLoading() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            items.add(items.size());
            items.add(items.size());
            items.add(items.size());
        }

    }
}