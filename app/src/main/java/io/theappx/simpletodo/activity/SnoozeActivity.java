package io.theappx.simpletodo.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.customview.DataLayout;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.service.TodoService;

public class SnoozeActivity extends AppCompatActivity {
    private static final String ARG_TODO_ITEM = "io.theappx.todoitem";

    @Bind(R.id.title_textView)
    TextView titleTextView;
    @Bind(R.id.data_layout)
    DataLayout dataLayout;

    private TodoItem todoItem;
    private Calendar calendar;

    public static Intent getCallingIntent(Context context, TodoItem todoItem) {
        Intent intent = new Intent(context, SnoozeActivity.class);
        intent.putExtra(ARG_TODO_ITEM, todoItem);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);
        ButterKnife.bind(this);

        //Set default snooze duration to 10 minutes
        dataLayout.setDataValue("10 minutes");

        todoItem = getIntent().getParcelableExtra(ARG_TODO_ITEM);
        titleTextView.setText(todoItem.getTitle());
        calendar = Calendar.getInstance();
        //Set calendar time to item's time
        calendar.setTimeInMillis(todoItem.getTime());
        //Set calendar time ahead by default snooze time
        calendar.add(Calendar.MINUTE, 10);
    }

    @OnClick(R.id.delete_button)
    public void onDeleteButtonPressed() {
        //Delete item and finish the actvity
        TodoService.startActionDeleteTodo(this, todoItem);
        finish();
    }

    @OnClick(R.id.data_layout)
    public void onDataLayoutClicked() {
        //Show snooze time picker dialog
        getDialog().show();
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        //Add snooze duration to current item's item, create updated alarm
        // and save the item to database
        todoItem.setTime(calendar.getTimeInMillis());
        TodoService.startActionCreateAlarm(this, todoItem);
        TodoService.startActionSaveTodo(this, todoItem);
        finish();
    }

    /**
     * Crete dialog to choose snooze duration
     * @return dialog providing snooze durations to choose from
     */
    private Dialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] items = {"10 minutes", "30 minutes", "1 hour"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(todoItem.getTime());

                dataLayout.setDataValue(items[which]);
                switch (which) {
                    case 0:
                        calendar.add(Calendar.MINUTE, 10);
                        break;

                    case 1:
                        calendar.add(Calendar.MINUTE, 30);
                        break;

                    case 2:
                        calendar.add(Calendar.MINUTE, 60);
                        break;

                }

                SnoozeActivity.this.calendar = calendar;
            }
        }).setTitle("Chose snooze time");

        return builder.create();
    }

    @Override
    public void onBackPressed() {
        //Don't create alarm
        //Don't delete item
        //Set item done status to true and save the item in database
        TodoService.startActionCompleteTodo(this, todoItem);
        super.onBackPressed();
    }
}
