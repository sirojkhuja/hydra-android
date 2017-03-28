package be.ugent.zeus.hydra.ui.main;

import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.ui.NewsArticleActivity;
import be.ugent.zeus.hydra.ui.common.html.Utils;
import be.ugent.zeus.hydra.ui.common.recyclerview.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.ui.NewsArticleActivity.PARCEL_NAME;
import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * View holder for the news items in the news tab or section.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class NewsItemViewHolder extends DataViewHolder<UgentNewsItem> {

    private TextView info;
    private TextView title;
    private TextView excerpt;

    NewsItemViewHolder(View v) {
        super(v);
        title = $(v, R.id.name);
        info = $(v, R.id.info);
        excerpt = $(v, R.id.article_excerpt);
    }

    @Override
    public void populate(final UgentNewsItem newsItem) {

        title.setText(newsItem.getTitle());

        String author = newsItem.getCreators().isEmpty() ? "" : newsItem.getCreators().iterator().next();

        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                DateUtils.relativeDateTimeString(newsItem.getCreated(), itemView.getContext()),
                author);
        info.setText(infoText);

        if (!TextUtils.isEmpty(newsItem.getDescription())) {
            excerpt.setText(Utils.fromHtml(newsItem.getDescription()).toString().trim());
        } else {
            excerpt.setText(Utils.fromHtml(newsItem.getText()).toString().trim());
        }

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NewsArticleActivity.class);
            intent.putExtra(PARCEL_NAME, (Parcelable) newsItem);
            v.getContext().startActivity(intent);
        });
    }
}