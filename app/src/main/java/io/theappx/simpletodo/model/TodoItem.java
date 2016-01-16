package io.theappx.simpletodo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

import io.theappx.simpletodo.utils.FormatUtils;

public class TodoItem implements Parcelable {
    public static final Parcelable.Creator<TodoItem> CREATOR = new Parcelable.Creator<TodoItem>() {
        public TodoItem createFromParcel(Parcel source) {
            return new TodoItem(source);
        }

        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };
    private String mUniqueId;
    private String mTitle;
    private String mDescription;
    private String mDate;
    private String mTime;
    private boolean shouldRemind;

    public TodoItem() {
        mUniqueId = UUID.randomUUID().toString();
    }

    protected TodoItem(Parcel in) {
        this.mTitle = in.readString();
        this.mDescription = in.readString();
        this.mDate = in.readString();
        this.shouldRemind = in.readByte() != 0;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String pTitle) {
        mTitle = pTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String pDescription) {
        mDescription = pDescription;
    }

    public String getDate() {
        if (shouldRemind) return mDate;
        else throw new IllegalStateException("Reminder not set");
    }

    public void setDate(String pDate) {
        if (shouldRemind) mDate = pDate;
        else throw new IllegalStateException("Reminder not set");
    }

    public String getTime() {
        if (mDate != null) return mDate;
        else throw new NullPointerException("Time not set");
    }

    public void setTime(String pTime) {
        if (shouldRemind) mTime = pTime;
        else throw new IllegalStateException("Reminder not set");
    }

    public Date getCompleteDate() {
        if (mDate != null && mTime != null) {
            String lDateWithTime = mDate + " " + mTime;
            return FormatUtils.getDateFromString(lDateWithTime);
        } else throw new NullPointerException("Date or Time is null");
    }

    public String getId() {
        return mUniqueId;
    }

    public boolean shouldBeReminded() {
        return shouldRemind;
    }

    public void setShouldRemind(boolean pShouldRemind) {
        shouldRemind = pShouldRemind;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTitle);
        dest.writeString(this.mDescription);
        dest.writeString(this.mDate);
        dest.writeByte(shouldRemind ? (byte) 1 : (byte) 0);
    }
}
