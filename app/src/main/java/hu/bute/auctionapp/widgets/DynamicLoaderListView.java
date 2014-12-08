package hu.bute.auctionapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

public class DynamicLoaderListView extends ListView {
    public interface OnLoadListener {
        public boolean startLoad();
    }

    private OnLoadListener listener;

    public DynamicLoaderListView(Context context) {
        super(context);
    }

    public DynamicLoaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicLoaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    {
        super.setFooterDividersEnabled(false);
        super.setOnScrollListener(new OnScrollListener() {
            boolean wouldLoad = true;
            public int currentFirstVisibleItem;
            public int currentVisibleItemCount;
            public int currentTotalItemCount;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (this.currentTotalItemCount - currentFirstVisibleItem - currentVisibleItemCount <= 1
                        && scrollState == SCROLL_STATE_IDLE) {
                    if (wouldLoad && listener != null) {
                        wouldLoad = listener.startLoad();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.currentTotalItemCount = totalItemCount;
            }
        });
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
    }

    public void setOnLoadListener(OnLoadListener listener) {
        this.listener = listener;
    }

    @Override
    public void setFooterDividersEnabled(boolean footerDividersEnabled) {
    }
}
