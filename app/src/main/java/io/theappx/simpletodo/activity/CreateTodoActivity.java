package io.theappx.simpletodo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.customview.DataLayout;
import io.theappx.simpletodo.customview.colorpicker.ColorPickerDialog;
import io.theappx.simpletodo.customview.colorpicker.ColorPickerSwatch;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.service.TodoService;
import io.theappx.simpletodo.utils.DateUtils;
import io.theappx.simpletodo.utils.FormatUtils;

public class CreateTodoActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        ColorPickerSwatch.OnColorSelectedListener {
    private static final String ARG_TODO_ITEM = "io.theappx.todoItem";
    private static final int ANIM_DURATION = 1000;
    private static final String STATE_TODO_INSTANCE = "io.theappx.todoInstance";
    private static final String STATE_TODO_CLONE_INSTANCE = "io.theappx.todoCloneInstance";
    private static final String STATE_NEW_INSTANCE = "io.theappx.newInstance";
    private static final String STATE_REMIND = "io.theappx.remind";

    public static final String SAVE_ITEM = "io.theappx.saveItem";
    public static final String IS_ITEM_DELETED = "io.theappx.isItemDeleted";
    public static final String IS_ITEM_UPDATED = "io.theappx.isItemUpdated";
    public static final String UPDATE_ITEM = "io.theappx.updateItem";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    EditText titleEditText;
    @Bind(R.id.description)
    EditText descriptionEditText;
    @Bind(R.id.dateTimeView)
    LinearLayout dateTimeView;
    @Bind(R.id.remindView)
    DataLayout remindView;
    @Bind(R.id.dateView)
    DataLayout dateView;
    @Bind(R.id.timeView)
    DataLayout timeView;

    private TodoItem mTodoItem, mCloneTodoItem;

    private boolean isNewTodo, reminderOn;

    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;

    private ColorPickerDialog colorPickerDialog;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        }
        ButterKnife.bind(this);

        int[] colorArray = getResources().getIntArray(R.array.color_array);
        colorPickerDialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                colorArray, colorArray[0], 3, ColorPickerDialog.SIZE_SMALL);
        colorPickerDialog.setOnColorSelectedListener(this);
        mCalendar = Calendar.getInstance();

        if (savedInstanceState == null) {
            Intent lIntent = getIntent();
            mTodoItem = lIntent.getParcelableExtra(ARG_TODO_ITEM);

            if (mTodoItem == null) {
                mTodoItem = new TodoItem(UUID.randomUUID().toString());
                mTodoItem.setColor(colorArray[0]);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MINUTE, mCalendar.get(Calendar.MINUTE) + 2);
                isNewTodo = true;
            } else {
                if (mTodoItem.shouldBeReminded()) mCalendar.setTime(mTodoItem.getDateInstance());
                fillTodoDataInEditText();
                mCloneTodoItem = new TodoItem(mTodoItem);
                isNewTodo = false;
                reminderOn = mTodoItem.shouldBeReminded();
            }

            setUp();
        }
    }

    private void setUp() {
        setUpTitleAndDescpEditText();
        setUpDateAndTimeEditText();
        setUpToolbar();
        setUpDateTimeView();

        mDatePickerDialog = DatePickerDialog.newInstance(
                this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
        );
        mTimePickerDialog = TimePickerDialog.newInstance(
                this,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)
        );
    }

    private void fillTodoDataInEditText() {
        titleEditText.setText(mTodoItem.getTitle());
        descriptionEditText.setText(mTodoItem.getDescription());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_TODO_INSTANCE, mTodoItem);
        outState.putParcelable(STATE_TODO_CLONE_INSTANCE, mCloneTodoItem);
        outState.putBoolean(STATE_NEW_INSTANCE, isNewTodo);
        outState.putBoolean(STATE_REMIND, reminderOn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mTodoItem = savedInstanceState.getParcelable(STATE_TODO_INSTANCE);
        mCloneTodoItem = savedInstanceState.getParcelable(STATE_TODO_CLONE_INSTANCE);
        isNewTodo = savedInstanceState.getBoolean(STATE_NEW_INSTANCE);
        reminderOn = savedInstanceState.getBoolean(STATE_REMIND);

        if (!isNewTodo) {
            if (mTodoItem.shouldBeReminded()) mCalendar.setTime(mTodoItem.getDateInstance());
            fillTodoDataInEditText();
        }

        setUp();
    }

    private void setUpTitleAndDescpEditText() {
        titleEditText.requestFocus();
        showSoftKeyboard();
        if (!TextUtils.isEmpty(mTodoItem.getTitle())) titleEditText.setSelection(mTodoItem.getTitle().length());

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodoItem.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodoItem.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setUpDateTimeView() {
        remindView.setDataValue(reminderOn ? "ON" : "OFF");
        dateTimeView.setVisibility(mTodoItem.shouldBeReminded() ? View.VISIBLE : View.GONE);
        remindView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderOn = !reminderOn;
                mTodoItem.setShouldRemind(reminderOn);
                remindView.setDataValue(reminderOn ? "ON" : "OFF");
                hideSoftKeyboardFromView(titleEditText);
                hideSoftKeyboardFromView(descriptionEditText);

                if (reminderOn) {
                    mTodoItem.setTime(mCalendar.getTimeInMillis());

                    setUpDateAndTimeEditText();
                    animateInRemindView();
                } else {
                    animateOutRemindView();
                }
            }
        });
    }

    private void animateOutRemindView() {
        ObjectAnimator fadeInAnim = ObjectAnimator.ofFloat(dateTimeView, "alpha", 1f, 0f);
        ObjectAnimator translateYAnim = ObjectAnimator.
                ofFloat(dateTimeView, "translationY", 0, dateTimeView.getHeight());
        AnimatorSet lAnimatorSet = new AnimatorSet();
        lAnimatorSet.setDuration(ANIM_DURATION);
        lAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        lAnimatorSet.playTogether(fadeInAnim, translateYAnim);
        lAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dateTimeView.setVisibility(View.GONE);
            }
        });
        lAnimatorSet.start();
    }

    private void animateInRemindView() {
        ObjectAnimator fadeInAnim = ObjectAnimator.ofFloat(dateTimeView, "alpha", 0f, 1f);
        ObjectAnimator translateYAnim = ObjectAnimator.
                ofFloat(dateTimeView, "translationY", dateTimeView.getHeight(), 0);
        AnimatorSet lAnimatorSet = new AnimatorSet();
        lAnimatorSet.setDuration(ANIM_DURATION);
        lAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        lAnimatorSet.playTogether(fadeInAnim, translateYAnim);
        lAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                dateTimeView.setVisibility(View.VISIBLE);
            }
        });
        lAnimatorSet.start();
    }

    private void setUpDateAndTimeEditText() {
        setUpDateEditText();
        setUpTimeEditText();
    }

    private void setUpTimeEditText() {
        java.text.DateFormat dateFormat = DateFormat.getTimeFormat(this);
        timeView.setDataValue(dateFormat.format(mCalendar.getTime()));
    }

    private void setUpDateEditText() {
        dateView.setDataValue(DateUtils.isToday(mCalendar) ? "Today"
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
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(IS_ITEM_DELETED, true);
                            returnIntent.putExtra(IS_ITEM_UPDATED, false);
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                        return true;

                    case R.id.change_color:
                        colorPickerDialog.show(getFragmentManager(), "CreateTodoActivity");
                        return true;
                }
                return true;
            }
        });
    }

    @OnClick(R.id.dateView)
    public void selectDate() {
        mDatePickerDialog.show(getFragmentManager(), "Choose Date");
    }

    @OnClick(R.id.timeView)
    public void selectTime() {
        mTimePickerDialog.show(getFragmentManager(), "Choose Time");
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        onActivityExit();
        hideSoftKeyboard();
        finish();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        Calendar lCalendar = Calendar.getInstance();
        lCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);

        Date lDate = lCalendar.getTime();
        if (lDate.before(new Date())) {
            Toast.makeText(this, "Woah there! The time machine isn't invented yet", Toast.LENGTH_SHORT).show();
        } else {
            mCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
            setTodoDate();
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar lCalendar = Calendar.getInstance();
        lCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);

        Date lDate = lCalendar.getTime();
        if (lDate.before(new Date())) {
            Toast.makeText(this, "Woah there! The time machine isn't invented yet", Toast.LENGTH_SHORT).show();
        } else {
            mCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
            setTodoTime();
        }
    }

    @Override
    public void onColorSelected(int color) {
        mTodoItem.setColor(color);
    }

    private void setTodoDate() {
        mTodoItem.setTime(mCalendar.getTimeInMillis());
        setUpDateEditText();
    }

    private void setTodoTime() {
        mTodoItem.setTime(mCalendar.getTimeInMillis());
        setUpTimeEditText();
    }

    private void onActivityExit() {
        if (isNewTodo) {
            if (!TextUtils.isEmpty(mTodoItem.getTitle())) {
                storeItemToDatabase();
                if (mTodoItem.shouldBeReminded()) {
                    TodoService.startActionCreateAlarm(this, mTodoItem);
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra(SAVE_ITEM, mTodoItem);
                setResult(RESULT_OK, returnIntent);
            }
            return;
        }

        if (mTodoItem.isChanged(mCloneTodoItem)) {
            if (TextUtils.isEmpty(titleEditText.getText())) {
                mTodoItem.setTitle(mCloneTodoItem.getTitle());
            }

            storeItemToDatabase();

            Intent returnIntent = new Intent();
            returnIntent.putExtra(IS_ITEM_DELETED, false);
            returnIntent.putExtra(IS_ITEM_UPDATED, true);
            returnIntent.putExtra(UPDATE_ITEM, mTodoItem);
            setResult(RESULT_OK, returnIntent);
        }

        if (mTodoItem.isRemindStatusChanged(mCloneTodoItem)) {
            if (mTodoItem.shouldBeReminded()) {
                TodoService.startActionCreateAlarm(this, mTodoItem);
            } else {
                TodoService.startActionDeleteAlarm(this, mTodoItem.getId());
            }
        } else {
            if (mTodoItem.shouldBeReminded()) {
                if (mTodoItem.isTimeChanged(mCloneTodoItem)) {
                    TodoService.startActionCreateAlarm(this, mTodoItem);
                }
            }
        }
    }

    private void storeItemToDatabase() {
        TodoService.startActionSaveTodo(this, mTodoItem);
    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void hideSoftKeyboardFromView(View view) {
        view.clearFocus();
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
