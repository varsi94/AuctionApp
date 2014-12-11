package hu.bute.auctionapp.fragments;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.activities.ProductDetailsActivity;
import hu.bute.auctionapp.adapters.ProductsAdapter;
import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.dynamiclist.DynamicListAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class ProductFragment extends ListFragment {
    private static final String KEY_TYPE = "type";
    private static final String KEY_FILTER = "filter";
    private int type;
    private String filter;
    private ProductsAdapter mAdapter;

    public static ProductFragment newInstance(int type, String filter) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        args.putString(KEY_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            type = savedInstanceState.getInt(KEY_TYPE);
            filter = savedInstanceState.getString(KEY_FILTER);
        } else if (getArguments() != null) {
            Bundle args = getArguments();
            type = args.getInt(KEY_TYPE);
            filter = args.getString(KEY_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new ProductsAdapter((AuctionApplication) getActivity().getApplication(), type, filter);
        DynamicListAdapter adapter = new DynamicListAdapter(getListView(), mAdapter);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ProductData data = mAdapter.getItem(position);
        Intent i = new Intent(getActivity(), ProductDetailsActivity.class);
        i.putExtra(ProductDetailsActivity.PRODUCT_KEY, data);
        startActivity(i);
        data.setClicks(data.getClicks() + 1);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TYPE, type);
        outState.putString(KEY_FILTER, filter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void refresh() {
        if (mAdapter != null) {
            mAdapter.refresh(filter);
        }
    }

    public void setFilter(String filter) {
        getArguments().putString(KEY_FILTER, filter);
        this.filter = filter;
    }
}
