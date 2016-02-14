package io.theappx.simpletodo.adapter;

import android.databinding.BindingAdapter;
import android.graphics.Paint;
import android.text.format.DateFormat;
import android.widget.TextView;

import java.util.Date;

import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.utils.FormatUtils;

public class AdapterBindings {
    @BindingAdapter("app:itemDate")
    public static void setDate(TextView textView, TodoItem todoItem) {
        if (todoItem.isRemind()) {
            String dateString = FormatUtils.getCompatDateString(new Date(todoItem.getTime()));
            textView.setText(dateString);
        }
    }

    @BindingAdapter("app:itemTime")
    public static void setTime(TextView textView, TodoItem todoItem) {
        if (todoItem.isRemind()) {
            java.text.DateFormat timeFormat = DateFormat.getTimeFormat(textView.getContext());
            String timeString = timeFormat.format(new Date(todoItem.getTime()));
            textView.setText(timeString);

        }
    }

    @BindingAdapter("app:doneStatus")
    public static void strikeThroughText(TextView textView, boolean done) {
        textView.setPaintFlags(done ? (textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG) :
                (textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)));
    }
}