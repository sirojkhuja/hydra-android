package be.ugent.zeus.hydra.ui.main.homefeed.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import be.ugent.zeus.hydra.ui.preferences.HomeFragment;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Util class, with e.g. help calculate the priority a card should have in the home feed.
 *
 * @see HomeCard
 *
 * @author Niko Strijbol
 */
public class FeedUtils {

    /**
     * The special shift.
     */
    public static int FEED_SPECIAL_SHIFT = 10;

    /**
     * The upper limit on the feed.
     */
    public static int FEED_MAX_VALUE = 1000 + FEED_SPECIAL_SHIFT;

    /**
     * Lineair interpolation of the value x ∈ [a,b] to [10,FEED_MAX_VALUE]. The formula used is:
     *
     * x' = (c - a) * FEED_MAX_VALUE / (b - a)
     *
     * If x is bigger than b, FEED_MAX_VALUE is returned. This is to help calculate a correct priority.
     *
     * @param x The value in the original range.
     * @param a Original range start.
     * @param b Original range end.
     *
     * @return The interpolated value in [y,z].
     */
    public static int lerp(int x, int a, int b) {
        return Math.min((int) ((x - a) * (double) FEED_MAX_VALUE / (b - a)), FEED_MAX_VALUE) + FEED_SPECIAL_SHIFT;
    }

    private static boolean isDataConstrained(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean setting = preferences.getBoolean(HomeFragment.PREF_DATA_SAVER, HomeFragment.PREF_DATA_SAVER_DEFAULT);
        return setting && NetworkUtils.isMeteredConnection(context);
    }

    /**
     * Use Picasso to load a thumbnail. If data saving is enabled, the thumbnail will only be loaded from the cache.
     *
     * @param context The context.
     * @param image The image url to load.
     * @param target The target view for the image.
     */
    public static void loadThumbnail(Context context, String image, ImageView target) {
        RequestCreator creator = Picasso.with(context).load(image).fit().centerInside();

        if (FeedUtils.isDataConstrained(context)) {
            creator.networkPolicy(NetworkPolicy.OFFLINE);
        }

        creator.into(target);
    }
}