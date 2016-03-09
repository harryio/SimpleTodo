package io.theappx.simpletodo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import io.theappx.simpletodo.helper.RepeatInterval;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.service.TodoService;
import io.theappx.simpletodo.utils.DateUtils;
import io.theappx.simpletodo.utils.FormatUtils;

public class CreateTodoActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        ColorPickerSwatch.OnColorSelectedListener {
    public static final String SAVE_ITEM = "io.theappx.saveItem";
    public static final String IS_ITEM_DELETED = "io.theappx.isItemDeleted";
    public static final String IS_ITEM_UPDATED = "io.theappx.isItemUpdated";
    public static final String UPDATE_ITEM = "io.theappx.updateItem";
    private static final String ARG_TODO_ITEM = "io.theappx.todoItem";
    private static final int ANIM_DURATION = 500;
    private static final String STATE_TODO_INSTANCE = "io.theappx.todoInstance";
    private static final String STATE_TODO_CLONE_INSTANCE = "io.theappx.todoCloneInstance";
    private static final String STATE_NEW_INSTANCE = "io.theappx.newInstance";
    private static final String STATE_REMIND = "io.theappx.remind";
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
    @Bind(R.id.repeatView)
    DataLayout repeatView;
    int selectedItemIndex;
    private TodoItem mTodoItem, mCloneTodoItem;
    private boolean isNewTodo, reminderOn;
    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;
    private ColorPickerDialog colorPickerDialog;
    private Calendar mCalendar;

    /**
     * Call if user wants to display/edit existing item
     *
     * @param pContext  Context
     * @param pTodoItem Item to be displayed
     * @return Calling intent to start this activity containing item to be displayed in CreateTodoActivity
     */
    public static Intent getCallingIntent(Context pContext, TodoItem pTodoItem) {
        Intent lIntent = new Intent(pContext, CreateTodoActivity.class);
        lIntent.putExtra(ARG_TODO_ITEM, pTodoItem);
        return lIntent;
    }

    /**
     * Call if user wants to create new item
     *
     * @param pContext Context
     * @return Calling intent to start this activity for creating new item for CreateTodoActivity
     */
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

        //Fetch colors for ColorPicker
        int[] colorArray = getResources().getIntArray(R.array.color_array);
        int selectedColor = colorArray[0];
        mCalendar = Calendar.getInstance();

        //Proceed only if the activity is not restarted after configuration change
        if (savedInstanceState == null) {
            Intent lIntent = getIntent();
            mTodoItem = lIntent.getParcelableExtra(ARG_TODO_ITEM);

            //Check whether to proceed with creating new item or proceed with editing existing one
            if (mTodoItem == null) {
                //Proceed with creating new item

                //Set default values for new item
                mTodoItem = new TodoItem(UUID.randomUUID().toString());
                mTodoItem.setColor(colorArray[0]);
                mTodoItem.setRepeatInterval(RepeatInterval.ONE_TIME);

                //Set calendar default time ahead of 1 hour from current time
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.add(Calendar.MINUTE, 60);
                //Set flag indicating that the current item is a new one
                isNewTodo = true;
            } else {
                //Proceed with editing existing item

                // Set calendar time to item's time
                if (mTodoItem.isRemind()) mCalendar.setTime(mTodoItem.getDateInstance());
                fillTodoDataInEditText();
                //Clone current item so that changes in the current item can be checked
                //when user exits the activity
                mCloneTodoItem = new TodoItem(mTodoItem);

                //Set default values
                isNewTodo = false;
                reminderOn = mTodoItem.isRemind();
                //noinspection SuspiciousMethodCalls
                selectedColor = mTodoItem.getColor();
            }

            setUp();
        }

        setUpColorPickerDialog(colorArray, selectedColor);
        repeatView.setDataValue(mTodoItem.getRepeatInterval().toString());
    }

    /**
     * Sets up ColorPickerDialog which is used for selecting item's color
     * @param colorArray    The array of colors to be shown in dialog
     * @param selectedColor Current selected color of the item
     */
    private void setUpColorPickerDialog(int colorArray[], int selectedColor) {
        colorPickerDialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                colorArray, selectedColor, 4, ColorPickerDialog.SIZE_SMALL);
        colorPickerDialog.setOnColorSelectedListener(this);
    }

    /**
     * Sets up widgets in the layout according to new item or existing item. If
     * current item is not an existing item, then all the widgets default values will be set to
     * item's values
     */
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

    /**
     * Set existing item title and description to respective TextViews
     */
    private void fillTodoDataInEditText() {
        titleEditText.setText(mTodoItem.getTitle());
        descriptionEditText.setText(mTodoItem.getDescription());
    }

    //Save state of the activity
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_TODO_INSTANCE, mTodoItem);
        outState.putParcelable(STATE_TODO_CLONE_INSTANCE, mCloneTodoItem);
        outState.putBoolean(STATE_NEW_INSTANCE, isNewTodo);
        outState.putBoolean(STATE_REMIND, reminderOn);
    }

    //Restore state of activity
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mTodoItem = savedInstanceState.getParcelable(STATE_TODO_INSTANCE);
        mCloneTodoItem = savedInstanceState.getParcelable(STATE_TODO_CLONE_INSTANCE);
        isNewTodo = savedInstanceState.getBoolean(STATE_NEW_INSTANCE);
        reminderOn = savedInstanceState.getBoolean(STATE_REMIND);

        if (!isNewTodo) {
            if (mTodoItem.isRemind()) mCalendar.setTime(mTodoItem.getDateInstance());
            fillTodoDataInEditText();
        }

        setUp();
    }

    /**
     * Sets up title and description EditTexts
     */
    private void setUpTitleAndDescpEditText() {
        //Show soft keyboard when titleEditText gains focus
        titleEditText.requestFocus();
        showSoftKeyboard();
        if (!TextUtils.isEmpty(mTodoItem.getTitle()))
            titleEditText.setSelection(mTodoItem.getTitle().length());

        //Set current item's title to text in titleEditText
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

        //Set current item's description to text in descriptionEditText
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

    /**
     * Set up remind state of the item
     */
    private void setUpDateTimeView() {
        //Show remind status of current item
        remindView.setDataValue(reminderOn ? "ON" : "OFF");
        //Hide/Show dateTime view according to remind status
        dateTimeView.setVisibility(mTodoItem.isRemind() ? View.VISIBLE : View.GONE);
        remindView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Flip the remind flag on every click
                reminderOn = !reminderOn;
                //Refactor this change in the item
                mTodoItem.setReminderStatus(reminderOn);
                remindView.setDataValue(reminderOn ? "ON" : "OFF");

                //As soon as this view gains focus hide soft keyboard
                hideSoftKeyboardFromView(titleEditText);
                hideSoftKeyboardFromView(descriptionEditText);

                if (reminderOn) {
                    //If reminder is set, then set item's remind time to calendar's time
                    mTodoItem.setTime(mCalendar.getTimeInMillis());

                    //Show item's current date and time in the UI
                    setUpDateAndTimeEditText();
                    //Animate in remind view
                    animateInRemindView();
                    repeatView.setDataValue(mTodoItem.getRepeatInterval().toString());
                } else {
                    //Animate out remind view
                    animateOutRemindView();
                }
            }
        });
    }

    /**
     * Animates in the view for selecting date and time for item
     */
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

    /**
     * Animates out the view for selecting data and time for item
     */
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

    /**
     * Show item's date and time
     */
    private void setUpDateAndTimeEditText() {
        setUpDateEditText();
        setUpTimeEditText();
    }

    /**
     * Show item's time
     */
    private void setUpTimeEditText() {
        java.text.DateFormat dateFormat = DateFormat.getTimeFormat(this);
        timeView.setDataValue(dateFormat.format(mCalendar.getTime()));
    }

    /**
     * Show item's date
     */
    private void setUpDateEditText() {
        dateView.setDataValue(DateUtils.isToday(mCalendar) ? "Today"
                : FormatUtils.getDayStringFromDate(mCalendar.getTime()));
    }

    /**
     * Set's up toolbar for this activity
     */
    private void setUpToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Exit activity without updating/creating item. All changes to the item will be lost
                onBackPressed();
            }
        });
        //Inflate menu to show in toolbar
        toolbar.inflateMenu(R.menu.menu_create_todo_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    //Handle action for deleting item
                    case R.id.action_delete:
                        if (!isNewTodo) {
                            Intent returnIntent = new Intent();
                            //Set result that item is deleted not updated
                            returnIntent.putExtra(IS_ITEM_DELETED, true);
                            returnIntent.putExtra(IS_ITEM_UPDATED, false);
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                        return true;

                    //Handle action for changing item's color
                    case R.id.change_color:
                        colorPickerDialog.show(getFragmentManager(), "CreateTodoActivity");
                        return true;
                }
                return true;
            }
        });
    }

    /**
     * Show DatePickerDialog when user wants to pick date
     */
    @OnClick(R.id.dateView)
    public void selectDate() {
        mDatePickerDialog.show(getFragmentManager(), "Choose Date");
    }

    /**
     * Show TimePickerDialog when user wants to pick time
     */
    @OnClick(R.id.timeView)
    public void selectTime() {
        mTimePickerDialog.setStartTime(mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE));
        mTimePickerDialog.show(getFragmentManager(), "Choose Time");
    }

    /**
     * Update/Create item on fab click. Item will only be updated/created if user clicks
     * this button after making changes to the item
     */
    @OnClick(R.id.fab)
    public void onFabClick() {
        onActivityExit();
        hideSoftKeyboard();
        finish();
    }

    @OnClick(R.id.repeatView)
    public void onRepeatViewClicked() {
        final RepeatInterval[] values = RepeatInterval.values();
        int length = values.length;
        String items[] = new String[length];

        for (int i = 0; i < length; ++i) {
            items[i] = values[i].toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.RepeatIntervalDialog);
        int checkedItemIndex = mTodoItem.getRepeatInterval().ordinal();
        builder.setSingleChoiceItems(items, checkedItemIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItemIndex = which;
            }
        }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                repeatView.setDataValue(values[selectedItemIndex].toString());
                mTodoItem.setRepeatInterval(values[selectedItemIndex]);
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setTitle("Repeat");

        builder.create().show();
    }

    /**
     * Callback when user select date from DatePickerDialog
     *
     * @param view        DatePickerDialog
     * @param year        Selected year
     * @param monthOfYear Selected month
     * @param dayOfMonth  Selected day
     */
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        //Clone current calendar instance so that this does not pollute
        // current calendar's date with wrong values
        Calendar lCalendar = Calendar.getInstance();
        lCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);

        //Check selected date for the past date
        Date lDate = lCalendar.getTime();
        if (lDate.before(new Date())) {
            //Notify user about the past date
            Toast.makeText(this, "Woah there! The time machine isn't invented yet", Toast.LENGTH_SHORT).show();
        } else {
            //If selected date is valid, then set this date to the calendar instance
            mCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
            //Also refactor this date in the item
            setTodoDate();
        }
    }

    /**
     * Callback when user select time from TimePickerDialog
     *
     * @param view      TimePickerDialog
     * @param hourOfDay Selected hour
     * @param minute    Selected minute
     * @param second    Selected second
     */
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        //Clone current calendar instance so that this does not pollute current
        //calendar's time with wrong values
        Calendar lCalendar = Calendar.getInstance();
        lCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);

        //Check selected time for past time
        Date lDate = lCalendar.getTime();
        if (lDate.before(new Date())) {
            //Notify user about the past time
            Toast.makeText(this, "Woah there! The time machine isn't invented yet", Toast.LENGTH_SHORT).show();
        } else {
            //If selected time is valid, then set this time to the calendar's instance
            mCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
            //Also refactor this time in the item
            setTodoTime();
        }
    }

    /**
     * Callback when user select color from ColorPickerDialog
     *
     * @param color selected color
     */
    @Override
    public void onColorSelected(int color) {
        mTodoItem.setColor(color);
    }

    /**
     * Set item date
     */
    private void setTodoDate() {
        mTodoItem.setTime(mCalendar.getTimeInMillis());
        setUpDateEditText();
    }

    /**
     * Set item time
     */
    private void setTodoTime() {
        mTodoItem.setTime(mCalendar.getTimeInMillis());
        setUpTimeEditText();
    }

    /**
     * Called when activity is about to exit saving new/edited item
     */
    private void onActivityExit() {
        //Current item is new
        if (isNewTodo) {
            //Check for empty title. If title is empty then item is not saved to the database
            if (!TextUtils.isEmpty(mTodoItem.getTitle())) {
                //Save the new item to the database in the background thread
                TodoService.startActionSaveTodo(this, mTodoItem);
                if (mTodoItem.isRemind()) {
                    //If user has set reminder, then also create alarm at the specified time
                    // in the background thread
                    TodoService.startActionCreateAlarm(this, mTodoItem);
                }

                Intent returnIntent = new Intent();
                //Set item result that item is saved
                returnIntent.putExtra(SAVE_ITEM, mTodoItem);
                setResult(RESULT_OK, returnIntent);
            }
            //Proceed no further after this
            return;
        }

        //Current item is existing one

        //Check if reminder is changed by the user
        if (mTodoItem.isRemindStatusChanged(mCloneTodoItem)) {
            //Check how has user changed the reminder status
            if (mTodoItem.isRemind()) {
                //Reminder status is changed from "not-set" to "set",
                // then create alarm for this item
                TodoService.startActionCreateAlarm(this, mTodoItem);
                //Also remove the item done status
                mTodoItem.setDone(false);
            } else {
                //Reminder status is changed from "set" to "not_set",
                // then delete alarm for this item
                TodoService.startActionDeleteAlarm(this, mTodoItem.getId());
            }
        } else {
            //Check if user has not changed remind status,
            // then is time changed by the user? Only if reminder is set on the item
            if (mTodoItem.isRemind()) {
                if (mTodoItem.isTimeChanged(mCloneTodoItem)) {
                    //Time is changed for this item , update the alarm for this item
                    TodoService.startActionCreateAlarm(this, mTodoItem);
                    //Also remove the item done status
                    mTodoItem.setDone(false);
                }
            }
        }

        //Check all fields of item for alteration
        if (mTodoItem.isChanged(mCloneTodoItem)) {
            //At least one of the field of this item is changed

            //If current title of the item is empty after editing,
            // then restore previous title for this item
            if (TextUtils.isEmpty(titleEditText.getText())) {
                mTodoItem.setTitle(mCloneTodoItem.getTitle());
            }

            //Update edited item in database in the background thread
            TodoService.startActionSaveTodo(this, mTodoItem);

            Intent returnIntent = new Intent();
            //Set result that item is updated neither saved nor deleted
            returnIntent.putExtra(IS_ITEM_DELETED, false);
            returnIntent.putExtra(IS_ITEM_UPDATED, true);
            returnIntent.putExtra(UPDATE_ITEM, mTodoItem);
            setResult(RESULT_OK, returnIntent);
        }

    }

    /**
     * Hide soft keyboard from the window
     */
    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Hide soft keyboard from view
     *
     * @param view view from which the keyboard is to be hidden
     */
    private void hideSoftKeyboardFromView(View view) {
        view.clearFocus();
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Shows soft keyboard in the window. Usually called on the activity startup
     */
    private void showSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Activity animations
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
}
