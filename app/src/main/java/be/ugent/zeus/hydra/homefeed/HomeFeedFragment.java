package be.ugent.zeus.hydra.homefeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.MainActivity;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.homefeed.content.debug.WaitRequest;
import be.ugent.zeus.hydra.homefeed.content.event.EventRequest;
import be.ugent.zeus.hydra.homefeed.content.minerva.agenda.MinervaAgendaRequest;
import be.ugent.zeus.hydra.homefeed.content.minerva.announcement.MinervaAnnouncementRequest;
import be.ugent.zeus.hydra.homefeed.content.news.NewsRequest;
import be.ugent.zeus.hydra.homefeed.content.resto.RestoRequest;
import be.ugent.zeus.hydra.homefeed.content.schamper.SchamperRequest;
import be.ugent.zeus.hydra.homefeed.content.specialevent.SpecialEventRequest;
import be.ugent.zeus.hydra.homefeed.feed.FeedOperation;
import be.ugent.zeus.hydra.homefeed.loader.FeedCollection;
import be.ugent.zeus.hydra.homefeed.loader.HomeFeedLoader;
import be.ugent.zeus.hydra.homefeed.loader.HomeFeedLoaderCallback;
import be.ugent.zeus.hydra.plugins.OfflinePlugin;
import be.ugent.zeus.hydra.requests.common.OfflineBroadcaster;
import be.ugent.zeus.hydra.utils.IterableSparseArray;
import be.ugent.zeus.hydra.utils.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.utils.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.utils.recycler.SpanItemSpacingDecoration;
import java8.util.function.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static be.ugent.zeus.hydra.homefeed.feed.OperationFactory.add;
import static be.ugent.zeus.hydra.homefeed.feed.OperationFactory.get;
import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * The fragment showing the home feed.
 *
 * The user has the possibility to decide to hide certain card types. When a user disables a certain type of cards,
 * we do not retrieve the data.
 *
 * This is the fragment responsible for showing the list. The data is loaded in {@link HomeFeedLoader}.
 *
 * @author Niko Strijbol
 * @author silox
 */
public class HomeFeedFragment extends Fragment implements HomeFeedLoaderCallback, SwipeRefreshLayout.OnRefreshListener {

    private static final boolean ADD_STALL_REQUEST = false;

    private static final String TAG = "HomeFeedFragment";

    public static final String PREF_DISABLED_CARDS = "pref_disabled_cards";

    private static final int LOADER = 0;

    private boolean shouldRefresh;
    private boolean preferencesUpdated;

    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeFeedAdapter adapter;
    private RecyclerView recyclerView;

    private ActivityHelper helper;

    /**
     * This boolean indicates whether the data from the loader was cached or not. If it was, the partial update
     * function will not be called.
     */
    private boolean wasCached = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        helper = CustomTabsHelper.initHelper(getActivity(), null);
        helper.setShareMenu();
    }

    public ActivityHelper getHelper() {
        return helper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = $(view, R.id.home_cards_view);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout = $(view, R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);

        adapter = new HomeFeedAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setRefreshing(true);
        getLoaderManager().initLoader(LOADER, null, this);
    }

    private OfflinePlugin getPlugin() {
        return ((MainActivity) getActivity()).getOfflinePlugin();
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.registerReceiver(receiver, OfflineBroadcaster.getBroadcastFilter());

        if (preferencesUpdated) {
            swipeRefreshLayout.setRefreshing(true);
            refreshLoader();
            preferencesUpdated = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        helper.bindCustomTabsService(getActivity());
    }

    /**
     * If the fragment goes to pause, we don't need to restart the loaders.
     */
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.unregisterReceiver(receiver);
        preferencesUpdated = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        helper.unbindCustomTabsService(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //See https://code.google.com/p/android/issues/detail?id=78062
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.clearAnimation();
    }

    /**
     * Restart the loaders
     */
    private void refreshLoader() {
        getLoader().onContentChanged();
    }

    private void showErrorMessage(String message) {
        //noinspection WrongConstant
        getPlugin().showSnackbar(message, Snackbar.LENGTH_LONG, null);
    }

    @Override
    public void onPartialUpdate(List<HomeCard> data, @Nullable DiffUtil.DiffResult update, @HomeCard.CardType int cardType) {
        Log.i(TAG, "Added card type: " + cardType);
        adapter.setData(data, update);
        wasCached = false;
    }

    public HomeFeedLoader getLoader() {
        return (HomeFeedLoader) getLoaderManager().<Pair<Set<Integer>, List<HomeCard>>>getLoader(LOADER);
    }

    @Override
    public void onPartialError(@HomeCard.CardType int cardType) {
        showErrorMessage(getContext().getString(R.string.home_feed_not_loaded));
    }

    @Override
    public Loader<Pair<Set<Integer>, List<HomeCard>>> onCreateLoader(int id, Bundle args) {
        return new HomeFeedLoader(getContext(), this);
    }

    /**
     * Called by the loader to retrieve the operations that should be executed. This method may be called from another
     * thread.
     *
     * @return The operations to execute.
     */
    @Override
    public IterableSparseArray<FeedOperation> onScheduleOperations(Context c) {
        FeedCollection operations = new FeedCollection();

        Set<String> s = PreferenceManager
                .getDefaultSharedPreferences(c)
                .getStringSet(HomeFeedFragment.PREF_DISABLED_CARDS, Collections.emptySet());

        Function<Integer, Boolean> d = i -> isTypeActive(s, i);

        //Always add the special events.
        operations.add(add(new SpecialEventRequest(c, shouldRefresh)));

        //Add other stuff if needed
        operations.add(get(d, () -> new RestoRequest(c, shouldRefresh), HomeCard.CardType.RESTO));
        operations.add(get(d, () -> new EventRequest(c, shouldRefresh), HomeCard.CardType.ACTIVITY));
        operations.add(get(d, () -> new SchamperRequest(c, shouldRefresh), HomeCard.CardType.SCHAMPER));
        operations.add(get(d, () -> new NewsRequest(c, shouldRefresh), HomeCard.CardType.NEWS_ITEM));
        operations.add(get(d, () -> new MinervaAnnouncementRequest(c), HomeCard.CardType.MINERVA_ANNOUNCEMENT));
        operations.add(get(d, () -> new MinervaAgendaRequest(c), HomeCard.CardType.MINERVA_AGENDA));

        if (BuildConfig.DEBUG && ADD_STALL_REQUEST) {
            operations.add(add(new WaitRequest()));
        }

        return operations;
    }

    @Override
    public void onLoadFinished(Loader<Pair<Set<Integer>, List<HomeCard>>> l, Pair<Set<Integer>, List<HomeCard>> data) {
        Log.i(TAG, "Finished loading data");
        if (wasCached) {
            for(Integer error: data.first) {
                //noinspection WrongConstant
                onPartialError(error);
            }

            adapter.setData(new ArrayList<>(data.second), null);
        }

        //Scroll to top if not refreshed
        if (!shouldRefresh) {
            recyclerView.scrollToPosition(0);
        }

        wasCached = true;
        shouldRefresh = false;
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Pair<Set<Integer>, List<HomeCard>>> loader) {
        //Do nothing
    }

    /**
     * Check to see if a card type is showable.
     *
     * @return True if the card may be shown.
     */
    private boolean isTypeActive(Set<String> data, @HomeCard.CardType int cardType) {
        return !data.contains(String.valueOf(cardType));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        //TODO there must a better of doing this
        HydraActivity activity = (HydraActivity) getActivity();
        HydraActivity.tintToolbarIcons(activity.getToolbar(), menu, R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        shouldRefresh = true;
        getPlugin().dismiss();
        refreshLoader();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(OfflineBroadcaster.OFFLINE)) {
                //noinspection WrongConstant
                getPlugin().showSnackbar(R.string.offline_data_use, Snackbar.LENGTH_INDEFINITE, HomeFeedFragment.this);
            }
        }
    };
}