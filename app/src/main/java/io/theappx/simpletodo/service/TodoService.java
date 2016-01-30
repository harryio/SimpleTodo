package io.theappx.simpletodo.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;

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
    private static final String ACTION_SAVE_TODO = "io.theappx.simpletodo.action.SAVETODO";
    private static final String ACTION_DELETE_TODO = "io.theappx.simpletodo.action.DELETETODO";
    private static final String ACTION_CREATE_ALARM = "io.theappx.simpletodo.action.CREATEALARM";
    private static final String ACTION_DELETE_ALARM = "io.theappx.simpletodo.action.DELETEALARM";

    private static final String EXTRA_TODO = "io.theappx.simpletodo.extra.TODO";
    private static final String EXTRA_TODO_ID = "io.theappx.simpletodo.extra.TODO_ID";

    public TodoService() {
        super("TodoService");
    }

    public static void startActionSaveTodo(Context pContext, TodoItem pTodoItem) {
        Intent intent = new Intent(pContext, TodoService.class);
        intent.setAction(ACTION_SAVE_TODO);
        intent.putExtra(EXTRA_TODO, pTodoItem);
        pContext.startService(intent);
    }

    public static void startActionDeleteTodo(Context context, TodoItem todoItem) {
        Intent intent = new Intent(context, TodoService.class);
        intent.setAction(ACTION_DELETE_ALARM);
        intent.putExtra(EXTRA_TODO, todoItem);
        context.startService(intent);
    }

    public static void startActionCreateAlarm(Context pContext, TodoItem pTodoItem) {
        Intent intent = new Intent(pContext, TodoService.class);
        intent.setAction(ACTION_CREATE_ALARM);
        intent.putExtra(EXTRA_TODO, pTodoItem);
        pContext.startService(intent);
    }

    public static void startActionDeleteAlarm(Context pContext, String todoId) {
        Intent intent = new Intent(pContext, TodoService.class);
        intent.setAction(ACTION_DELETE_TODO);
        intent.putExtra(EXTRA_TODO, todoId);
        pContext.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE_TODO.equals(action)) {
                final TodoItem lTodoItem = intent.getParcelableExtra(EXTRA_TODO);
                handleActionSaveTodo(lTodoItem);
            } else if(ACTION_DELETE_ALARM.equals(action)) {
                final TodoItem todoItem = intent.getParcelableExtra(EXTRA_TODO);
                handleActionDeleteTodo(todoItem);
            } else if (ACTION_CREATE_ALARM.equals(action)) {
                final TodoItem lTodoItem = intent.getParcelableExtra(EXTRA_TODO);
                handleCreateAlarm(lTodoItem);
            } else if (ACTION_DELETE_ALARM.equals(action)) {
                final String todoItemId = intent.getParcelableExtra(EXTRA_TODO_ID);
                handleActionDeleteAlarm(todoItemId);
            }
        }
    }

    private void handleActionSaveTodo(TodoItem pTodoItem) {
        StorIOSQLite lStorIoSQLite = StorIOProvider.getInstance(getApplicationContext());
        PutResult putResult = lStorIoSQLite
                .put()
                .object(pTodoItem)
                .prepare()
                .executeAsBlocking();
        Log.i("TodoService", "Inside saveTodo");
        Log.i("TodoService", "Was updated: " + putResult.wasUpdated());
    }

    private void handleActionDeleteTodo(TodoItem todoItem) {
        StorIOSQLite storIOSQLite = StorIOProvider.getInstance(getApplicationContext());
        storIOSQLite
                .delete()
                .object(todoItem)
                .prepare()
                .executeAsBlocking();

        if (todoItem.shouldBeReminded())
            handleActionDeleteAlarm(todoItem.getId());
    }

    private void handleActionDeleteAlarm(String todoItemId) {
        Intent intent = new Intent(this, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, todoItemId.hashCode(),
                intent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null) {
            pendingIntent.cancel();
            ((AlarmManager) getSystemService(ALARM_SERVICE)).cancel(pendingIntent);
        }
    }

    private void handleCreateAlarm(TodoItem pTodoItem) {
        scheduleNotification(pTodoItem, getNotification(pTodoItem));
    }

    private void scheduleNotification(TodoItem pTodoItem, Notification pNotification) {
        Intent lIntent = new Intent(this, NotificationPublisher.class);
        lIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, pTodoItem.getId().hashCode());
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = ContextCompat.getColor(this, R.color.colorPrimary);
            lBuilder.setColor(color);
        }

        return lBuilder.build();
    }
}
