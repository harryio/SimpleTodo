package io.theappx.simpletodo.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.receiver.NotificationPublisher;

/**
 * Helper class providing methods to create/delete alarm.
 */
public class AlarmHelper {
    /**
     * Create alarm
     * @param context context
     * @param todoItem item for which alarm is to set
     */
    public void createAlarm(Context context, TodoItem todoItem) {
        Intent intent = NotificationPublisher.getCallingIntent(context, todoItem);
        PendingIntent lPendingIntent = PendingIntent.getBroadcast(context, todoItem.getId().hashCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long timeInMillis = todoItem.getTime();

        AlarmManager lAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        lAlarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, lPendingIntent);
    }

    /**
     * Delete alarm
     * @param context context
     * @param todoItemId item for which alarm is to be deleted
     */
    public void deleteAlarm(Context context, String todoItemId) {
        Intent intent = new Intent(context, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, todoItemId.hashCode(),
                intent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null) {
            pendingIntent.cancel();
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(pendingIntent);
        }
    }
}
