package be.ugent.zeus.hydra.association.list;

import android.content.Intent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.AssociationMap;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.EventDetailsActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class EventViewHolderTest {

    private static void testEvent(boolean isLast) {
        View view = inflate(R.layout.item_event_item);
        EventViewHolder viewHolder = new EventViewHolder(view, new MemoryHolder());
        EventItem item = new EventItem(generate(Event.class), isLast);
        Event event = item.getItem();
        viewHolder.populate(item);

        // We don't test all values, it's not worth it to just copy all code.
        assertTextIs(event.getTitle(), view.findViewById(R.id.name));
        assertNotEmpty(view.findViewById(R.id.starttime));

        // Check that the click listener works.
        View card = view.findViewById(R.id.card_view);
        card.performClick();

        Intent expectedIntent = EventDetailsActivity.start(card.getContext(), event, Association.unknown(event.getAssociation()));
        Intent actual = getShadowApplication().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
        assertNotNull(actual.getParcelableExtra(EventDetailsActivity.PARCEL_EVENT));

        int expected = isLast ? View.GONE : View.VISIBLE;
        assertEquals(expected, view.findViewById(R.id.item_event_divider).getVisibility());
    }

    @Test
    public void populateMiddleEvent() {
        testEvent(false);
    }

    @Test(expected = IllegalStateException.class)
    public void populateHeader() {
        View view = inflate(R.layout.item_event_item);
        EventViewHolder viewHolder = new EventViewHolder(view, new MemoryHolder());
        EventItem item = new EventItem(generate(LocalDate.class));
        viewHolder.populate(item);
    }

    @Test
    public void populateLastEvent() {
        testEvent(true);
    }

    private static class MemoryHolder implements AssociationMap {
        @NonNull
        @Override
        public Association get(@Nullable String abbreviation) {
            return Association.unknown(abbreviation);
        }

        @Override
        public Stream<Association> associations() {
            return Stream.empty();
        }
    }
}
