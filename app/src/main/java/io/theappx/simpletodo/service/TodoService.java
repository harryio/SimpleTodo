package io.theappx.simpletodo.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import io.theappx.simpletodo.R;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.receiver.NotificationPublisher;
import io.theappx.simpletodo.utils.StorIOProvider;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TodoService extends IntentService {
    private static final String ACTION_SAVE_TDOD = "io.theappx.simpletodo.action.SAVETODO";
    private static final String ACTION_CREATE_ALARM = "io.theappx.simpletodo.action.CREATEALARM";

    private static final String EXTRA_TODO = "io.theappx.simpletodo.extra.TODO";

    public TodoService() {
        super("TodoService");
    }

    public static void startActionSaveTodo(Context pContext, TodoItem pTodoItem) {
        Intent intent = new Intent(pContext, TodoService.class);
        intent.setAction(ACTION_SAVE_TDOD);
        intent.putExtra(EXTRA_TODO, pTodoItem);
        pContext.startService(intent);
    }

    public static void startActionCreateAlarm(Context pContext, TodoItem pTodoItem) {
        Intent intent = new Intent(pContext, TodoService.class);
        intent.setAction(ACTION_CREATE_ALARM);
        intent.putExtra(EXTRA_TODO, pTodoItem);
        pContext.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE_TDOD.equals(action)) {
                final TodoItem lTodoItem = intent.getParcelableExtra(EXTRA_TODO);
                handleActionSaveTodo(lTodoItem);
            } else if (ACTION_SAVE_TDOD.equals(action)) {
                final TodoItem lTodoItem = intent.getParcelableExtra(EXTRA_TODO);
                handleCreateAlarm(lTodoItem);
            }
        }
    }

    private void handleActionSaveTodo(TodoItem pTodoItem) {
        StorIOSQLite lStorIoSQLite = StorIOProvider.getInstance(getApplicationContext());
        lStorIoSQLite
                .put()
                .object(pTodoItem)
                .prepare()
                .executeAsBlocking();
    }

    private void handleCreateAlarm(TodoItem pTodoItem) {
        scheduleNotification(pTodoItem, getNotification(pTodoItem));
    }

    private void scheduleNotification(TodoItem pTodoItem, Notification pNotification) {
        Intent lIntent = new Intent(this, NotificationPublisher.class);
        lIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, pTodoItem.getId());
        lIntent.putExtra(NotificationPublisher.NOTIFICATION, pNotification);
        PendingIntent lPendingIntent = PendingIntent.getBroadcast(this, pTodoItem.getId().hashCode()
                , lIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long timeInMillis = pTodoItem.getCompleteDate().getTime();

        AlarmManager lAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        lAlarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, lPendingIntent);
    }

    private Notification getNotification(TodoItem pTodoItem) {
        Notification.Builder lBuilder = new Notification.Builder(this);
        lBuilder.setTicker(pTodoItem.getTitle())
                .setContentTitle(pTodoItem.getTitle())
                .setSmallIcon(R.drawable.ic_alarm)
                .setAutoCancel(true);

        return lBuilder.build();
    }
}
