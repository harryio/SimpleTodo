package io.theappx.simpletodo.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.theappx.simpletodo.R;

public class AboutActivity extends AppCompatActivity {

    @Bind(R.id.version)
    TextView versionTextView;
    @Bind(R.id.contact)
    TextView contactTextView;
    @Bind(R.id.github)
    TextView githubTextView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //Set status bar color manually due to a bug in android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        ButterKnife.bind(this);
        //Highlight hyperlinks in TextViews
        MovementMethod movementMethod = LinkMovementMethod.getInstance();
        contactTextView.setMovementMethod(movementMethod);
        githubTextView.setMovementMethod(movementMethod);

        try {
            //Fetch version name
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = "Version " + packageInfo.versionName;
            versionTextView.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
