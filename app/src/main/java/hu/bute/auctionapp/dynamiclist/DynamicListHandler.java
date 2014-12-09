package hu.bute.auctionapp.dynamiclist;

import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class DynamicListHandler {
    private final ListView list;
    private final AdapterWrapper wrapper;
    private final DynamicLoader listener;
    private boolean isLoading = false;

    public DynamicListHandler(ListView target, BaseAdapter adapter, final DynamicLoader listener) {
        this.list = target;
        this.wrapper = new AdapterWrapper(adapter);
        this.listener = listener;
        list.setAdapter(wrapper);
        list.setFooterDividersEnabled(false);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentFirstVisibleItem;
            int currentVisibleItemCount;
            int currentTotalItemCount;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (this.currentTotalItemCount - currentFirstVisibleItem - currentVisibleItemCount <= 1
                        && scrollState == SCROLL_STATE_IDLE) {
                    checkWantsLoad();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount == totalItemCount) {
                    checkWantsLoad();
                }
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.currentTotalItemCount = totalItemCount;
            }
        });
    }

    private void checkWantsLoad() {
        if (!isLoading && listener.wantsToLoad()) {
            new LoaderAsyncTask().execute();
        }
    }

    public interface DynamicLoader {
        public boolean wantsToLoad();

        public void doLoading();
    }

    private class LoaderAsyncTask extends AsyncTask<Void, Void, Void> {
        LoaderAsyncTask() {
            isLoading = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            listener.doLoading();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isLoading = false;
            wrapper.adapter.notifyDataSetChanged();
        }
    }

    private class AdapterWrapper implements ListAdapter {
        private BaseAdapter adapter;

        public AdapterWrapper(BaseAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public boolean hasStableIds() {
            return adapter.hasStableIds();
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            adapter.registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            adapter.unregisterDataSetObserver(observer);
        }

        @Override
        public boolean areAllItemsEnabled() {
            return adapter.areAllItemsEnabled();
        }

        @Override
        public boolean isEnabled(int position) {
            return adapter.isEnabled(position);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getCount() - 1) {
                return getViewTypeCount() - 1;
            }
            return adapter.getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            return adapter.getViewTypeCount() + 1;
        }

        @Override
        public boolean isEmpty() {
            return adapter.isEmpty();
        }

        @Override
        public int getCount() {
            return adapter.getCount() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == getCount() - 1)
                return null;
            return adapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            if (position == getCount() - 1)
                return position;
            return adapter.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == getCount() - 1) {
                if (convertView == null) {
                    convertView = new ProgressBar(list.getContext());
                }
                return convertView;
            }
            return adapter.getView(position, convertView, parent);
        }
    }
}