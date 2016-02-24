package io.theappx.simpletodo.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.theappx.simpletodo.helper.AlarmHelper;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.utils.StorIOProvider;

public class TodoService extends IntentService {
    private static final String ACTION_SAVE_TODO = "io.theappx.simpletodo.action.SAVETODO";
    private static final String ACTION_DELETE_TODO = "io.theappx.simpletodo.action.DELETETODO";
    private static final String ACTION_CREATE_ALARM = "io.theappx.simpletodo.action.CREATEALARM";
    private static final String ACTION_DELETE_ALARM = "io.theappx.simpletodo.action.DELETEALARM";
    private static final String ACTION_DELETE_ALL = "io.theappx.simpletodo.action.DELETEALL";
    private static final String ACTION_COMPLETE_TODO = "io.theappx.simpletodo.action.COMPLETETODO";

    private static final String EXTRA_TODO = "io.theappx.simpletodo.extra.TODO";
    private static final String EXTRA_TODO_ID = "io.theappx.simpletodo.extra.TODO_ID";
    private static final String EXTRA_TODO_LIST = "io.theappx.simpletodo.extra.TODO_LIST";

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
        intent.setAction(ACTION_DELETE_TODO);
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
        intent.setAction(ACTION_DELETE_ALARM);
        intent.putExtra(EXTRA_TODO_ID, todoId);
        pContext.startService(intent);
    }

    public static void startActionDeleteAll(Context context, List<TodoItem> todoItems) {
        Intent intent = new Intent(context, TodoService.class);
        intent.setAction(ACTION_DELETE_ALL);
        ArrayList<TodoItem> todoItemArrayList = new ArrayList<>(todoItems);
        intent.putParcelableArrayListExtra(EXTRA_TODO_LIST, todoItemArrayList);
        context.startService(intent);
    }

    public static void startActionCompleteTodo(Context context, TodoItem todoItem) {
        context.startService(getCompleteTodoIntent(context, todoItem));
    }

    public static Intent getCompleteTodoIntent(Context context, TodoItem todoItem) {
        Intent intent = new Intent(context, TodoService.class);
        intent.setAction(ACTION_COMPLETE_TODO);
        intent.putExtra(EXTRA_TODO, todoItem);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE_TODO.equals(action)) {
                final TodoItem lTodoItem = intent.getParcelableExtra(EXTRA_TODO);
                handleActionSaveTodo(lTodoItem);
            } else if(ACTION_DELETE_TODO.equals(action)) {
                final TodoItem todoItem = intent.getParcelableExtra(EXTRA_TODO);
                handleActionDeleteTodo(todoItem);
            } else if (ACTION_CREATE_ALARM.equals(action)) {
                final TodoItem lTodoItem = intent.getParcelableExtra(EXTRA_TODO);
                handleCreateAlarm(lTodoItem);
            } else if (ACTION_DELETE_ALARM.equals(action)) {
                final String todoItemId = intent.getStringExtra(EXTRA_TODO_ID);
                handleActionDeleteAlarm(todoItemId);
            } else if (ACTION_DELETE_ALL.equals(action)) {
                final List<TodoItem> todoItems = intent.getParcelableArrayListExtra(EXTRA_TODO_LIST);
                handleActionDeleteAll(todoItems);
            } else if (ACTION_COMPLETE_TODO.equals(action)) {
                final TodoItem todoItem = intent.getParcelableExtra(EXTRA_TODO);
                handleActionCompleteTodo(todoItem);
            }
        }
    }

    private void handleActionCompleteTodo(TodoItem todoItem) {
        todoItem.setDone(true);
        handleActionSaveTodo(todoItem);
    }

    private void handleActionSaveTodo(TodoItem pTodoItem) {
        StorIOSQLite lStorIoSQLite = StorIOProvider.getInstance(getApplicationContext());
        lStorIoSQLite
                .put()
                .object(pTodoItem)
                .prepare()
                .executeAsBlocking();
    }

    private void handleActionDeleteTodo(TodoItem todoItem) {
        StorIOSQLite storIOSQLite = StorIOProvider.getInstance(getApplicationContext());
        storIOSQLite
                .delete()
                .object(todoItem)
                .prepare()
                .executeAsBlocking();

        if (todoItem.isRemind())
            handleActionDeleteAlarm(todoItem.getId());
    }

    private void handleActionDeleteAll(List<TodoItem> todoItems) {
        StorIOSQLite storIOSQLite = StorIOProvider.getInstance(getApplicationContext());
        storIOSQLite
                .delete()
                .objects(todoItems)
                .prepare()
                .executeAsBlocking();

        int length = todoItems.size();
        for (int i = 0; i < length; ++i) {
            TodoItem todoItem = todoItems.get(i);
            if (todoItem.isRemind()) {
                handleActionDeleteAlarm(todoItem.getId());
            }
        }
    }

    private void handleActionDeleteAlarm(String todoItemId) {
        AlarmHelper alarmHelper = new AlarmHelper();
        alarmHelper.deleteAlarm(this, todoItemId);
    }

    private void handleCreateAlarm(TodoItem pTodoItem) {
        Date alarmDate = new Date(pTodoItem.getTime());
        //Only create alarm if item time is after current time
        if (alarmDate.after(new Date())) {
            AlarmHelper alarmHelper = new AlarmHelper();
            alarmHelper.createAlarm(this, pTodoItem);
        }
    }
}
