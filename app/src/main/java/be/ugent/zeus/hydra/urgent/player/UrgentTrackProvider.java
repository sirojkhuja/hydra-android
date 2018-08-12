package be.ugent.zeus.hydra.urgent.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.urgent.UrgentInfo;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.urgent.UrgentInfoRequest;
import java9.util.function.Consumer;

/**
 * @author Niko Strijbol
 */
class UrgentTrackProvider {

    static final String URGENT_ID = "be.ugent.zeus.hydra.urgent";

    static final String MEDIA_ID_ROOT = "__ROOT__";
    static final String MEDIA_ID_EMPTY_ROOT = "__EMPTY__";

    private MediaMetadataCompat track;
    private final Context context;

    UrgentTrackProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    public MediaMetadataCompat getTrack() {
        return track;
    }

    @SuppressLint("StaticFieldLeak")
    void prepareMedia(@NonNull Consumer<Boolean> callback) {

        if (track != null) {
            callback.accept(true);
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                loadData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                callback.accept(hasTrackInformation());
            }
        }.execute();
    }

    private synchronized void loadData() {
        Request<UrgentInfo> infoRequest = new UrgentInfoRequest(context);
        Result<UrgentInfo> programme = infoRequest.performRequest();

        if (!programme.hasData()) {
            // It failed.
            return;
        }
        UrgentInfo info = programme.getData();

        Bitmap albumArt = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_urgent);
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, URGENT_ID)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, info.getUrl())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);

        if (!TextUtils.isEmpty(info.getName())) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, info.getName())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, context.getString(R.string.urgent_fm));
        } else {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, context.getString(R.string.urgent_fm));
        }

        track = builder.build();
    }

    synchronized boolean hasTrackInformation() {
        return track != null;
    }
}