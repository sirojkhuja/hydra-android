package be.ugent.zeus.hydra.cache;

import android.content.AsyncTaskLoader;
import android.content.Context;

import be.ugent.zeus.hydra.cache.file.SerializeCache;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.requests.common.RequestFailureException;

import java.io.Serializable;

/**
 * A special version of the loader using the framework classes for the settings. This loader should not be preferred
 * over the other one.
 *
 * Once we have API 23+, we might switch to this, but not before that.
 *
 * TODO: track fixes to compat preferences so we might use that instead.
 *
 * @see CachedAsyncTaskLoader
 *
 * @author Niko Strijbol
 */
public class SystemCachedAsyncTaskLoader<D extends Serializable, R> extends AsyncTaskLoader<ThrowableEither<R>> {

    private CacheRequest<D, R> request;
    private ThrowableEither<R> data = null;
    private boolean refresh;
    private final Cache cache;

    /**
     * This loader will honour the cache settings of the request.
     *
     * @param request The request to execute.
     * @param context The context.
     */
    public SystemCachedAsyncTaskLoader(CacheRequest<D, R> request, Context context) {
        this(request, context, false);
    }

    /**
     * This loader has the option to ignore the cache.
     *
     * @param request   The request to execute.
     * @param context   The context.
     * @param freshData If the data should be fresh or maybe cached.
     */
    public SystemCachedAsyncTaskLoader(CacheRequest<D, R> request, Context context, boolean freshData) {
        super(context);
        this.request = request;
        this.refresh = freshData;
        this.cache = new SerializeCache(context);
    }

    /**
     * Sets the refresh flag. This means the next request will get new data, regardless of the cache.
     */
    public void setNextRefresh() {
        this.refresh = true;
    }

    /**
     * {@inheritDoc}
     *
     * The data is loaded and cached by default.
     *
     * If the refresh flag is set, the existing cache is ignored, a new request is made and the result of that
     * request is saved in the cache.
     *
     * @return The data or the error that occurred while getting the data.
     */
    @Override
    public ThrowableEither<R> loadInBackground() {

        //Load the data, and set the refresh flag to false.
        try {
            R content;
            if (refresh) {
                //Get new data
                content = cache.get(request, Cache.NEVER);
            } else {
                content = cache.get(request);
            }
            data = new ThrowableEither<>(content);
        } catch (RequestFailureException e) {
            data = new ThrowableEither<>(e);
        }
        this.refresh = false;
        return data;
    }

    /**
     * Pass the data to the listener if the loader was not reset and the loader was started.
     */
    @Override
    public void deliverResult(ThrowableEither<R> data) {

        // The Loader has been reset; ignore the result and invalidate the data.
        if (isReset()) {
            return;
        }

        // Set the data in the loader.
        this.data = data;

        // If the Loader is in a started state, deliver the results to the client.
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    /**
     * Handles requests to start the loader.
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        // If the data is available, deliver it.
        if (data != null) {
            deliverResult(data);
        }

        // When the observer detects a change, it should call onContentChanged() on the Loader, which will
        // cause the next call to takeContentChanged() to return true. If this is ever the case
        // (or if the current data is null), we force a new load.
        if (takeContentChanged() || data == null) {
            forceLoad();
        }
    }

    /**
     * Handles requests to stop the loader.
     */
    @Override
    protected void onStopLoading() {
        super.onStopLoading();

        // Stop the request.
        cancelLoad();
    }

    /**
     * Handles a request to completely reset the loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader has stopped.
        onStopLoading();

        // Reset the data.
        data = null;
    }
}