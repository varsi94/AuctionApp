package hu.bute.auctionapp.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.fragments.CategoryFragment;
import hu.bute.auctionapp.fragments.ProductFragment;

public class ProductsActivity extends Activity implements ActionBar.TabListener, CategoryFragment.OnCategorySelectedListener {
    public static final String KEY_FILTER = "filt_key";
    public static final String KEY_STORE_FILTER = "store_filt";
    private static final int UPLOAD_PRODUCT_REQUEST = 1520;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private String filter = null;
    private StoreData storeFilter = null;

    public static <T> int indexof(T[] array, T element) {
        if (element == null) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null)
                    return i;
            }
            return -1;
        } else {
            for (int i = 0; i < array.length; ++i) {
                if (element.equals(array[i]))
                    return i;
            }
        }
        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        filter = getIntent().getStringExtra(KEY_FILTER);
        storeFilter = (StoreData) getIntent().getSerializableExtra(KEY_STORE_FILTER);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        if (storeFilter == null) {
            if (filter == null) {
                actionBar.setTitle(R.string.products);
            } else {
                actionBar.setTitle(filter);
            }
        } else {
            actionBar.setTitle(storeFilter.getName());
        }
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        mViewPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.upload_product) {
            startActivityForResult(new Intent(ProductsActivity.this, UploadActivity.class), UPLOAD_PRODUCT_REQUEST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == UPLOAD_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            clearFragments();
        }
    }

    private void clearFragments() {
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            Fragment f = getFragment(i);
            if (f instanceof ProductFragment) {
                ProductFragment pf = (ProductFragment) f;
                pf.setFilter(filter, storeFilter);
            }
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    private Fragment getFragment(int pos) {
        return getFragmentManager().findFragmentByTag(getFragmentTag(pos));
    }

    private String getFragmentTag(int fragmentPosition) {
        return "android:switcher:" + mViewPager.getId() + ":" + fragmentPosition;
    }

    @Override
    public void onBackPressed() {
        if (filter != null) {
            filter = null;
            final ActionBar actionBar = getActionBar();
            if (storeFilter == null) {
                actionBar.setTitle(R.string.products);
            } else {
                actionBar.setTitle(storeFilter.getName());
            }
            mViewPager.setCurrentItem(0, true);
            clearFragments();
            Fragment f = getFragment(0);
            if (f instanceof CategoryFragment) {
                CategoryFragment catf = (CategoryFragment) f;
                catf.setSelection(-1);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void categorySelected(int index, String category) {
        this.filter = category;
        final ActionBar actionBar = getActionBar();
        if (storeFilter == null) {
            if (filter == null) {
                actionBar.setTitle(R.string.products);
            } else {
                actionBar.setTitle(filter);
            }
        } else {
            actionBar.setTitle(storeFilter.getName());
        }
        clearFragments();
        mViewPager.setCurrentItem(1, true);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        String[] tabs;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.product_tab_titles);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CategoryFragment.newInstance(R.array.product_types, indexof(getResources().getStringArray(R.array.product_types), filter));
                default:
                    return ProductFragment.newInstance(position - 1, filter, storeFilter);
            }
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

    }
}
