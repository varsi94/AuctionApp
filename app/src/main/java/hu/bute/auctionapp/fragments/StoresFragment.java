package hu.bute.auctionapp.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.adapters.StoresAdapter;
import hu.bute.auctionapp.dynamiclist.DynamicListHandler;

public class StoresFragment extends ListFragment {
    private static final String KEY_TYPE = "type";
    private static final String KEY_FILTER = "filter";
    private int type;
    private String filter;
    private StoresAdapter mAdapter;

    public static StoresFragment newInstance(int type, String filter) {
        StoresFragment fragment = new StoresFragment();
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
        return inflater.inflate(R.layout.fragment_store_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new StoresAdapter((AuctionApplication) getActivity().getApplication(), type, filter);
        DynamicListHandler handler = new DynamicListHandler(getListView(), mAdapter);
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
