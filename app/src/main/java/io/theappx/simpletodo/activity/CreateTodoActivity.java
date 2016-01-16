package io.theappx.simpletodo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.theappx.simpletodo.R;

public class CreateTodoActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_title)
    EditText titleEditText;
    @Bind(R.id.et_description)
    EditText descriptionEditText;
    @Bind(R.id.switch_remind)
    SwitchCompat remindSwitch;

    LinearLayout remindView;

    DatePickerDialog mDatePickerDialog;
    TimePickerDialog mTimePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_todo);
        ButterKnife.bind(this);

        setUpToolbar();

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
                true
        );
    }

    private void setUpToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @OnClick(R.id.et_date)
    public void selectDate() {
        mDatePickerDialog.show(getFragmentManager(), "Choose Date");
    }

    @OnClick(R.id.et_time)
    public void selectTime() {
        mTimePickerDialog.show(getFragmentManager(), "Choose Time");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

    }
}
