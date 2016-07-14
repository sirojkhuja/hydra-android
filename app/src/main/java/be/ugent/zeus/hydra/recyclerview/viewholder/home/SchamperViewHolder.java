package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.SchamperArticleActivity;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SchamperCard;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.views.NowToolbar;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperViewHolder extends AbstractViewHolder {

    private TextView title;
    private TextView date;
    private TextView author;
    private ImageView image;
    private NowToolbar toolbar;
    private HomeCardAdapter adapter;

    public SchamperViewHolder(View v, HomeCardAdapter adapter) {
        super(v);

        title =     $(v, R.id.title);
        date =      $(v, R.id.date);
        author =    $(v, R.id.author);
        image =     $(v, R.id.image);
        toolbar =   $(v, R.id.card_now_toolbar);
        this.adapter = adapter;
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCard.CardType.SCHAMPER) {
            return; //TODO: report errors
        }

        final SchamperCard schamperCard = (SchamperCard) card;
        final Article article = schamperCard.getArticle();
        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());

        Picasso.with(this.itemView.getContext()).load(article.getImage()).into(image);

        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), SchamperArticleActivity.class);
                intent.putExtra("article", (Parcelable) article);
                itemView.getContext().startActivity(intent);
            }
        });

        toolbar.setOnClickListener(adapter.listener(HomeCard.CardType.SCHAMPER));
    }
}