package io.theappx.simpletodo.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);
        MovementMethod movementMethod = LinkMovementMethod.getInstance();
        contactTextView.setMovementMethod(movementMethod);
        githubTextView.setMovementMethod(movementMethod);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = "Version " + packageInfo.versionName;
            versionTextView.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
