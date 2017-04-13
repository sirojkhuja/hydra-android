package be.ugent.zeus.hydra.ui.main.homefeed.content;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.DataViewHolder;
import be.ugent.zeus.hydra.ui.common.widgets.NowToolbar;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * View holder for cards that are hideable, using the {@link NowToolbar}.
 *
 * @author Niko Strijbol
 */
public abstract class HideableViewHolder extends DataViewHolder<HomeCard> {

    protected final HomeFeedAdapter adapter;
    protected final NowToolbar toolbar;

    private TextView priority;

    public HideableViewHolder(View itemView, HomeFeedAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        toolbar = $(itemView, R.id.card_now_toolbar);

        if(BuildConfig.DEBUG) {
            priority = new TextView(itemView.getContext());
            CardView cardView = (CardView) itemView;
            cardView.addView(priority);
        }
    }

    @Override
    public void populate(HomeCard card) {
        toolbar.setOnClickListener(adapter.listener(card.getCardType()));
        debugPriority(card);
    }

    protected void debugPriority(HomeCard card) {
        if(BuildConfig.DEBUG && false) {
            priority.setText("Prioriteit: " + card.getPriority());
        }
    }
}