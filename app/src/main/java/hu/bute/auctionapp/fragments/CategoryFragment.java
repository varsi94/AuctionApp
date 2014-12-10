package hu.bute.auctionapp.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import hu.bute.auctionapp.R;

public class CategoryFragment extends ListFragment {
    private static final String KEY_CATEGORIES_ID = "key_cats_id";
    private static final String KEY_INIT_SELECTION = "key_init_sel";
    private BaseAdapter adapter;
    private OnCategorySelectedListener listener;
    private int selection = -1;

    public static CategoryFragment newInstance(int categoriesId, int initialselection) {
        CategoryFragment result = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_CATEGORIES_ID, categoriesId);
        bundle.putInt(KEY_INIT_SELECTION, initialselection);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_list, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            selection = savedInstanceState.getInt("selection");
        }
        selection = getArguments().getInt(KEY_INIT_SELECTION);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setItemChecked(selection, true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selection", selection);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnCategorySelectedListener) activity;
        final int categoriesId = getArguments().getInt(KEY_CATEGORIES_ID);
        adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_activated_1, activity.getResources().getStringArray(categoriesId));
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (listener != null) {
            listener.categorySelected(position, (String) adapter.getItem(position));
            setSelection(position);
        }
    }

    public void clearSelection() {
        setSelection(-1);
    }

    public void setSelection(int selection) {
        this.selection = selection;
        getArguments().putInt(KEY_INIT_SELECTION, selection);
        getListView().setItemChecked(selection, true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnCategorySelectedListener {
        public void categorySelected(int index, String value);
    }
}
