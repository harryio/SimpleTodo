package io.theappx.simpletodo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.service.TodoService;
import io.theappx.simpletodo.utils.DateUtils;
import io.theappx.simpletodo.utils.FormatUtils;

public class CreateTodoActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private static final String ARG_TODO_ITEM = "io.theappx.todoItem";
    private static final int ANIM_DURATION = 1500;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_title)
    EditText titleEditText;
    @Bind(R.id.et_description)
    EditText descriptionEditText;
    @Bind(R.id.switch_remind)
    SwitchCompat remindSwitch;
    @Bind(R.id.et_date)
    EditText dateEditText;
    @Bind(R.id.et_time)
    EditText timeEditText;
    @Bind(R.id.remind_view)
    LinearLayout remindView;

    private TodoItem mTodoItem, mCloneTodoItem;

    private boolean isNewTodo;

    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;

    private Calendar mCalendar;

    public static Intent getCallingIntent(Context pContext, TodoItem pTodoItem) {
        Intent lIntent = new Intent(pContext, CreateTodoActivity.class);
        lIntent.putExtra(ARG_TODO_ITEM, pTodoItem);
        return lIntent;
    }

    public static Intent getCallingIntent(Context pContext) {
        return new Intent(pContext, CreateTodoActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_todo);
        ButterKnife.bind(this);

        mCalendar = Calendar.getInstance();

        Intent lIntent = getIntent();
        mTodoItem = lIntent.getParcelableExtra(ARG_TODO_ITEM);

        if (mTodoItem == null) {
            mTodoItem = new TodoItem();
            isNewTodo = true;
        } else {
            if (mTodoItem.shouldBeReminded()) mCalendar.setTime(mTodoItem.getCompleteDate());
            mCloneTodoItem = new TodoItem(mTodoItem);
            isNewTodo = false;
        }

        setUpDateAndTimeEditText();
        setUpToolbar();
        setUpSwitchCompat();

        Calendar lCalendar = Calendar.getInstance();
        mDatePickerDialog = DatePickerDialog.newInstance(
                this,
                lCalendar.get(Calendar.YEAR),
                lCalendar.get(Calendar.MONTH),
                lCalendar.get(Calendar.DAY_OF_MONTH)
        );
        mTimePickerDialog = TimePickerDialog.newInstance(
                this,
                lCalendar.get(Calendar.HOUR_OF_DAY),
                lCalendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)
        );
    }

    private void setUpSwitchCompat() {
        remindSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTodoItem.setShouldRemind(isChecked);

                if (isChecked) {
                    setUpDateAndTimeEditText();
                    animateInRemindView();
                } else {
                    animateOutRemindView();
                }
            }
        });
    }

    private void animateOutRemindView() {
        ObjectAnimator fadeInAnim = ObjectAnimator.ofFloat(remindView, "alpha", 1f, 0f);
        ObjectAnimator translateYAnim = ObjectAnimator.
                ofFloat(remindView, "translationY", 0, remindView.getHeight());
        AnimatorSet lAnimatorSet = new AnimatorSet();
        lAnimatorSet.setDuration(ANIM_DURATION);
        lAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        lAnimatorSet.playTogether(fadeInAnim, translateYAnim);
        lAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                remindView.setVisibility(View.GONE);
            }
        });
        lAnimatorSet.start();
    }

    private void animateInRemindView() {
        ObjectAnimator fadeInAnim = ObjectAnimator.ofFloat(remindView, "alpha", 0f, 1f);
        ObjectAnimator translateYAnim = ObjectAnimator.
                ofFloat(remindView, "translationY", remindView.getHeight(), 0);
        AnimatorSet lAnimatorSet = new AnimatorSet();
        lAnimatorSet.setDuration(ANIM_DURATION);
        lAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        lAnimatorSet.playTogether(fadeInAnim, translateYAnim);
        lAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                remindView.setVisibility(View.VISIBLE);
            }
        });
        lAnimatorSet.start();
    }

    private void setUpDateAndTimeEditText() {
        setUpDateEditText();
        setUpTimeEditText();
    }

    private void setUpTimeEditText() {
        timeEditText.setText(DateFormat.is24HourFormat(this) ?
                FormatUtils.get24HourTimeStringFromDate(mCalendar.getTime()) :
                FormatUtils.getTimeStringFromDate(mCalendar.getTime()));
    }

    private void setUpDateEditText() {
        dateEditText.setText(DateUtils.isToday(mCalendar) ? "Today"
                : FormatUtils.getDayStringFromDate(mCalendar.getTime()));
    }

    private void setUpToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.menu_create_todo_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        if (!isNewTodo) {
                            deleteTodoItem();
                            finish();
                        }
                        return true;
                }

                return true;
            }
        });
    }

    private void deleteTodoItem() {
        TodoService.startActionDeleteTodo(this, mTodoItem);
    }

    @OnClick(R.id.et_date)
    public void selectDate() {
        mDatePickerDialog.show(getFragmentManager(), "Choose Date");
    }

    @OnClick(R.id.et_time)
    public void selectTime() {
        mTimePickerDialog.show(getFragmentManager(), "Choose Time");
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        onActivityExit();
        finish();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar lCalendar = Calendar.getInstance();
        lCalendar.set(year, monthOfYear, dayOfMonth);

        Date lDate = lCalendar.getTime();
        if (lDate.before(new Date())) {
            Toast.makeText(this, "Woah there! The time machine isn't invented yet", Toast.LENGTH_SHORT).show();
        } else {
            mCalendar.set(year, monthOfYear, dayOfMonth);
            setTodoDate();
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar lCalendar = Calendar.getInstance();
        lCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute, 0);

        Date lDate = lCalendar.getTime();
        if (lDate.before(new Date())) {
            Toast.makeText(this, "Woah there! The time machine isn't invented yet", Toast.LENGTH_SHORT).show();
        } else {
            mCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
            setTodoTime();
        }
    }

    private void setTodoDate() {
        String dateString = FormatUtils.getDayStringFromDate(mCalendar.getTime());
        mTodoItem.setDate(dateString);
        setUpDateEditText();
    }

    private void setTodoTime() {
        String timeString = FormatUtils.getTimeStringFromDate(mCalendar.getTime());
        mTodoItem.setTime(timeString);
        setUpTimeEditText();
    }

    private void onActivityExit() {
        if (isNewTodo) {
            storeItemToDatabase();
            TodoService.startActionCreateAlarm(this, mTodoItem);
            return;
        }

        if (mTodoItem.isChanged(mCloneTodoItem)) {
            storeItemToDatabase();

            if (mTodoItem.isRemindStatusChanged(mCloneTodoItem)) {
                if (mTodoItem.shouldBeReminded()) {
                    TodoService.startActionCreateAlarm(this, mTodoItem);
                } else {
                    TodoService.startActionDeleteAlarm(this, mTodoItem.getId());
                }
            } else {
                if (mTodoItem.isDateChanged(mCloneTodoItem) || mTodoItem.isTimeChanged(mCloneTodoItem)) {
                    TodoService.startActionCreateAlarm(this, mTodoItem);
                }
            }
        }
    }

    private void storeItemToDatabase() {
        TodoService.startActionSaveTodo(this, mTodoItem);
    }

    @Override
    public void onBackPressed() {
        onActivityExit();
        super.onBackPressed();
    }
}
