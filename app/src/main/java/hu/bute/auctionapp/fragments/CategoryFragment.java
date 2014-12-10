package hu.bute.auctionapp.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CategoryFragment extends ListFragment {
    private static final String KEY_CATEGORIES_ID = "key_cats_id";
    private OnCategorySelectedListener listener;

    public static CategoryFragment newInstance(int categoriesId) {
        CategoryFragment result = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_CATEGORIES_ID, categoriesId);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnCategorySelectedListener) activity;
        final int categoriesId = getArguments().getInt(KEY_CATEGORIES_ID);
        setListAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, activity.getResources().getStringArray(categoriesId)));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (listener != null) {
            listener.categorySelected(position);
        }
    }

    public interface OnCategorySelectedListener {
        public void categorySelected(int index);
    }
}
