package hu.bute.auctionapp.fragments.stores;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.adapters.StoresAdapter;
import hu.bute.auctionapp.dynamiclist.DynamicListHandler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoresFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoresFragment extends ListFragment {
    private static final String TYPE_KEY = "type";
    private OnFragmentInteractionListener mListener;
    private int type;
    private StoresAdapter mAdapter;

    public StoresFragment() {
    }

    public static StoresFragment newInstance(int type) {
        StoresFragment fragment = new StoresFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE_KEY, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            type = savedInstanceState.getInt(TYPE_KEY);
        } else if (getArguments() != null) {
            Bundle args = getArguments();
            type = args.getInt(TYPE_KEY);
        }
        //setListAdapter(new StoresAdapter((hu.bute.auctionapp.AuctionApplication) getActivity().getApplication(), type));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_storelist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new StoresAdapter((AuctionApplication) getActivity().getApplication(), type);
        DynamicListHandler handler = new DynamicListHandler(getListView(), mAdapter);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TYPE_KEY, type);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void refreshStores() {
        System.out.println("StoresFragment.refreshStores");
        System.out.println("mAdapter = " + mAdapter);
        if (mAdapter != null) {
            mAdapter.refresh();
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }
}
