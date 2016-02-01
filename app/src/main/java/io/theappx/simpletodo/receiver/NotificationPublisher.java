package io.theappx.simpletodo.receiver;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationPublisher extends BroadcastReceiver {
    public static final String NOTIFICATION_ID = "io.theappx.simpletodo.NOTIFICATION_ID";
    public static final String NOTIFICATION = "io.theappx.simpletodo.NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat lNotificationManagerCompat =
                NotificationManagerCompat.from(context);

        Notification lNotification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        lNotificationManagerCompat.notify(id, lNotification);

        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone notificationSound = RingtoneManager.getRingtone(context, uri);
            notificationSound.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
