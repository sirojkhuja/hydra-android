package be.ugent.zeus.hydra.loader;

import android.content.Context;
import android.support.v4.content.Loader;
import be.ugent.zeus.hydra.activities.common.LoaderToolbarActivity;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;

import java.io.Serializable;

/**
 * Same as {@link LoaderFragment} and {@link LoaderToolbarActivity},
 * but without any parent.
 *
 * @author Niko Strijbol
 */
public abstract class LoaderCallback<D extends Serializable> implements ErrorLoaderCallback<D> {

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<ThrowableEither<D>> loader, ThrowableEither<D> data) {
        if(data.hasError()) {
            receiveError(data.getError());
        } else {
            receiveData(data.getData());
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.  The application
     * should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<ThrowableEither<D>> loader) {
        loader.reset();
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param context The context.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    protected Loader<ThrowableEither<D>> onCreateLoader(Context context) {
        return new CachedAsyncTaskLoader<>(getRequest(), context);
    }
}