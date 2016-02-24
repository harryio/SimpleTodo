package io.theappx.simpletodo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.Date;
import java.util.List;

import io.theappx.simpletodo.database.TodoContract;
import io.theappx.simpletodo.helper.AlarmHelper;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.utils.StorIOProvider;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        StorIOSQLite storIOSQLite = StorIOProvider.getInstance(context);

        storIOSQLite
                .get()
                .listOfObjects(TodoItem.class)
                .withQuery(Query
                        .builder()
                        .table(TodoContract.TABLE_NAME)
                        .build())
                .prepare()
                .createObservable()
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<List<TodoItem>, Observable<TodoItem>>() {
                    @Override
                    public Observable<TodoItem> call(List<TodoItem> todoItems) {
                        return Observable.from(todoItems);
                    }
                })
                .filter(new Func1<TodoItem, Boolean>() {
                    @Override
                    public Boolean call(TodoItem todoItem) {
                        return todoItem.isRemind();
                    }
                })
                .filter(new Func1<TodoItem, Boolean>() {
                    @Override
                    public Boolean call(TodoItem todoItem) {
                        return !todoItem.isDone();
                    }
                })
                .filter(new Func1<TodoItem, Boolean>() {
                    @Override
                    public Boolean call(TodoItem todoItem) {
                        Date todoDate = new Date(todoItem.getTime());
                        return todoDate.after(new Date());
                    }
                })
                .toList()
                .subscribe(new Subscriber<List<TodoItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<TodoItem> todoItems) {
                        createAlarms(context, todoItems);
                    }
                });
    }

    private void createAlarms(Context context, List<TodoItem> todoItems) {
        AlarmHelper alarmHelper = new AlarmHelper();

        int length = todoItems.size();
        for (int i = 0; i < length; ++i) {
            TodoItem todoItem = todoItems.get(i);
            alarmHelper.createAlarm(context, todoItem);
        }
    }
}
