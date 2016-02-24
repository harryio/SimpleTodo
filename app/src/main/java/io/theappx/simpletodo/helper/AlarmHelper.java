package io.theappx.simpletodo.helper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.receiver.NotificationPublisher;

public class AlarmHelper {
    public void createAlarm(Context context, TodoItem todoItem) {
        TodoNotificationHelper notificationHelper = new TodoNotificationHelper();
        Notification todoNotification = notificationHelper.getTodoNotification(context, todoItem);

        Intent lIntent = new Intent(context, NotificationPublisher.class);
        lIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, todoItem.getId().hashCode());
        lIntent.putExtra(NotificationPublisher.NOTIFICATION, todoNotification);
        PendingIntent lPendingIntent = PendingIntent.getBroadcast(context, todoItem.getId().hashCode(),
                lIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long timeInMillis = todoItem.getTime();

        AlarmManager lAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        lAlarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, lPendingIntent);
    }

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
