package io.theappx.simpletodo.customview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.theappx.simpletodo.R;

public class DataLayout extends RelativeLayout {
    private TextView defaultValueTextView;

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
        int drawableId;

        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.Data, 0, 0);
        try {
            dataTitle = typedArray.getString(R.styleable.Data_dataTitle);
            dataDefaultValue = typedArray.getString(R.styleable.Data_dataDefaultValue);

            drawableId = typedArray.getResourceId(R.styleable.Data_dataIcon, 0);
        } finally {
            typedArray.recycle();
        }

        titleTextView.setText(dataTitle);
        defaultValueTextView.setText(dataDefaultValue);
        imageView.setImageResource(drawableId);

        Activity activity = (Activity) context;
        int[] attrs1 = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray1 = activity.obtainStyledAttributes(attrs1);
        int backgroundResource = typedArray1.getResourceId(0, 0);
        typedArray1.recycle();
        setBackgroundResource(backgroundResource);

        int padding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        setPadding(padding, 0, padding, 0);
    }

    public void setDataValue(String defaultValue) {
        defaultValueTextView.setText(defaultValue);
    }
}
