package hu.bute.auctionapp.fragments;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import hu.bute.auctionapp.R;
import hu.bute.auctionapp.activities.ProductDetailsActivity;
import hu.bute.auctionapp.adapters.ProductsAdapter;
import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.dynamiclist.DynamicListHandler;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class ProductFragment extends ListFragment implements AbsListView.OnItemClickListener {
    private static final String SECTION_NUMBER = "section_number";
    private int section_number;
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProductFragment() {
    }

    public static ProductFragment newInstance(int sectionNumber) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            section_number = getArguments().getInt(SECTION_NUMBER);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(this);
        DynamicListHandler handler = new DynamicListHandler(getListView(), new ProductsAdapter(getActivity(), section_number));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProductData data = (ProductData) parent.getAdapter().getItem(position);
        Intent i = new Intent(getActivity(), ProductDetailsActivity.class);
        i.putExtra(ProductDetailsActivity.PRODUCT_KEY, data);
        startActivity(i);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SECTION_NUMBER, section_number);
    }
}
