package io.theappx.simpletodo.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.utils.StorIOProvider;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TodoService extends IntentService {
    private static final String ACTION_SAVE_TDOD = "io.theappx.simpletodo.action.SAVETODO";

    private static final String EXTRA_TODO = "io.theappx.simpletodo.extra.TODO";

    public TodoService() {
        super("TodoService");
    }

    public static void startActionSaveTodo(Context context, TodoItem pTodoItem) {
        Intent intent = new Intent(context, TodoService.class);
        intent.setAction(ACTION_SAVE_TDOD);
        intent.putExtra(EXTRA_TODO, pTodoItem);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE_TDOD.equals(action)) {
                final TodoItem lTodoItem = intent.getParcelableExtra(EXTRA_TODO);
                handleActionSaveTodo(lTodoItem);
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
}
