package be.ugent.zeus.hydra.common.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.crashlytics.android.Crashlytics;

/**
 * Custom version of {@link android.support.v4.view.ViewPager} that does not crash on weird errors.
 *
 * @see <a href="https://github.com/chrisbanes/PhotoView#issues-with-viewgroups">Similar issues in other libraries</a>
 *
 * @author Niko Strijbol
 */
public class HackedViewPager extends ViewPager {
    public HackedViewPager(@NonNull Context context) {
        super(context);
    }

    public HackedViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException e) {
            // Log as report, so maybe we can provide them to Google to fix it sometime
            Crashlytics.logException(e);
            return false;
        }
    }
}
