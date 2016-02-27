package io.theappx.simpletodo.receiver;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

import io.theappx.simpletodo.helper.RepeatInterval;
import io.theappx.simpletodo.helper.TodoNotificationHelper;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.service.TodoService;

public class NotificationPublisher extends WakefulBroadcastReceiver {
    private static final String NOTIFICATION_ID = "io.theappx.simpletodo.NOTIFICATION_ID";
    private static final String EXTRA_TODO_ITEM = "io.theappx.simpletodo.TODO_ITEM";

    private static final int DAY_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEEK_MILLIS = DAY_MILLIS * 7;

    public static Intent getCallingIntent(Context context, TodoItem todoItem) {
        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, todoItem.getId().hashCode());
        intent.putExtra(NotificationPublisher.EXTRA_TODO_ITEM, todoItem);
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat lNotificationManagerCompat =
                NotificationManagerCompat.from(context);

        TodoItem todoItem = intent.getParcelableExtra(EXTRA_TODO_ITEM);
        TodoNotificationHelper notificationHelper = new TodoNotificationHelper();
        Notification todoNotification = notificationHelper.getTodoNotification(context, todoItem);

        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        lNotificationManagerCompat.notify(id, todoNotification);

        //Check for repeating alarm status of item and set alarm accordingly
        setRepeatingAlarm(context, todoItem);
    }

    private void setRepeatingAlarm(Context context, TodoItem todoItem) {
        RepeatInterval repeatInterval = RepeatInterval.valueOf(todoItem.getRepeatInterval());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(todoItem.getTime());

        //Add repeat interval to the calendar instance
        switch (repeatInterval) {
            case ONE_TIME:
                //This is a one time event only. No need to repeat alarm.
                break;

            case DAILY:
                calendar.add(Calendar.MILLISECOND, DAY_MILLIS);
                break;

            case WEEKLY:
                calendar.add(Calendar.MILLISECOND, WEEK_MILLIS);
                break;

            case YEARLY:
                calendar.add(Calendar.YEAR, 1);
                break;

            default: throw new IllegalStateException("No interval defined for "
                    + repeatInterval.name());

        }

        todoItem.setTime(calendar.getTimeInMillis());
        TodoService.startActionSaveTodo(context, todoItem);
    }
}
