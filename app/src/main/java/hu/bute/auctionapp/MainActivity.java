package hu.bute.auctionapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import hu.bute.auctionapp.activities.LoginActivity;
import hu.bute.auctionapp.activities.ProductsActivity;
import hu.bute.auctionapp.activities.SearchActivity;
import hu.bute.auctionapp.activities.StoresActivity;
import hu.bute.auctionapp.activities.UploadActivity;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.dynamiclist.DynamicListHandler;


public class MainActivity extends Activity {
    private static final int REQUEST_LOGIN = 9746;
    private AuctionApplication app;

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

        ListView list = (ListView) findViewById(R.id.main_list);
        loadhandler = new DynamicListHandler(list, new MainListAdapter(this));
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


    private class MainListAdapter extends BaseAdapter implements DynamicListHandler.DynamicLoader {
        List<String> titles = new ArrayList<String>();
        List<Object> items = new ArrayList<Object>();
        private Context context;

        public MainListAdapter(Context context) {
            this.context = context;
            for (String s : context.getResources().getStringArray(R.array.store_types)) {
                titles.add(s);
            }
        }

        @Override
        public int getCount() {
            return 1 + items.size() * 2;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0)
                return null;
            position -= 1;
            if (position % 2 == 0) {
                return titles.get(position / 2);
            }
            return items.get(position / 2);
        }

        @Override
        public int getViewTypeCount() {
            return 1 + 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return 2;//buttons

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
            if (position == 0) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_main_top_buttons, parent, false);
                }
                View storesButton = convertView.findViewById(R.id.main_stores);
                View searchButton = convertView.findViewById(R.id.main_search);
                View productsButton = convertView.findViewById(R.id.main_products);
                View uploadAdButton = convertView.findViewById(R.id.main_upload_ad);
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
                return convertView;
            }
            if ((position - 1) % 2 == 0) {
                final TitleViewHolder holder;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_main_title, parent, false);
                    holder = new TitleViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (TitleViewHolder) convertView.getTag();
                }
                final String category = (String) getItem(position);
                holder.text.setText(category + "");
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, StoresActivity.class);
                        i.putExtra(StoresActivity.KEY_FILTER, category);
                        startActivity(i);
                    }
                });
                return convertView;
            }
            final ContentViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_main_content, parent, false);
                holder = new ContentViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ContentViewHolder) convertView.getTag();
            }
            List<StoreData> stores = (List<StoreData>) getItem(position);
            int i = 0;
            for (; i < stores.size() && i < holder.holders.length; ++i) {
                ContentViewHolder.StoreViewHolder v = holder.holders[i];
                v.findy.setVisibility(View.VISIBLE);
                StoreData s = stores.get(i);
                if (s.hasPicture()) {
                    try {
                        Bitmap image = BitmapFactory.decodeStream(app.openFileInput(s.getPictureFileName()));
                        v.image.setImageBitmap(image);
                    } catch (FileNotFoundException e) {
                        v.image.setImageResource(R.drawable.nophoto);
                    }
                } else {
                    v.image.setImageResource(R.drawable.nophoto);
                }
                v.text.setText(s.getName());
            }
            for (; i < holder.holders.length; ++i) {
                holder.holders[i].findy.setVisibility(View.GONE);
            }
            return convertView;
        }

        @Override
        public boolean wantsToLoad() {
            return items.size() < titles.size();
        }

        @Override
        public Object doLoading() {
            int index = items.size();
            String category = titles.get(index);
            List<StoreData> stores = app.cloud.getMostPopularStoreDirectly(category, 3);
            if (stores.size() == 0) {
                titles.remove(index);
                return null;
            } else {

                return stores;
            }
        }

        @Override
        public void addLoaded(Object result) {
            if (result != null) {
                items.add(result);
            }
        }

        class TitleViewHolder {
            TextView text;
            View button;

            TitleViewHolder(View findy) {
                text = (TextView) findy.findViewById(R.id.list_main_title_text);
                button = findy.findViewById(R.id.list_main_title_button);
            }
        }

        class ContentViewHolder {

            StoreViewHolder[] holders = new StoreViewHolder[3];

            ContentViewHolder(View findy) {
                holders[0] = new StoreViewHolder(findy.findViewById(R.id.list_main_content_store_1));
                holders[1] = new StoreViewHolder(findy.findViewById(R.id.list_main_content_store_2));
                holders[2] = new StoreViewHolder(findy.findViewById(R.id.list_main_content_store_3));
            }

            class StoreViewHolder {
                View findy;
                ImageView image;
                TextView text;

                StoreViewHolder(View findy) {
                    this.findy = findy;
                    image = (ImageView) findy.findViewById(R.id.list_main_content_store_img);
                    text = (TextView) findy.findViewById(R.id.list_main_content_store_text);
                }
            }
        }

    }
}