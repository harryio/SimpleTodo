package io.theappx.simpletodo.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.theappx.simpletodo.R;

public class DataLayout extends RelativeLayout {
    TextView defaultValueTextView;

    public DataLayout(Context context) {
        super(context, null);
    }

    public DataLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DataLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public DataLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.data_layout, this, true);
        ImageView imageView = (ImageView) findViewById(R.id.data_image);
        TextView titleTextView = (TextView) findViewById(R.id.data_title);
        defaultValueTextView = (TextView) findViewById(R.id.data_default_value);

        String dataTitle = "";
        String dataDefaultValue = "";
        Drawable drawable;

        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.Data, 0, 0);
        try {
            dataTitle = typedArray.getString(R.styleable.Data_dataTitle);
            dataDefaultValue = typedArray.getString(R.styleable.Data_dataDefaultValue);

            drawable = typedArray.getDrawable(R.styleable.Data_dataIcon);
        } finally {
            typedArray.recycle();
        }

        titleTextView.setText(dataTitle);
        defaultValueTextView.setText(dataDefaultValue);
        imageView.setImageDrawable(drawable);
    }

    public void setDataValue(String defaultValue) {
        defaultValueTextView.setText(defaultValue);
    }
}
