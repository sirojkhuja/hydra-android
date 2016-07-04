package be.ugent.zeus.hydra.recyclerview.adapters;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.viewholder.home.*;

import java.util.*;

import static be.ugent.zeus.hydra.models.cards.HomeCard.CardType.*;

/**
 * Created by feliciaan on 06/04/16.
 */
public class HomeCardAdapter extends RecyclerView.Adapter<AbstractViewHolder> {

    private List<HomeCard> cardItems = new ArrayList<>();
    private SharedPreferences preferences;

    public HomeCardAdapter(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    /**
     * Remove all items of a given type and a new list
     * @param cardList List with object implementing the card protocol
     * @param type The type of the cards
     */
    public void updateCardItems(List<HomeCard> cardList, @HomeCard.CardType int type) {
        removeCardType(type);

        cardItems.addAll(cardList);

        Collections.sort(cardItems, new Comparator<HomeCard>() {
            @Override
            public int compare(HomeCard lhs, HomeCard rhs) {
                return  -(lhs.getPriority() - rhs.getPriority());
            }
        });

        notifyDataSetChanged();
    }

    public void removeCardType(@HomeCard.CardType int type) {
        Iterator<HomeCard> it = cardItems.iterator();
        while (it.hasNext()) { // Why no filter :(
            HomeCard c = it.next();
            if (c.getCardType() == type) {
                notifyItemRemoved(cardItems.indexOf(c));
                it.remove();
            }
        }
    }

    @Override
    public AbstractViewHolder onCreateViewHolder(ViewGroup parent, @HomeCard.CardType int viewType) {
        switch (viewType) {
            case RESTO:
                return new RestoCardViewHolder(getViewForLayout(R.layout.home_card_resto, parent), this);
            case ACTIVITY:
                return new ActivityCardViewHolder(getViewForLayout(R.layout.home_card_event, parent), this);
            case SPECIAL_EVENT:
                return new SpecialEventCardViewHolder(getViewForLayout(R.layout.home_card_special, parent));
            case SCHAMPER:
                return new SchamperViewHolder(getViewForLayout(R.layout.home_card_schamper, parent), this);
            case NEWS_ITEM:
                return new NewsItemViewHolder(getViewForLayout(R.layout.home_card_news_item, parent));
            case MINERVA_LOGIN:
                View v = getViewForLayout(R.layout.home_minerva_login_card, parent);
                return new MinervaLoginViewHolder(v);
        }
        return null;
    }

    public void disableCardType(@HomeCard.CardType int viewType) {
        Set<String> disabled = preferences.getStringSet("pref_disabled_cards", Collections.<String>emptySet());
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> newDisabled = new HashSet<>(disabled);
        newDisabled.add(String.valueOf(viewType));
        editor.putStringSet("pref_disabled_cards", newDisabled);
        if(editor.commit()) {
            removeCardType(viewType);
        }
    }

    private View getViewForLayout(int rLayout, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(rLayout, parent, false);
    }

    @Override
    public void onBindViewHolder(AbstractViewHolder holder, int position) {
        HomeCard object = cardItems.get(position);
        holder.populate(object);
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    @Override
    @HomeCard.CardType
    public int getItemViewType(int position) {
        return cardItems.get(position).getCardType();
    }
}