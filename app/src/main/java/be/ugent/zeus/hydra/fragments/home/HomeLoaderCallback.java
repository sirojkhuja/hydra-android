package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.executor.RequestCallback;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Base callback class for the loaders in the home fragment.
 *
 * @author Niko Strijbol
 */
abstract class HomeLoaderCallback implements LoaderManager.LoaderCallbacks<ThrowableEither<List<HomeCard>>>, RequestCallback<List<HomeCard>> {

    protected final Context context;
    protected final HomeCardAdapter adapter;
    protected final FragmentCallback callback;

    public HomeLoaderCallback(Context context, HomeCardAdapter adapter, FragmentCallback callback) {
        this.context = context;
        this.adapter = adapter;
        this.callback = callback;
    }

    @Override
    public void receiveData(@NonNull List<HomeCard> data) {

        if(!isTypeActive()) {
            return;
        }

        adapter.updateCardItems(data, getCardType());
        callback.onCompleted();
    }

    @Override
    public void receiveError(@NonNull Throwable error) {
        if(!isTypeActive()) {
            return;
        }

        String e = this.context.getString(R.string.fragment_home_error);
        String name = this.context.getString(getErrorName());

        callback.onError(String.format(e, name));
    }

    /**
     * @return The card type of the cards that are produced here.
     */
    @HomeCard.CardType
    protected abstract int getCardType();

    /**
     * Check to see if a card type is showable.
     *
     * @return True if the card may be shown.
     */
    protected boolean isTypeActive() {
        Set<String> data = PreferenceManager.getDefaultSharedPreferences(this.context).getStringSet(HomeFragment.PREF_DISABLED_CARDS, Collections.<String>emptySet());
        return !data.contains(String.valueOf(getCardType()));
    }

    /**
     * @return Name of this request for error messages.
     */
    @StringRes
    protected abstract int getErrorName();

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<ThrowableEither<List<HomeCard>>> loader, ThrowableEither<List<HomeCard>> data) {
        if (data.hasError()) {
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
    public void onLoaderReset(Loader<ThrowableEither<List<HomeCard>>> loader) {
        loader.reset();
    }
}