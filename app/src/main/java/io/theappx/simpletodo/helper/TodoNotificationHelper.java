package io.theappx.simpletodo.helper;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import io.theappx.simpletodo.R;
import io.theappx.simpletodo.activity.SnoozeActivity;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.service.TodoService;

public class TodoNotificationHelper {
    public Notification getTodoNotification(Context context, TodoItem todoItem) {
        Intent contentIntent = SnoozeActivity.getCallingIntent(context, todoItem);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, todoItem.getId().hashCode(),
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deleteIntent = TodoService.getCompleteTodoIntent(context, todoItem);
        PendingIntent deleteItemPendingIntent = PendingIntent.getService(context, todoItem.getId().hashCode(),
                deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder lBuilder = new NotificationCompat.Builder(context);
        lBuilder.setTicker(todoItem.getTitle())
                .setWhen(todoItem.getTime())
                .setContentTitle(todoItem.getTitle())
                .setSmallIcon(R.drawable.alarm_check_wrapper)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        //Enable snoozing and setting item status to done only for non-repeating alarms
        if (todoItem.getRepeatInterval() == RepeatInterval.ONE_TIME) {
            lBuilder.setContentIntent(contentPendingIntent);
            lBuilder.setDeleteIntent(deleteItemPendingIntent);
        }

        String description = todoItem.getDescription();
        if (!TextUtils.isEmpty(description)) {
            lBuilder.setContentText(description);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = ContextCompat.getColor(context, R.color.colorPrimary);
            lBuilder.setColor(color);
        }

        return lBuilder.build();
    }
}
