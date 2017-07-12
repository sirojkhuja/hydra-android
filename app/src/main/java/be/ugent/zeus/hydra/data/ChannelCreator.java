package be.ugent.zeus.hydra.data;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import be.ugent.zeus.hydra.R;

/**
 * Creates the channels for the notifications.
 *
 * @author Niko Strijbol
 */
public class ChannelCreator {

    public static final String MINERVA_ANNOUNCEMENT_CHANNEL = "be.ugent.zeus.hydra.notifications.minerva.announcements";
    public static final String MINERVA_ACCOUNT_CHANNEL = "be.ugent.zeus.hydra.notifications.minerva";
    public static final String SKO_CHANNEL = "be.ugent.zeus.hydra.notifications.sko";

    private final Context context;
    private final NotificationManager notificationManager;

    private ChannelCreator(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = context.getSystemService(NotificationManager.class);
    }

    private static ChannelCreator instance;

    public static synchronized ChannelCreator getInstance(Context context) {
        if (instance == null) {
            instance = new ChannelCreator(context);
        }
        return instance;
    }

    /**
     * Create a channel for the Minerva notifications.
     */
    public void createMinervaAnnouncementChannel() {

        // Don't do anything on older versions.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        String channelName = context.getString(R.string.channel_minerva_announcements);
        String channelDescription = context.getString(R.string.channel_minerva_announcements_desc);

        NotificationChannel channel = new NotificationChannel(MINERVA_ANNOUNCEMENT_CHANNEL, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(channelDescription);
        channel.enableLights(true);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Create a channel for maintenance and other app-specific notifications about Minerva.
     */
    public void createMinervaAccountChannel() {

        // Don't do anything on older versions.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        String channelName = context.getString(R.string.channel_minerva_account);
        String channelDescription = context.getString(R.string.channel_minerva_account_desc);

        NotificationChannel channel = new NotificationChannel(MINERVA_ACCOUNT_CHANNEL, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(channelDescription);
        channel.enableLights(true);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Create a channel for SKO stuff.
     */
    public void createSkoChannel() {

        // Don't do anything on older versions.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        String channelName = context.getString(R.string.channel_sko);
        String channelDescription = context.getString(R.string.channel_sko_desc);

        NotificationChannel channel = new NotificationChannel(SKO_CHANNEL, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(channelDescription);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[] { 1000, 1000});
        notificationManager.createNotificationChannel(channel);
    }
}
