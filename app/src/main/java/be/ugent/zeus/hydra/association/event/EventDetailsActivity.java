/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.association.event;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.util.LinkifyCompat;
import androidx.core.view.WindowCompat;

import java.time.format.DateTimeFormatter;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.databinding.ActivityEventDetailBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Activity to show details of an association's event.
 *
 * @author Niko Strijbol
 */
public class EventDetailsActivity extends BaseActivity<ActivityEventDetailBinding> {

    public static final String PARCEL_EVENT = "eventParcelable";
    public static final String PARCEL_ASSOCIATION = "associationParcelable";
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String GENT = "51.05,3.72";

    private Event event;

    public static Intent start(Context context, Event event, Association association) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra(PARCEL_EVENT, event);
        intent.putExtra(PARCEL_ASSOCIATION, association);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityEventDetailBinding::inflate);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        boolean hasDescription = true;

        // Get data from saved instance, or from intent.
        event = getIntent().getParcelableExtra(PARCEL_EVENT);
        Association association = getIntent().getParcelableExtra(PARCEL_ASSOCIATION);
        assert event != null;
        assert association != null;

        if (event.getTitle() != null) {
            requireToolbar().setTitle(event.getTitle());
        }

        if (event.getAssociation() != null) {
            binding.eventOrganisatorMain.setText(association.getName());
        }

        if (event.getDescription() != null && !event.getDescription().trim().isEmpty()) {
            binding.description.setText(event.getDescription());
            LinkifyCompat.addLinks(binding.description, Linkify.ALL);
        } else {
            hasDescription = false;
            binding.eventDescriptionBlock.setVisibility(View.GONE);
        }

        if (association.getDescription() != null && !association.getDescription().trim().isEmpty()) {
            binding.eventOrganisatorSmall.setText(association.getDescription());
            // If there is no event description, allow the association description to be longer.
            if (!hasDescription) {
                binding.eventOrganisatorSmall.setMaxLines(Integer.MAX_VALUE);
            }
        }

        if (association.getWebsite() != null) {
            binding.eventOrganizer.setOnClickListener(v -> NetworkUtils.maybeLaunchBrowser(v.getContext(), association.getWebsite()));
        }

        if (event.hasPreciseLocation() || event.hasLocation()) {
            if (event.hasLocation()) {
                binding.location.setText(event.getLocation());
            } else {
                binding.location.setText(event.getAddress());
            }
            // Make location clickable
            binding.locationRow.setOnClickListener(view -> NetworkUtils.maybeLaunchIntent(this, getLocationIntent()));
        } else {
            binding.location.setText(R.string.event_detail_no_location);
        }

        binding.timeStart.setText(event.getLocalStart().format(format));

        if (event.getLocalEnd() != null) {
            binding.timeEnd.setText(event.getLocalEnd().format(format));
        } else {
            binding.timeEnd.setText(R.string.event_detail_date_unknown);
        }

        if (event.getAssociation() != null) {
            Picasso.get().load(association.getImageLink()).into(binding.eventOrganisatorImage, new EventCallback(binding.eventOrganisatorImage));
        } else {
            binding.eventOrganisatorImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

        Reporting.getTracker(this)
                .log(new EventViewedEvent(event));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            //Up button
            supportFinishAfterTransition();
            return true;
        } else if (itemId == R.id.event_link) {
            NetworkUtils.maybeLaunchBrowser(this, event.getUrl());
            return true; 
        } else if (itemId == R.id.menu_event_add_to_calendar) {
            addToCalendar();

            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);

        // We need to manually set the color of this Drawable for some reason.
        tintToolbarIcons(menu, R.id.event_link, R.id.menu_event_add_to_calendar);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Add the event to an intent, for adding to the calendar.
     */
    private void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStart().toInstant().toEpochMilli())
                .putExtra(CalendarContract.Events.TITLE, event.getTitle())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation())
                .putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_TENTATIVE);

        if (event.getEnd() != null) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEnd().toInstant().toEpochMilli());
        }

        NetworkUtils.maybeLaunchIntent(this, intent);
    }

    /**
     * Remove options if we don't have the data.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!event.hasUrl()) {
            menu.removeItem(R.id.event_link);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Get the intent for a location. If the precise location is available, that will be used. Otherwise, we just search
     * for the location. One location must be present.
     *
     * @return The intent.
     */
    private Intent getLocationIntent() {

        Uri uriLocation;

        //If there is a precise location, use that.
        if (event.hasPreciseLocation()) {
            uriLocation = Uri.parse("geo:" + GENT + "?q=" + event.getAddress());
        } else {
            uriLocation = Uri.parse("geo:" + GENT + "?q=" + event.getLocation());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, uriLocation);
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(getApplicationContext(), "Google Maps is niet geïnstalleerd.", Toast.LENGTH_LONG).show();
        }

        return intent;
    }

    private static class EventCallback extends Callback.EmptyCallback {
        private final ImageView organisatorImage;

        EventCallback(ImageView organisatorImage) {
            this.organisatorImage = organisatorImage;
        }

        @Override
        public void onError(Exception e) {
            organisatorImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
    }

    private static final class EventViewedEvent implements be.ugent.zeus.hydra.common.reporting.Event {

        private final Event event;

        private EventViewedEvent(Event event) {
            this.event = event;
        }

        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Reporting.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.itemCategory(), Event.class.getSimpleName());
            params.putString(names.itemId(), event.getIdentifier());
            params.putString(names.itemName(), event.getTitle());
            return params;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().viewItem();
        }
    }
}
