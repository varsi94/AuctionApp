package hu.bute.auctionapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.adapters.ProductsAdapter;
import hu.bute.auctionapp.adapters.StoresAdapter;
import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.dynamiclist.DynamicListAdapter;

public class SearchActivity extends Activity {
    public static final String KEY_REQUEST_DATA = "key_reqdat";
    public static final String KEY_DISPLAY_DATA = "key_dispdat";
    public static final String KEY_RESULT_PROD = "key_resproddat";
    public static final String KEY_RESULT_STORE = "key_resstordat";
    public static final int DISPLAY_PRODUCT = 1;
    public static final int DISPLAY_STORE = 2;
    public static final int DISPLAY_BOTH = DISPLAY_PRODUCT | DISPLAY_STORE;
    public static final int REQUEST_DATA_NONE = 4;
    public static final int REQUEST_DATA_STORE = 8;
    public static final int REQUEST_DATA_PRODUCT = 16;
    public static final int REQUEST_DATA_BOTH = REQUEST_DATA_STORE | REQUEST_DATA_PRODUCT;
    private static final int LOAD_COUNT = 2;

    private EditText text;
    private ListView list;
    private DynamicListAdapter dynamicListAdapter;
    private SearchAdapter searchAdapter;
    private AuctionApplication app;

    private int request;
    private int display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (AuctionApplication) getApplication();

        request = getIntent().getIntExtra(KEY_REQUEST_DATA, REQUEST_DATA_NONE);
        display = getIntent().getIntExtra(KEY_DISPLAY_DATA, DISPLAY_PRODUCT | DISPLAY_STORE);

        setContentView(R.layout.activity_search);
        text = (EditText) findViewById(R.id.search_edittext);
        list = (ListView) findViewById(R.id.search_list_result);
        list.setAdapter(null);
        View okbtn = findViewById(R.id.search_button_ok);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch();
                    return true;
                }
                return false;
            }
        });

        searchAdapter = new SearchAdapter();
        dynamicListAdapter = new DynamicListAdapter(list, searchAdapter);
        list.setAdapter(dynamicListAdapter);
    }

    private void startSearch() {
        String keyword = text.getText().toString();
        searchAdapter.clear(keyword);
        dynamicListAdapter.requestLoad();
    }

    private class SearchAdapter extends BaseAdapter implements DynamicListAdapter.DynamicLoader {
        private List<Object> items = new ArrayList<>();
        private String keyword;
        private boolean wantsLoad;

        public void clear(String keyword) {
            this.keyword = keyword;
            wantsLoad = keyword != null;
            items.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            Object item = getItem(position);
            if (item instanceof StoreData) {
                return 0;
            } else if (item instanceof ProductData) {
                return 1;
            }
            throw new RuntimeException("invalid item? " + item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Object item = getItem(position);
            if (item instanceof StoreData) {
                convertView = StoresAdapter.getStoreListItem((StoreData) item, SearchActivity.this, convertView, parent);
            } else if (item instanceof ProductData) {
                convertView = ProductsAdapter.getProductListItem((ProductData) item, SearchActivity.this, convertView, parent);
            } else {
                throw new RuntimeException("invalid item? " + position);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (request != REQUEST_DATA_NONE) {
                        if (item instanceof StoreData && (request & REQUEST_DATA_STORE) != 0) {
                            Intent result = new Intent();
                            result.putExtra(KEY_RESULT_STORE, (java.io.Serializable) item);
                            setResult(RESULT_OK, result);
                        } else if (item instanceof ProductData && (request & REQUEST_DATA_PRODUCT) != 0) {
                            Intent result = new Intent();
                            result.putExtra(KEY_RESULT_PROD, (java.io.Serializable) item);
                            setResult(RESULT_OK, result);
                        }
                    } else {
                        if (item instanceof StoreData) {
                            Intent i = new Intent(SearchActivity.this, StoreDetailsActivity.class);
                            i.putExtra(StoreDetailsActivity.STORE_KEY, (java.io.Serializable) item);
                            startActivity(i);
                        } else if (item instanceof ProductData) {
                            Intent i = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                            i.putExtra(ProductDetailsActivity.PRODUCT_KEY, (java.io.Serializable) item);
                            startActivity(i);
                        }
                    }
                    finish();
                }
            });
            return convertView;
        }

        @Override
        public boolean wantsToLoad() {
            return wantsLoad;
        }

        @Override
        public Object doLoading() {
            switch (display) {
                case DISPLAY_BOTH: {
                    return app.cloud.findProdAndStoreDirectly(this.items.size(), LOAD_COUNT, keyword);
                }
                case DISPLAY_STORE: {
                    return app.cloud.findStoresDirectly(this.items.size(), LOAD_COUNT, keyword);
                }
                case DISPLAY_PRODUCT: {
                    return app.cloud.findProductsDirectly(this.items.size(), LOAD_COUNT, keyword);
                }
            }
            return null;
        }

        @Override
        public void addLoaded(Object result) {
            if (result != null) {
                List<Object> data = (List<Object>) result;
                wantsLoad = data.size() >= LOAD_COUNT;
                items.addAll(data);
            } else {
                wantsLoad = false;
            }
        }
    }

}
