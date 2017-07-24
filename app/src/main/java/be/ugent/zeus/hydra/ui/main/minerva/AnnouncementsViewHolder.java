package be.ugent.zeus.hydra.ui.main.minerva;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectListAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class AnnouncementsViewHolder extends DataViewHolder<Pair<Announcement, Boolean>> {

    private final TextView title;
    private final TextView subtitle;
    private final View backgroundHolder;
    private final ResultStarter resultStarter;
    private final MultiSelectListAdapter<Announcement> adapter;

    public AnnouncementsViewHolder(View itemView, ResultStarter starter, MultiSelectListAdapter<Announcement> adapter) {
        super(itemView);
        this.resultStarter = starter;
        this.adapter = adapter;
        title = $(itemView, R.id.title);
        subtitle = $(itemView, R.id.subtitle);
        backgroundHolder = $(itemView, R.id.background_container);
    }

    @Override
    public void populate(final Pair<Announcement, Boolean> pair) {

        Announcement announcement = pair.first;

        toggleBackground();

        title.setText(announcement.getTitle());
        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                announcement.getCourse().getTitle(),
                DateUtils.relativeDateTimeString(announcement.getDate(), itemView.getContext(), false));
        subtitle.setText(infoText);

        itemView.setOnClickListener(v -> {
            // When we are in select mode, we just toggle the items. Otherwise we open them.
            if (adapter.hasSelected()) {
                toggleSelected();
            } else {
                Intent intent = new Intent(resultStarter.getContext(), AnnouncementActivity.class);
                intent.putExtra(AnnouncementActivity.ARG_ANNOUNCEMENT, (Parcelable) announcement);
                resultStarter.startActivityForResult(intent, resultStarter.getRequestCode());
            }
        });

        itemView.setOnLongClickListener(v -> {
            toggleSelected();
            return true;
        });
    }

    private void toggleSelected() {
        adapter.setChecked(getAdapterPosition());
        toggleBackground();
    }

    private void toggleBackground() {
        if (adapter.isChecked(getAdapterPosition())) {
            backgroundHolder.setBackgroundColor(R.color.black_overlay);
        } else {
            backgroundHolder.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}