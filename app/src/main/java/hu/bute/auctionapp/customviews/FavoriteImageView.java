package hu.bute.auctionapp.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import hu.bute.auctionapp.R;

/**
 * Created by Varsi on 2014.12.10..
 */
public class FavoriteImageView extends ImageView {
    private boolean isFavorite;

    public FavoriteImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavoriteImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
        if (isFavorite) {
            setImageResource(R.drawable.favorite);
        } else {
            setImageResource(R.drawable.nofavorite);
        }
    }
}
