package io.theappx.simpletodo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.customview.DataLayout;

public class SnoozeActivity extends AppCompatActivity {
    @Bind(R.id.data_layout)
    DataLayout dataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);

        ButterKnife.bind(this);
        dataLayout.setDefaultValue("10 minutes");
    }

}
