package hu.bute.auctionapp.dynamiclist;

import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class DynamicListAdapter implements ListAdapter {
    private final ListView list;
    private final DynamicLoader listener;
    private final AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        int currentFirstVisibleItem;
        int currentVisibleItemCount;
        int currentTotalItemCount;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (this.currentTotalItemCount - currentFirstVisibleItem - currentVisibleItemCount <= 1
                    && scrollState == SCROLL_STATE_IDLE) {
                requestLoad();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (visibleItemCount == totalItemCount) {
                requestLoad();
            }
            this.currentFirstVisibleItem = firstVisibleItem;
            this.currentVisibleItemCount = visibleItemCount;
            this.currentTotalItemCount = totalItemCount;
        }
    };
    private boolean isLoading;
    private boolean needProgressbar;
    private int animateIndex = 0;
    private BaseAdapter adapter;

    public DynamicListAdapter(ListView target, BaseAdapter adapter, final DynamicLoader listener) {
        this.needProgressbar = listener.wantsToLoad();
        this.isLoading = false;
        this.list = target;
        this.adapter = adapter;
        this.listener = listener;

        list.setFooterDividersEnabled(false);
        list.setOnScrollListener(scrollListener);
    }

    public DynamicListAdapter(ListView target, BaseAdapter adapter) {
        this(target, adapter, (DynamicLoader) adapter);
    }

    public void requestLoad() {
        if (!isLoading) {
            boolean wantsload = listener.wantsToLoad();
            if (wantsload != needProgressbar) {
                needProgressbar = wantsload;
                adapter.notifyDataSetChanged();
            }
            if (needProgressbar) {
                new LoaderAsyncTask().execute();
            }
        }
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
        if (needProgressbar && position == getCount() - 1) {
            return adapter.getViewTypeCount();
        }
        return adapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return adapter.getViewTypeCount() + 1;
    }

    @Override
    public boolean isEmpty() {
        return adapter.isEmpty() && !needProgressbar;
    }

    @Override
    public int getCount() {
        if (needProgressbar) {
            return adapter.getCount() + 1;
        }
        return adapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        if (needProgressbar && position == getCount() - 1)
            return null;
        return adapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        if (needProgressbar && position == getCount() - 1)
            return position;
        return adapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1 && needProgressbar) {
            if (convertView == null) {
                convertView = new ProgressBar(list.getContext());
            }
        } else {
            convertView = adapter.getView(position, convertView, parent);
        }
        final Animation anim;
        if (position >= animateIndex) {
            anim = AnimationUtils.loadAnimation(list.getContext(), android.R.anim.fade_in);
            animateIndex = position + 1;
        } else {
            anim = null;
        }
        convertView.setAnimation(anim);
        return convertView;
    }

    public interface DynamicLoader {
        public boolean wantsToLoad();

        public Object doLoading();

        public void addLoaded(Object result);
    }

    private class LoaderAsyncTask extends AsyncTask<Void, Void, Object> {
        private int preloadSize = 0;

        LoaderAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            isLoading = true;
           /* if (!needProgressbar) {
                needProgressbar = true;
                adapter.notifyDataSetChanged();
            }*/
            preloadSize = adapter.getCount();
        }

        @Override
        protected Object doInBackground(Void... params) {
            return listener.doLoading();
        }

        @Override
        protected void onPostExecute(Object aresult) {
            listener.addLoaded(aresult);

            needProgressbar = listener.wantsToLoad();
            animateIndex = preloadSize;
            isLoading = false;
            adapter.notifyDataSetChanged();
        }
    }
}