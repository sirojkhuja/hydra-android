package be.ugent.zeus.hydra.repository.observers;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;
import be.ugent.zeus.hydra.repository.Result;

/**
 * Observes successful loading of data.
 *
 * @author Niko Strijbol
 */
public abstract class SuccessObserver<D> implements Observer<Result<D>> {

    private static final String TAG = "SuccessObserver";

    @Override
    public void onChanged(@Nullable Result<D> result) {

        Log.d(TAG, "onChanged: receiving data");

        if (result == null) {
            onEmpty();
            return;
        }

        if (result.getStatus() == Result.Status.DONE || result.getStatus() == Result.Status.CONTINUING) {
            onSuccess(result.getData());
            return;
        }

        if (result.getStatus() == Result.Status.ERROR) {
            if (result.hasData()) {
                onSuccess(result.getData());
            } else {
                onEmpty();
            }
        }
    }

    /**
     * Called when there is no result, so nothing should be shown. The default method will do nothing.
     */
    protected void onEmpty() {}

    /**
     * Called when the result was successful and there is data.
     *
     * @param data The data.
     */
    protected abstract void onSuccess(D data);
}