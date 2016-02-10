package io.theappx.simpletodo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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
        dataLayout.setDefaultValue("10 minutes");

        todoItem = getIntent().getParcelableExtra(ARG_TODO_ITEM);
        titleTextView.setText(todoItem.getTitle());
    }

    @OnClick(R.id.delete_button)
    public void onDeleteButtonPressed() {
        TodoService.startActionDeleteTodo(this, todoItem);
        finish();
    }
}
